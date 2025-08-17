package com.bookstore.util.queue;

import java.util.ArrayList;
import java.util.List;

/**
 * Array-based implementation of QueueADT
 * Uses circular array for efficient space utilization
 *
 * Characteristics:
 * - Fixed capacity (must be specified at creation)
 * - O(1) enqueue/dequeue operations
 * - Memory efficient for known capacity requirements
 * - Good cache locality due to contiguous memory
 *
 * @param <T> The type of elements stored in the queue
 */
public class ArrayQueue<T> implements QueueADT<T> {
    
    private T[] queue;
    private int front;
    private int rear;
    private int size;
    private final int capacity;
    
    /**
     * Create a queue with specified capacity
     * @param capacity Maximum number of elements the queue can hold
     */
    @SuppressWarnings("unchecked")
    public ArrayQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;
        this.queue = (T[]) new Object[capacity];
        this.front = 0;
        this.rear = -1;
        this.size = 0;
    }
    
    /**
     * Create a queue with default capacity of 100
     */
    public ArrayQueue() {
        this(100);
    }
    
    @Override
    public boolean enqueue(T element) throws QueueFullException {
        if (element == null) {
            throw new IllegalArgumentException("Cannot enqueue null element");
        }
        
        if (isFull()) {
            throw new QueueFullException("Queue has reached maximum capacity: " + capacity);
        }
        
        rear = (rear + 1) % capacity;
        queue[rear] = element;
        size++;
        return true;
    }
    
    @Override
    public T dequeue() throws QueueEmptyException {
        if (isEmpty()) {
            throw new QueueEmptyException("Cannot dequeue from empty queue");
        }
        
        T element = queue[front];
        queue[front] = null; // Help GC
        front = (front + 1) % capacity;
        size--;
        return element;
    }
    
    @Override
    public T peek() throws QueueEmptyException {
        if (isEmpty()) {
            throw new QueueEmptyException("Cannot peek at empty queue");
        }
        
        return queue[front];
    }
    
    @Override
    public boolean isEmpty() {
        return size == 0;
    }
    
    @Override
    public boolean isFull() {
        return size == capacity;
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
        // Help GC by nullifying references
        for (int i = 0; i < capacity; i++) {
            queue[i] = null;
        }
        front = 0;
        rear = -1;
        size = 0;
    }
    
    @Override
    public List<T> toList() {
        List<T> result = new ArrayList<>(size);
        
        if (!isEmpty()) {
            int current = front;
            for (int i = 0; i < size; i++) {
                result.add(queue[current]);
                current = (current + 1) % capacity;
            }
        }
        
        return result;
    }
    
    @Override
    public boolean contains(T element) {
        if (element == null || isEmpty()) {
            return false;
        }
        
        int current = front;
        for (int i = 0; i < size; i++) {
            if (element.equals(queue[current])) {
                return true;
            }
            current = (current + 1) % capacity;
        }
        
        return false;
    }
    
    // Note: getInternalState() method removed as it was only used for debugging and not used in production
    
    @Override
    public String toString() {
        if (isEmpty()) {
            return "ArrayQueue{empty}";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("ArrayQueue{size=").append(size).append(", elements=[");
        
        List<T> elements = toList();
        for (int i = 0; i < elements.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(elements.get(i));
        }
        
        sb.append("]}");
        return sb.toString();
    }
}