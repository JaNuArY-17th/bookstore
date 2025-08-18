package com.bookstore.service;

import com.bookstore.dao.OrderDAO;
import com.bookstore.dao.BookDAO;
import com.bookstore.model.Order;
import com.bookstore.model.OrderItem;
import com.bookstore.model.Book;
import com.bookstore.model.OrderStatus;
import com.bookstore.model.User;
import com.bookstore.model.Role;

import com.bookstore.util.algorithms.SearchingAlgorithms;
import com.bookstore.util.algorithms.SortingAlgorithms;
import com.bookstore.util.queue.OrderQueueManager;
import com.bookstore.service.QueueService;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

public class OrderService {
    private OrderDAO orderDAO;
    private BookDAO bookDAO;
    private QueueService queueService;
    private SessionDataManager sessionManager;

    public OrderService() {
        this.orderDAO = new OrderDAO();
        this.bookDAO = new BookDAO();
        this.queueService = new QueueService();
    }

    public OrderService(SessionDataManager sessionManager) {
        this.orderDAO = new OrderDAO();
        this.bookDAO = new BookDAO();
        this.queueService = new QueueService();
        this.sessionManager = sessionManager;
    }

    // Process new order - Business Logic
    public int createNewOrder(Order order, User currentUser) {
        // 1. Check stock and calculate total amount
        double totalAmount = 0;
        List<OrderItem> itemsToRemove = new ArrayList<>();
        boolean allItemsAvailable = true;

        for (OrderItem item : order.getOrderItems()) {
            Book book = bookDAO.getBookById(item.getBookId());
            if (book == null || book.getStockQuantity() < item.getQuantity()) {
                System.out.println("Book '" + (book != null ? book.getTitle() : "N/A") + "' (ID: " + item.getBookId() + ") is out of stock or insufficient quantity.");
                allItemsAvailable = false;
                itemsToRemove.add(item);
            } else {
                item.setUnitPrice(book.getPrice());
                totalAmount += item.getQuantity() * item.getUnitPrice();
                // Note: Book details available via BookDAO.getBookById(item.getBookId())
            }
        }

        if (!allItemsAvailable) {
            order.getOrderItems().removeAll(itemsToRemove);
            if (order.getOrderItems().isEmpty()) {
                System.out.println("Order cancelled as no items are available.");
                return -1;
            }
            System.out.println("Some items removed due to insufficient stock. Recalculating total.");
            totalAmount = 0;
            for (OrderItem item : order.getOrderItems()) {
                totalAmount += item.getQuantity() * item.getUnitPrice();
            }
        }
        
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING);

        // 2. Save order to database first to get ID
        int orderId = orderDAO.addOrder(order);
        if (orderId == -1) {
            System.err.println("Failed to save order to database");
            return -1;
        }

        order.setOrderId(orderId);

        // 3. Add to session cache if available
        if (sessionManager != null) {
            sessionManager.addOrderToCache(order);
        }

        // 4. Add order to appropriate queues
        boolean addedToQueue = queueService.addOrderToQueue(order, currentUser);
        if (!addedToQueue) {
            System.err.println("Order saved to database but failed to add to queue");
        }

        return orderId;
    }

    // Process orders in queue (called periodically or by a worker thread)
    public void processNextOrderInQueue(User currentUser) {
        if (currentUser == null) {
            System.err.println("User must be logged in to process orders");
            return;
        }

        boolean processed = queueService.processNextOrder(currentUser);
        if (processed) {
            System.out.println("Order processed successfully by " + currentUser.getUsername());
        } else {
            System.out.println("No orders to process or processing failed");
        }
    }

    // Search order by ID
    public Order findOrderById(int orderId) {
        // Use cached data if available, otherwise fall back to database
        List<Order> orders = getCachedOrdersForCurrentUser();
        if (orders == null || orders.isEmpty()) {
            orders = orderDAO.getAllOrders();
        }

        if (orders == null || orders.isEmpty()) {
            return null;
        }

        // Step 2: Sort order list by OrderId
        SortingAlgorithms.quickSort(orders, SearchingAlgorithms.ORDER_ID_COMPARATOR);

        // Step 3: Perform binary search
        return SearchingAlgorithms.binarySearchOrderById(orders, orderId);
    }

    /**
     * Sort orders by specified field and order
     * @param field The field to sort by (order_id, order_date)
     * @param ascending True for ascending order, false for descending
     * @return Sorted list of orders
     */
    public List<Order> sort(String field, boolean ascending) {
        List<Order> orders = getCachedOrdersForCurrentUser();
        if (orders == null || orders.isEmpty()) {
            return new ArrayList<>();
        }

        // Create a copy to avoid modifying the original cached list
        List<Order> sortedOrders = new ArrayList<>(orders);
        Comparator<Order> comparator = getOrderComparator(field);

        if (!ascending) {
            comparator = comparator.reversed();
        }

        SortingAlgorithms.quickSort(sortedOrders, comparator);
        return sortedOrders;
    }

    /**
     * Search orders by search term
     * @param searchTerm The term to search for (order_id, customer_id)
     * @return List of orders matching the search term
     */
    public List<Order> search(String searchTerm) {
        List<Order> orders = getCachedOrdersForCurrentUser();
        if (orders == null || orders.isEmpty()) {
            return new ArrayList<>();
        }

        return SearchingAlgorithms.searchOrders(orders, searchTerm);
    }

    /**
     * Filter orders by status
     * @param status The order status to filter by
     * @param ascending True for ascending order by order_id, false for descending
     * @return Filtered list of orders
     */
    public List<Order> filter(OrderStatus status, boolean ascending) {
        List<Order> orders = getCachedOrdersForCurrentUser();
        if (orders == null || orders.isEmpty()) {
            return new ArrayList<>();
        }

        // Filter by status
        List<Order> filteredOrders = orders.stream()
                .filter(order -> order.getStatus() == status)
                .collect(Collectors.toList());

        // Sort by order_id
        Comparator<Order> comparator = Comparator.comparing(Order::getOrderId);
        if (!ascending) {
            comparator = comparator.reversed();
        }

        SortingAlgorithms.quickSort(filteredOrders, comparator);
        return filteredOrders;
    }

    /**
     * Update order status (admin only)
     * @param orderId The order ID
     * @param newStatus The new status
     * @param user The user performing the operation
     * @return True if update successful, false otherwise
     */
    public boolean updateOrderStatus(int orderId, OrderStatus newStatus, User user) {
        if (!isAdminUser(user)) {
            System.err.println("Access denied: Admin permission required to update order status");
            return false;
        }

        // Update in database
        boolean updated = orderDAO.updateOrderStatus(orderId, newStatus.name());

        if (updated) {
            // Update in cache
            if (sessionManager != null) {
                Order cachedOrder = sessionManager.findOrderInCache(orderId);
                if (cachedOrder != null) {
                    cachedOrder.setStatus(newStatus);
                    sessionManager.updateOrderInCache(cachedOrder);

                    // Update in queues
                    OrderQueueManager.updateOrderInQueues(cachedOrder);
                }
            }
            System.out.println("Order status updated successfully for ID: " + orderId);
        }

        return updated;
    }

    /**
     * Delete order (admin only)
     * @param orderId The order ID
     * @param user The user performing the operation
     * @return True if deletion successful, false otherwise
     */
    public boolean deleteOrder(int orderId, User user) {
        if (!isAdminUser(user)) {
            System.err.println("Access denied: Admin permission required to delete orders");
            return false;
        }

        // Delete from database
        boolean deleted = orderDAO.deleteOrder(orderId);

        if (deleted) {
            // Remove from cache
            removeOrderFromCache(orderId);
            System.out.println("Order deleted successfully with ID: " + orderId);
        }

        return deleted;
    }

    // Queue Management Methods - Delegated to QueueService
    public List<Order> getUserQueueOrders(User user) {
        return queueService.getUserQueueOrders(user);
    }

    public Order peekNextOrder(User user) {
        return queueService.peekNextOrder(user);
    }

    public int getQueueSize(User user) {
        return queueService.getQueueSize(user);
    }

    public OrderQueueManager.UserQueueStatistics getUserQueueStats(User user) {
        return queueService.getUserQueueStats(user);
    }

    public OrderQueueManager.QueueStatistics getOverallQueueStats(User user) {
        return queueService.getOverallQueueStats(user);
    }

    public boolean completeOrder(Order order, User user) {
        return queueService.completeOrder(order, user);
    }

    public boolean cancelOrder(Order order, User user, String reason) {
        return queueService.cancelOrder(order, user, reason);
    }

    public void initializeQueues(User currentUser) {
        queueService.loadExistingOrdersIntoQueues(currentUser);
    }

    public List<Order> getPendingOrders(User user) {
        return queueService.getPendingOrders(user);
    }

    public List<Order> getCompletedOrders(User user) {
        return queueService.getCompletedOrders(user);
    }

    public boolean clearUserQueue(int userId, User admin) {
        return queueService.clearUserQueue(userId, admin);
    }

    public boolean clearAllQueues(User admin) {
        return queueService.clearAllQueues(admin);
    }

    public boolean hasOrdersInQueue(User user) {
        return queueService.hasOrdersInQueue(user);
    }

    // Helper methods

    /**
     * Get cached orders for current user
     */
    private List<Order> getCachedOrdersForCurrentUser() {
        if (sessionManager != null) {
            return sessionManager.getCachedOrders();
        }
        return new ArrayList<>();
    }

    /**
     * Check if user is admin
     */
    private boolean isAdminUser(User user) {
        return user != null && user.getRole() == Role.ADMIN;
    }

    /**
     * Get comparator for order sorting
     */
    private Comparator<Order> getOrderComparator(String field) {
        switch (field.toLowerCase()) {
            case "order_id":
                return Comparator.comparing(Order::getOrderId);
            case "order_date":
                return Comparator.comparing(Order::getOrderDate);
            case "total_amount":
                return Comparator.comparing(Order::getTotalAmount);
            case "customer_id":
                return Comparator.comparing(Order::getCustomerId);
            default:
                throw new IllegalArgumentException("Invalid sort field: " + field);
        }
    }

    /**
     * Remove order from cache
     */
    private void removeOrderFromCache(int orderId) {
        if (sessionManager != null) {
            List<Order> orders = sessionManager.getCachedOrders();
            if (orders != null) {
                orders.removeIf(order -> order.getOrderId() == orderId);
            }
        }
    }
}