package com.bookstore.util.queue;

/**
 * Exception thrown when attempting to add elements to a queue
 * that has reached its maximum capacity
 */
public class QueueFullException extends Exception {
    
    public QueueFullException() {
        super("Queue is full");
    }
    
    public QueueFullException(String message) {
        super(message);
    }
    
    public QueueFullException(String message, Throwable cause) {
        super(message, cause);
    }
}