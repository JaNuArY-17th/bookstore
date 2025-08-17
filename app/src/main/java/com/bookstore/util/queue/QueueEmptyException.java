package com.bookstore.util.queue;

/**
 * Exception thrown when attempting to perform operations on an empty queue
 * that require at least one element (dequeue, peek)
 */
public class QueueEmptyException extends Exception {
    
    public QueueEmptyException() {
        super("Queue is empty");
    }
    
    public QueueEmptyException(String message) {
        super(message);
    }
    
    public QueueEmptyException(String message, Throwable cause) {
        super(message, cause);
    }
}