package com.bookstore.util.queue;

import java.util.List;

/**
 * Generic Queue Abstract Data Type interface
 * Defines the contract for queue operations
 * 
 * @param <T> The type of elements stored in the queue
 */
public interface QueueADT<T> {
    
    /**
     * Add an element to the rear of the queue
     * @param element The element to add
     * @return true if the element was successfully added, false otherwise
     * @throws QueueFullException if the queue is at maximum capacity
     */
    boolean enqueue(T element) throws QueueFullException;
    
    /**
     * Remove and return the element at the front of the queue
     * @return The element at the front of the queue
     * @throws QueueEmptyException if the queue is empty
     */
    T dequeue() throws QueueEmptyException;
    
    /**
     * Return the element at the front of the queue without removing it
     * @return The element at the front of the queue
     * @throws QueueEmptyException if the queue is empty
     */
    T peek() throws QueueEmptyException;
    
    /**
     * Check if the queue is empty
     * @return true if the queue is empty, false otherwise
     */
    boolean isEmpty();
    
    /**
     * Check if the queue is full
     * @return true if the queue is full, false otherwise
     */
    boolean isFull();
    
    /**
     * Get the current number of elements in the queue
     * @return The number of elements in the queue
     */
    int size();
    
    /**
     * Get the maximum capacity of the queue
     * @return The maximum capacity, or -1 if unlimited
     */
    int capacity();
    
    /**
     * Clear all elements from the queue
     */
    void clear();
    
    /**
     * Get all elements in the queue as a list (for inspection purposes)
     * The order should reflect the queue order (front to rear)
     * @return A list containing all queue elements
     */
    List<T> toList();
    
    /**
     * Get queue utilization as a percentage
     * @return Utilization percentage (0.0 to 1.0), or -1.0 if unlimited capacity
     */
    default double getUtilization() {
        if (capacity() <= 0) {
            return -1.0; // Unlimited capacity
        }
        return (double) size() / capacity();
    }
    
    /**
     * Check if the queue contains a specific element
     * @param element The element to search for
     * @return true if the element is found, false otherwise
     */
    boolean contains(T element);
}