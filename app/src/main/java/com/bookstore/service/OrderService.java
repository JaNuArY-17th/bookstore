package com.bookstore.service;

import com.bookstore.dao.OrderDAO; 
import com.bookstore.dao.BookDAO; 
import com.bookstore.model.Order; 
import com.bookstore.model.OrderItem; 
import com.bookstore.model.Book; 
import com.bookstore.model.OrderStatus; 
import com.bookstore.model.User;

import com.bookstore.util.algorithms.SearchingAlgorithms;
import com.bookstore.util.queue.OrderQueueManager;
import com.bookstore.service.QueueService; 

import java.util.List;
import java.util.ArrayList;

public class OrderService {
    private OrderDAO orderDAO;
    private BookDAO bookDAO;
    private QueueService queueService;

    public OrderService() {
        this.orderDAO = new OrderDAO();
        this.bookDAO = new BookDAO();
        this.queueService = new QueueService();
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

        // 3. Add order to appropriate queues
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

    // Note: Legacy processNextOrderInQueue() method removed. Use processNextOrderInQueue(User) instead.

    // Search order by ID
    public Order findOrderById(int orderId) {
        // In a real system, you would prefer to query directly from DB using SQL for ID search
        // because order_id is a primary key and is often indexed, which is very efficient.
        // However, to illustrate Binary Search on a memory dataset:

        // Step 1: Get all orders from database.
        // (Need getAllOrders() method in OrderDAO)
        List<Order> allOrdersFromDb = orderDAO.getAllOrders();

        if (allOrdersFromDb == null || allOrdersFromDb.isEmpty()) {
            return null;
        }
        
        // Step 2: Sort order list by OrderId (if not already sorted)
        // Database Index usually works better for this purpose in a real system.
        // This sorting is only for demonstration of Binary Search on a List in Java.
        allOrdersFromDb.sort(SearchingAlgorithms.ORDER_ID_COMPARATOR);

        // Step 3: Perform binary search
        return SearchingAlgorithms.binarySearchOrderById(allOrdersFromDb, orderId);
    }

    // Queue Management Methods - Delegated to QueueService
    // Note: These methods provide a unified interface for order and queue operations
    // All queue-specific logic is handled by QueueService to maintain separation of concerns

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
}