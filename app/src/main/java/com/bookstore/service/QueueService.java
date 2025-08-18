package com.bookstore.service;

import com.bookstore.model.Order;
import com.bookstore.model.User;
import com.bookstore.model.Role;
import com.bookstore.model.OrderStatus;
import com.bookstore.dao.OrderDAO;
import com.bookstore.util.queue.OrderQueueManager;

import java.util.List;
import java.util.ArrayList;

/**
 * Service layer for queue operations
 * Handles business logic for order queue management
 */
public class QueueService {
    
    private OrderDAO orderDAO;
    
    public QueueService() {
        this.orderDAO = new OrderDAO();
    }
    
    /**
     * Add new order to appropriate queues
     */
    public boolean addOrderToQueue(Order order, User currentUser) {
        if (order == null) {
            System.err.println("Cannot add null order to queue");
            return false;
        }
        
        // Set user who created the order
        if (currentUser != null) {
            order.setUserId(currentUser.getUserId());
        }
        
        // Add to queue management system
        boolean success = OrderQueueManager.addOrderToQueues(order, currentUser);
        
        if (success) {
            System.out.println("Order " + order.getOrderId() + " added to queue successfully");
            logQueueOperation("ADD", order, currentUser);
        } else {
            System.err.println("Failed to add order " + order.getOrderId() + " to queue");
        }
        
        return success;
    }
    
    /**
     * Get next order for processing (role-based)
     */
    public Order getNextOrder(User user) {
        if (user == null) {
            System.err.println("User cannot be null");
            return null;
        }
        
        Order order = OrderQueueManager.getNextOrderForUser(user);
        
        if (order != null) {
            logQueueOperation("DEQUEUE", order, user);
            System.out.println("Retrieved order " + order.getOrderId() + " for user " + user.getUsername());
        } else {
            System.out.println("No orders available in queue for user " + user.getUsername());
        }
        
        return order;
    }
    
    /**
     * Peek at next order without removing it
     */
    public Order peekNextOrder(User user) {
        if (user == null) {
            return null;
        }
        
        return OrderQueueManager.peekNextOrderForUser(user);
    }
    
    /**
     * Get all orders in user's queue
     */
    public List<Order> getUserQueueOrders(User user) {
        if (user == null) {
            return new ArrayList<>();
        }
        
        return OrderQueueManager.getUserOrdersList(user);
    }
    
    /**
     * Process next order in queue
     */
    public boolean processNextOrder(User user) {
        if (user == null) {
            System.err.println("User cannot be null for order processing");
            return false;
        }
        
        Order order = getNextOrder(user);
        if (order == null) {
            System.out.println("No orders to process in queue");
            return false;
        }
        
        try {
            // Update order status to PROCESSING
            order.setStatus(OrderStatus.PROCESSING);
            
            // Update in database
            boolean updated = orderDAO.updateOrderStatus(order.getOrderId(), "PROCESSING");
            
            if (updated) {
                // Update in queue system
                OrderQueueManager.updateOrderInQueues(order);
                
                // Remove from pending queue
                OrderQueueManager.removeFromPending(order);
                
                System.out.println("Order " + order.getOrderId() + " is now being processed by " + user.getUsername());
                logQueueOperation("PROCESS", order, user);
                
                return true;
            } else {
                System.err.println("Failed to update order status in database");
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("Error processing order: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Complete order processing
     */
    public boolean completeOrder(Order order, User user) {
        if (order == null || user == null) {
            return false;
        }
        
        try {
            // Update order status
            order.setStatus(OrderStatus.DELIVERED);
            
            // Update in database
            boolean updated = orderDAO.updateOrderStatus(order.getOrderId(), "DELIVERED");
            
            if (updated) {
                // Move to completed queue
                OrderQueueManager.moveToCompleted(order);
                
                System.out.println("Order " + order.getOrderId() + " completed by " + user.getUsername());
                logQueueOperation("COMPLETE", order, user);
                
                return true;
            }
            
        } catch (Exception e) {
            System.err.println("Error completing order: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Cancel order
     */
    public boolean cancelOrder(Order order, User user, String reason) {
        if (order == null || user == null) {
            return false;
        }
        
        try {
            // Update order status
            order.setStatus(OrderStatus.CANCELLED);
            if (reason != null && !reason.trim().isEmpty()) {
                order.setNotes(order.getNotes() + "\nCancellation reason: " + reason);
            }
            
            // Update in database
            boolean updated = orderDAO.updateOrderStatus(order.getOrderId(), "CANCELLED");
            
            if (updated) {
                // Move to completed queue
                OrderQueueManager.moveToCompleted(order);
                
                System.out.println("Order " + order.getOrderId() + " cancelled by " + user.getUsername());
                logQueueOperation("CANCEL", order, user);
                
                return true;
            }
            
        } catch (Exception e) {
            System.err.println("Error cancelling order: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Get queue statistics for user
     */
    public OrderQueueManager.UserQueueStatistics getUserQueueStats(User user) {
        return OrderQueueManager.getUserQueueStatistics(user);
    }
    
    /**
     * Get overall queue statistics (admin only)
     */
    public OrderQueueManager.QueueStatistics getOverallQueueStats(User user) {
        if (!isAdmin(user)) {
            throw new SecurityException("Admin access required for overall queue statistics");
        }
        
        return OrderQueueManager.getQueueStatistics();
    }
    
    /**
     * Get queue size for user
     */
    public int getQueueSize(User user) {
        return OrderQueueManager.getQueueSizeForUser(user);
    }
    
    /**
     * Check if user has orders in queue
     */
    public boolean hasOrdersInQueue(User user) {
        return getQueueSize(user) > 0;
    }
    
    /**
     * Load existing orders into queues (initialization)
     */
    public void loadExistingOrdersIntoQueues(User currentUser) {
        try {
            // Load all pending and processing orders
            List<Order> allOrders = orderDAO.getAllOrders();
            
            for (Order order : allOrders) {
                String status = order.getStatus().name();
                if ("PENDING".equals(status) || "PROCESSING".equals(status)) {
                    OrderQueueManager.addOrderToQueues(order, currentUser);
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error loading existing orders into queues: " + e.getMessage());
        }
    }
    
    /**
     * Clear user's queue (admin operation)
     */
    public boolean clearUserQueue(int userId, User admin) {
        if (!isAdmin(admin)) {
            System.err.println("Admin access required to clear user queues");
            return false;
        }
        
        OrderQueueManager.clearUserQueue(userId);
        
        return true;
    }
    
    /**
     * Clear all queues (admin operation)
     */
    public boolean clearAllQueues(User admin) {
        if (!isAdmin(admin)) {
            System.err.println("Admin access required to clear all queues");
            return false;
        }
        
        OrderQueueManager.clearAllQueues();
        
        return true;
    }
    
    /**
     * Get pending orders (admin view)
     */
    public List<Order> getPendingOrders(User user) {
        if (!isAdmin(user)) {
            throw new SecurityException("Admin access required for pending orders view");
        }
        
        return OrderQueueManager.getPendingQueue().toList();
    }
    
    /**
     * Get completed orders (admin view)
     */
    public List<Order> getCompletedOrders(User user) {
        if (!isAdmin(user)) {
            throw new SecurityException("Admin access required for completed orders view");
        }
        
        return OrderQueueManager.getCompletedQueue().toList();
    }
    
    // Helper methods
    private boolean isAdmin(User user) {
        return user != null && user.getRole() == Role.ADMIN;
    }

    private void logQueueOperation(String operation, Order order, User user) {
        String logMessage = String.format("[QUEUE] %s - Order: %d, User: %s (%s), Status: %s",
            operation,
            order.getOrderId(),
            user.getUsername(),
            user.getRole(),
            order.getStatus()
        );

        System.out.println(logMessage);
    }
}