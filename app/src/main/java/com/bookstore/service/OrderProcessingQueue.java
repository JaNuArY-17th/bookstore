package com.bookstore.service;

import com.bookstore.model.Order;
import com.bookstore.util.queue.LinkedQueue;
import com.bookstore.util.queue.QueueADT;
import com.bookstore.util.queue.QueueEmptyException;
import com.bookstore.util.queue.QueueFullException;

/**
 * Enhanced Order Processing Queue using proper Queue ADT
 * Replaces the simple LinkedList implementation with a proper queue structure
 */
public class OrderProcessingQueue {
    // Main processing queue using our Queue ADT
    private static final QueueADT<Order> processingQueue = new LinkedQueue<>(1000);
    
    // Legacy support - keep the old interface but use new implementation
    
    /**
     * Add order to processing queue
     * @param order The order to add
     * @return true if successfully added, false otherwise
     */
    public static boolean addOrderToQueue(Order order) {
        if (order == null) {
            System.err.println("Cannot add null order to queue");
            return false;
        }
        
        try {
            boolean added = processingQueue.enqueue(order);
            if (added) {
                System.out.println("Order " + order.getOrderId() + 
                                 " added to processing queue. Queue size: " + processingQueue.size());
            }
            return added;
        } catch (QueueFullException e) {
            System.err.println("Processing queue is full: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get next order to process (removes from queue)
     * @return The next order, or null if queue is empty
     */
    public static Order getNextOrderToProcess() {
        try {
            Order order = processingQueue.dequeue();
            if (order != null) {
                System.out.println("Retrieved order " + order.getOrderId() + 
                                 " from processing queue. Remaining: " + processingQueue.size());
            }
            return order;
        } catch (QueueEmptyException e) {
            return null; // Queue is empty
        }
    }
    
    /**
     * Peek at the next order without removing it
     * @return The next order, or null if queue is empty
     */
    public static Order peekNextOrder() {
        try {
            return processingQueue.peek();
        } catch (QueueEmptyException e) {
            return null; // Queue is empty
        }
    }

    /**
     * Check if queue is empty
     * @return true if empty, false otherwise
     */
    public static boolean isQueueEmpty() {
        return processingQueue.isEmpty();
    }

    /**
     * Get current queue size
     * @return Number of orders in queue
     */
    public static int getQueueSize() {
        return processingQueue.size();
    }
    
    /**
     * Get queue capacity
     * @return Maximum queue capacity
     */
    public static int getQueueCapacity() {
        return processingQueue.capacity();
    }
    
    /**
     * Check if queue is full
     * @return true if full, false otherwise
     */
    public static boolean isQueueFull() {
        return processingQueue.isFull();
    }
    
    /**
     * Get queue utilization percentage
     * @return Utilization as a value between 0.0 and 1.0
     */
    public static double getQueueUtilization() {
        return processingQueue.getUtilization();
    }
    
    /**
     * Clear all orders from the queue
     */
    public static void clearQueue() {
        processingQueue.clear();
        System.out.println("Processing queue cleared");
    }
    
    /**
     * Check if a specific order is in the queue
     * @param order The order to search for
     * @return true if found, false otherwise
     */
    public static boolean containsOrder(Order order) {
        return processingQueue.contains(order);
    }
    
    /**
     * Get detailed queue information
     * @return String representation of queue status
     */
    public static String getQueueInfo() {
        return String.format("ProcessingQueue{size=%d/%d, utilization=%.2f%%, empty=%s, full=%s}",
                           processingQueue.size(), 
                           processingQueue.capacity(),
                           processingQueue.getUtilization() * 100,
                           processingQueue.isEmpty(),
                           processingQueue.isFull());
    }
    
    /**
     * Get the underlying queue implementation (for advanced operations)
     * @return The QueueADT implementation
     */
    public static QueueADT<Order> getQueue() {
        return processingQueue;
    }
}
