package com.bookstore.util.queue;

import com.bookstore.model.Order;
import com.bookstore.model.User;
import com.bookstore.model.Role;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Centralized Order Queue Manager
 * Manages separate queues for users and admins with proper access control
 */
public class OrderQueueManager {
    
    // Global admin queue for all orders
    private static final QueueADT<Order> adminQueue = new LinkedQueue<>(1000);
    
    // User-specific queues (userId -> user's order queue)
    private static final Map<Integer, QueueADT<Order>> userQueues = new ConcurrentHashMap<>();
    
    // Queue for pending orders (before assignment to users)
    private static final QueueADT<Order> pendingQueue = new LinkedQueue<>(500);
    
    // Queue for completed orders (for history/reporting)
    private static final QueueADT<Order> completedQueue = new LinkedQueue<>(2000);
    
    // Comparators for different queue orderings
    private static final Comparator<Order> ORDER_DATE_COMPARATOR = 
        Comparator.comparing(Order::getOrderDate);
    
    private static final Comparator<Order> ORDER_PRIORITY_COMPARATOR = 
        (o1, o2) -> {
            // Priority: PENDING > PROCESSING > SHIPPED > DELIVERED > CANCELLED
            int priority1 = getOrderPriority(o1);
            int priority2 = getOrderPriority(o2);
            if (priority1 != priority2) {
                return Integer.compare(priority1, priority2);
            }
            // If same priority, order by date (oldest first)
            return o1.getOrderDate().compareTo(o2.getOrderDate());
        };
    
    /**
     * Add order to appropriate queues based on user role and order status
     */
    public static boolean addOrderToQueues(Order order, User currentUser) {
        if (order == null) {
            return false;
        }
        
        try {
            // Always add to admin queue (admins see all orders)
            adminQueue.enqueue(order);
            
            // Add to user-specific queue if user exists
            if (currentUser != null) {
                QueueADT<Order> userQueue = getUserQueue(currentUser.getUserId());
                
                // Users only see their own orders, admins see all
                if (currentUser.getRole() == Role.ADMIN || 
                    order.getCustomerId() == currentUser.getUserId()) {
                    userQueue.enqueue(order);
                }
            }
            
            // Add to pending queue if order is pending
            if (order.getStatus().name().equals("PENDING")) {
                pendingQueue.enqueue(order);
            }
            
            return true;
            
        } catch (QueueFullException e) {
            System.err.println("Queue is full: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get user-specific queue (creates if doesn't exist)
     */
    public static QueueADT<Order> getUserQueue(int userId) {
        return userQueues.computeIfAbsent(userId, k -> new LinkedQueue<>(100));
    }
    
    /**
     * Get admin queue (all orders)
     */
    public static QueueADT<Order> getAdminQueue() {
        return adminQueue;
    }
    
    /**
     * Get pending orders queue
     */
    public static QueueADT<Order> getPendingQueue() {
        return pendingQueue;
    }
    
    /**
     * Get completed orders queue
     */
    public static QueueADT<Order> getCompletedQueue() {
        return completedQueue;
    }
    
    /**
     * Get next order for user (based on their role)
     */
    public static Order getNextOrderForUser(User user) {
        if (user == null) {
            return null;
        }
        
        try {
            if (user.getRole() == Role.ADMIN) {
                // Admins get from admin queue (all orders)
                return adminQueue.dequeue();
            } else {
                // Users get from their personal queue
                QueueADT<Order> userQueue = getUserQueue(user.getUserId());
                return userQueue.dequeue();
            }
        } catch (QueueEmptyException e) {
            return null; // Queue is empty
        }
    }
    
    /**
     * Peek at next order for user without removing it
     */
    public static Order peekNextOrderForUser(User user) {
        if (user == null) {
            return null;
        }
        
        try {
            if (user.getRole() == Role.ADMIN) {
                return adminQueue.peek();
            } else {
                QueueADT<Order> userQueue = getUserQueue(user.getUserId());
                return userQueue.peek();
            }
        } catch (QueueEmptyException e) {
            return null;
        }
    }
    
    /**
     * Get all orders in user's queue as a list
     */
    public static List<Order> getUserOrdersList(User user) {
        if (user == null) {
            return new ArrayList<>();
        }
        
        if (user.getRole() == Role.ADMIN) {
            return adminQueue.toList();
        } else {
            QueueADT<Order> userQueue = getUserQueue(user.getUserId());
            return userQueue.toList();
        }
    }
    
    /**
     * Get queue size for user
     */
    public static int getQueueSizeForUser(User user) {
        if (user == null) {
            return 0;
        }
        
        if (user.getRole() == Role.ADMIN) {
            return adminQueue.size();
        } else {
            QueueADT<Order> userQueue = getUserQueue(user.getUserId());
            return userQueue.size();
        }
    }
    
    /**
     * Move order to completed queue
     */
    public static boolean moveToCompleted(Order order) {
        if (order == null) {
            return false;
        }
        
        try {
            completedQueue.enqueue(order);
            
            // Remove from pending queue if it's there
            removeFromQueue(pendingQueue, order);
            
            return true;
        } catch (QueueFullException e) {
            System.err.println("Completed queue is full: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Remove order from pending queue (when it's processed)
     */
    public static boolean removeFromPending(Order order) {
        return removeFromQueue(pendingQueue, order);
    }
    
    /**
     * Update order status in all relevant queues
     */
    public static void updateOrderInQueues(Order updatedOrder) {
        // This is a simplified approach - in a real system you might want
        // to implement a more sophisticated update mechanism
        
        // For now, we'll rely on the fact that Order objects are references
        // and updates to the order object will be reflected in the queues
        
        // If order is completed, move to completed queue
        if (isOrderCompleted(updatedOrder)) {
            moveToCompleted(updatedOrder);
        }
    }
    
    /**
     * Clear all queues (admin operation)
     */
    public static void clearAllQueues() {
        adminQueue.clear();
        pendingQueue.clear();
        completedQueue.clear();
        userQueues.clear();
    }
    
    /**
     * Clear user-specific queue
     */
    public static void clearUserQueue(int userId) {
        QueueADT<Order> userQueue = userQueues.get(userId);
        if (userQueue != null) {
            userQueue.clear();
        }
    }
    
    /**
     * Get comprehensive queue statistics
     */
    public static QueueStatistics getQueueStatistics() {
        return new QueueStatistics(
            adminQueue.size(),
            pendingQueue.size(),
            completedQueue.size(),
            userQueues.size(),
            userQueues.values().stream().mapToInt(QueueADT::size).sum()
        );
    }
    
    /**
     * Get queue statistics for specific user
     */
    public static UserQueueStatistics getUserQueueStatistics(User user) {
        if (user == null) {
            return new UserQueueStatistics(0, 0, 0);
        }
        
        int userQueueSize = getQueueSizeForUser(user);
        int pendingCount = 0;
        int processingCount = 0;
        
        List<Order> userOrders = getUserOrdersList(user);
        for (Order order : userOrders) {
            switch (order.getStatus().name()) {
                case "PENDING":
                    pendingCount++;
                    break;
                case "PROCESSING":
                    processingCount++;
                    break;
            }
        }
        
        return new UserQueueStatistics(userQueueSize, pendingCount, processingCount);
    }
    
    // Helper methods
    
    private static int getOrderPriority(Order order) {
        switch (order.getStatus().name()) {
            case "PENDING": return 1;
            case "PROCESSING": return 2;
            case "SHIPPED": return 3;
            case "DELIVERED": return 4;
            case "CANCELLED": return 5;
            default: return 6;
        }
    }
    
    private static boolean isOrderCompleted(Order order) {
        String status = order.getStatus().name();
        return "DELIVERED".equals(status) || "CANCELLED".equals(status);
    }
    
    private static boolean removeFromQueue(QueueADT<Order> queue, Order order) {
        // This is a simplified removal - in a real implementation,
        // you might want a more efficient removal mechanism
        List<Order> tempList = new ArrayList<>();
        boolean found = false;
        
        try {
            // Dequeue all items
            while (!queue.isEmpty()) {
                Order current = queue.dequeue();
                if (!found && current.getOrderId() == order.getOrderId()) {
                    found = true; // Skip this order (effectively removing it)
                } else {
                    tempList.add(current);
                }
            }
            
            // Re-enqueue remaining items
            for (Order o : tempList) {
                queue.enqueue(o);
            }
            
        } catch (QueueEmptyException | QueueFullException e) {
            System.err.println("Error removing order from queue: " + e.getMessage());
        }
        
        return found;
    }
    
    /**
     * Statistics class for overall queue information
     */
    public static class QueueStatistics {
        private final int adminQueueSize;
        private final int pendingQueueSize;
        private final int completedQueueSize;
        private final int activeUserQueues;
        private final int totalUserQueueItems;
        
        public QueueStatistics(int adminQueueSize, int pendingQueueSize, 
                             int completedQueueSize, int activeUserQueues, 
                             int totalUserQueueItems) {
            this.adminQueueSize = adminQueueSize;
            this.pendingQueueSize = pendingQueueSize;
            this.completedQueueSize = completedQueueSize;
            this.activeUserQueues = activeUserQueues;
            this.totalUserQueueItems = totalUserQueueItems;
        }
        
        // Getters
        public int getAdminQueueSize() { return adminQueueSize; }
        public int getPendingQueueSize() { return pendingQueueSize; }
        public int getCompletedQueueSize() { return completedQueueSize; }
        public int getActiveUserQueues() { return activeUserQueues; }
        public int getTotalUserQueueItems() { return totalUserQueueItems; }
        
        @Override
        public String toString() {
            return String.format(
                "QueueStats{admin=%d, pending=%d, completed=%d, userQueues=%d, userItems=%d}",
                adminQueueSize, pendingQueueSize, completedQueueSize, 
                activeUserQueues, totalUserQueueItems
            );
        }
    }
    
    /**
     * Statistics class for user-specific queue information
     */
    public static class UserQueueStatistics {
        private final int totalOrders;
        private final int pendingOrders;
        private final int processingOrders;
        
        public UserQueueStatistics(int totalOrders, int pendingOrders, int processingOrders) {
            this.totalOrders = totalOrders;
            this.pendingOrders = pendingOrders;
            this.processingOrders = processingOrders;
        }
        
        // Getters
        public int getTotalOrders() { return totalOrders; }
        public int getPendingOrders() { return pendingOrders; }
        public int getProcessingOrders() { return processingOrders; }
        
        @Override
        public String toString() {
            return String.format(
                "UserQueueStats{total=%d, pending=%d, processing=%d}",
                totalOrders, pendingOrders, processingOrders
            );
        }
    }
}