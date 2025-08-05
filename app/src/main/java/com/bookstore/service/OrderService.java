package com.bookstore.service;

import com.bookstore.dao.OrderDAO; 
import com.bookstore.dao.BookDAO; 
import com.bookstore.model.Order; 
import com.bookstore.model.OrderItem; 
import com.bookstore.model.Book; 
import com.bookstore.model.OrderStatus; 
import com.bookstore.util.algorithms.SortingAlgorithms; 
import com.bookstore.util.algorithms.SearchingAlgorithms; 

import java.util.List;
import java.util.ArrayList;

public class OrderService {
    private OrderDAO orderDAO;
    private BookDAO bookDAO;

    public OrderService() {
        this.orderDAO = new OrderDAO();
        this.bookDAO = new BookDAO();
    }

    // Process new order - Business Logic
    public int createNewOrder(Order order) {
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
                item.setBook(book); // Link OrderItem with full Book object
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

        // 2. Add order to processing queue
        // Assign a temporary orderId if needed before adding to queue, or let DAO assign after persist
        // For simplicity, we'll let DAO assign ID after persist
        OrderProcessingQueue.addOrderToQueue(order);
        return order.getOrderId(); // Will be 0 or default value if not assigned
    }

    // Process orders in queue (called periodically or by a worker thread)
    public void processNextOrderInQueue() {
        Order orderToProcess = OrderProcessingQueue.getNextOrderToProcess();
        if (orderToProcess != null) {
            System.out.println("Processing order: " + orderToProcess.getOrderId());

            // 1. Sort book list in order (e.g. by title)
            if (orderToProcess.getOrderItems() != null && !orderToProcess.getOrderItems().isEmpty()) {
                SortingAlgorithms.quickSort(orderToProcess.getOrderItems(), SortingAlgorithms.BOOK_TITLE_COMPARATOR);
                System.out.println("Order items sorted by title.");
            }

            // 2. Save order to Database and update stock
            int persistedOrderId = orderDAO.addOrder(orderToProcess);
            if (persistedOrderId != -1) {
                System.out.println("Order " + persistedOrderId + " successfully persisted to database."); 
                // Update stock
                for (OrderItem item : orderToProcess.getOrderItems()) {
                    bookDAO.updateBookStock(item.getBookId(), -item.getQuantity());
                }
                orderDAO.updateOrderStatus(persistedOrderId, OrderStatus.PROCESSING.name());
                System.out.println("Stock updated and order status set to PROCESSING.");
            } else {
                System.err.println("Failed to persist order " + orderToProcess.getOrderId());
            }
        } else {
            System.out.println("Order processing queue is empty.");
        }
    }

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
}