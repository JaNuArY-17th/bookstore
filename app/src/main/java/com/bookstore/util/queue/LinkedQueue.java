package com.bookstore.util.queue;

import java.util.ArrayList;
import java.util.List;

/**
 * Linked list-based implementation of QueueADT
 * Provides dynamic sizing with optional capacity limits
 *
 * Characteristics:
 * - Dynamic capacity (can grow as needed)
 * - O(1) enqueue/dequeue operations
 * - Memory allocated per element (no wasted space)
 * - Preferred implementation for production use
 *
 * @param <T> The type of elements stored in the queue
 */
public class LinkedQueue<T> implements QueueADT<T> {
    
    /**
     * Node class for the linked list implementation
     */
    private static class Node<T> {
        T data;
        Node<T> next;
        
        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }
    
    private Node<T> front;
    private Node<T> rear;
    private int size;
    private final int capacity;
    
    /**
     * Create a queue with specified maximum capacity
     * @param capacity Maximum number of elements (-1 for unlimited)
     */
    public LinkedQueue(int capacity) {
        if (capacity < -1 || capacity == 0) {
            throw new IllegalArgumentException("Capacity must be positive or -1 for unlimited");
        }
        this.capacity = capacity;
        this.front = null;
        this.rear = null;
        this.size = 0;
    }
    
    /**
     * Create a queue with unlimited capacity
     */
    public LinkedQueue() {
        this(-1);
    }
    
    @Override
    public boolean enqueue(T element) throws QueueFullException {
        if (element == null) {
            throw new IllegalArgumentException("Cannot enqueue null element");
        }
        
        if (isFull()) {
            throw new QueueFullException("Queue has reached maximum capacity: " + capacity);
        }
        
        Node<T> newNode = new Node<>(element);
        
        if (isEmpty()) {
            front = rear = newNode;
        } else {
            rear.next = newNode;
            rear = newNode;
        }
        
        size++;
        return true;
    }
    
    @Override
    public T dequeue() throws QueueEmptyException {
        if (isEmpty()) {
            throw new QueueEmptyException("Cannot dequeue from empty queue");
        }
        
        T element = front.data;
        front = front.next;
        
        if (front == null) {
            rear = null; // Queue is now empty
        }
        
        size--;
        return element;
    }
    
    @Override
    public T peek() throws QueueEmptyException {
        if (isEmpty()) {
            throw new QueueEmptyException("Cannot peek at empty queue");
        }
        
        return front.data;
    }
    
    @Override
    public boolean isEmpty() {
        return front == null;
    }
    
    @Override
    public boolean isFull() {
        return capacity > 0 && size >= capacity;
    }
    
    @Override
    public int size() {
        return size;
    }
    
    @Override
    public int capacity() {
        return capacity;
    }
    
    @Override
    public void clear() {
        // Help GC by breaking the chain
        while (front != null) {
            Node<T> temp = front;
            front = front.next;
            temp.data = null;
            temp.next = null;
        }
        rear = null;
        size = 0;
    }
    
    @Override
    public List<T> toList() {
        List<T> result = new ArrayList<>(size);
        
        Node<T> current = front;
        while (current != null) {
            result.add(current.data);
            current = current.next;
        }
        
        return result;
    }
    
    @Override
    public boolean contains(T element) {
        if (element == null || isEmpty()) {
            return false;
        }
        
        Node<T> current = front;
        while (current != null) {
            if (element.equals(current.data)) {
                return true;
            }
            current = current.next;
        }
        
        return false;
    }
    
    // Note: peekRear() method removed as it was not used anywhere in the codebase
    
    @Override
    public String toString() {
        if (isEmpty()) {
            return "LinkedQueue{empty}";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("LinkedQueue{size=").append(size);
        if (capacity > 0) {
            sb.append("/").append(capacity);
        }
        sb.append(", elements=[");
        
        Node<T> current = front;
        boolean first = true;
        while (current != null) {
            if (!first) sb.append(", ");
            sb.append(current.data);
            current = current.next;
            first = false;
        }
        
        sb.append("]}");
        return sb.toString();
    }
}