package com.bookstore.service;

import java.util.LinkedList;
import java.util.Queue;

import com.bookstore.model.Order;

public class OrderProcessingQueue {
    // Queue to store new orders
    private static Queue<Order> newOrdersQueue = new LinkedList<>();

    // Add order to queue
    public static void addOrderToQueue(Order order) {
        newOrdersQueue.offer(order);
        System.out.println("Order " + order.getOrderId() + " added to processing queue. Queue size: " + newOrdersQueue.size());
    }

    // Get next order to process
    public static Order getNextOrderToProcess() {
        return newOrdersQueue.poll();
    }

    // Check if queue is empty
    public static boolean isQueueEmpty() {
        return newOrdersQueue.isEmpty();
    }

    // Get queue size
    public static int getQueueSize() {
        return newOrdersQueue.size();
    }
}
