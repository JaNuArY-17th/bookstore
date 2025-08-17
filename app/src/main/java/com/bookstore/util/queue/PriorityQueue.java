package com.bookstore.util.queue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Priority queue implementation using a binary heap
 * Elements are dequeued based on priority rather than insertion order
 * 
 * @param <T> The type of elements stored in the queue
 */
public class PriorityQueue<T> implements QueueADT<T> {
    
    private List<T> heap;
    private final Comparator<T> comparator;
    private final int capacity;
    
    /**
     * Create a priority queue with specified capacity and comparator
     * @param capacity Maximum number of elements (-1 for unlimited)
     * @param comparator Comparator to determine priority (smaller values have higher priority)
     */
    public PriorityQueue(int capacity, Comparator<T> comparator) {
        if (capacity < -1 || capacity == 0) {
            throw new IllegalArgumentException("Capacity must be positive or -1 for unlimited");
        }
        if (comparator == null) {
            throw new IllegalArgumentException("Comparator cannot be null");
        }
        
        this.capacity = capacity;
        this.comparator = comparator;
        this.heap = new ArrayList<>();
    }
    
    /**
     * Create a priority queue with unlimited capacity
     * @param comparator Comparator to determine priority
     */
    public PriorityQueue(Comparator<T> comparator) {
        this(-1, comparator);
    }
    
    /**
     * Create a priority queue for Comparable elements
     * @param capacity Maximum number of elements (-1 for unlimited)
     */
    @SuppressWarnings("unchecked")
    public PriorityQueue(int capacity) {
        this(capacity, (Comparator<T>) Comparator.naturalOrder());
    }
    
    /**
     * Create an unlimited priority queue for Comparable elements
     */
    @SuppressWarnings("unchecked")
    public PriorityQueue() {
        this(-1, (Comparator<T>) Comparator.naturalOrder());
    }
    
    @Override
    public boolean enqueue(T element) throws QueueFullException {
        if (element == null) {
            throw new IllegalArgumentException("Cannot enqueue null element");
        }
        
        if (isFull()) {
            throw new QueueFullException("Queue has reached maximum capacity: " + capacity);
        }
        
        heap.add(element);
        heapifyUp(heap.size() - 1);
        return true;
    }
    
    @Override
    public T dequeue() throws QueueEmptyException {
        if (isEmpty()) {
            throw new QueueEmptyException("Cannot dequeue from empty queue");
        }
        
        T result = heap.get(0);
        T lastElement = heap.remove(heap.size() - 1);
        
        if (!heap.isEmpty()) {
            heap.set(0, lastElement);
            heapifyDown(0);
        }
        
        return result;
    }
    
    @Override
    public T peek() throws QueueEmptyException {
        if (isEmpty()) {
            throw new QueueEmptyException("Cannot peek at empty queue");
        }
        
        return heap.get(0);
    }
    
    @Override
    public boolean isEmpty() {
        return heap.isEmpty();
    }
    
    @Override
    public boolean isFull() {
        return capacity > 0 && heap.size() >= capacity;
    }
    
    @Override
    public int size() {
        return heap.size();
    }
    
    @Override
    public int capacity() {
        return capacity;
    }
    
    @Override
    public void clear() {
        heap.clear();
    }
    
    @Override
    public List<T> toList() {
        // Return a copy of the heap (not in priority order, but in heap order)
        return new ArrayList<>(heap);
    }
    
    /**
     * Get elements in priority order (highest priority first)
     * Note: This is an expensive operation as it creates a copy and sorts it
     * @return List of elements in priority order
     */
    public List<T> toSortedList() {
        List<T> sorted = new ArrayList<>(heap);
        sorted.sort(comparator);
        return sorted;
    }
    
    @Override
    public boolean contains(T element) {
        return element != null && heap.contains(element);
    }
    
    /**
     * Move element up the heap to maintain heap property
     * @param index Index of element to move up
     */
    private void heapifyUp(int index) {
        while (index > 0) {
            int parentIndex = (index - 1) / 2;
            
            if (comparator.compare(heap.get(index), heap.get(parentIndex)) >= 0) {
                break; // Heap property satisfied
            }
            
            swap(index, parentIndex);
            index = parentIndex;
        }
    }
    
    /**
     * Move element down the heap to maintain heap property
     * @param index Index of element to move down
     */
    private void heapifyDown(int index) {
        int size = heap.size();
        
        while (true) {
            int smallest = index;
            int leftChild = 2 * index + 1;
            int rightChild = 2 * index + 2;
            
            if (leftChild < size && 
                comparator.compare(heap.get(leftChild), heap.get(smallest)) < 0) {
                smallest = leftChild;
            }
            
            if (rightChild < size && 
                comparator.compare(heap.get(rightChild), heap.get(smallest)) < 0) {
                smallest = rightChild;
            }
            
            if (smallest == index) {
                break; // Heap property satisfied
            }
            
            swap(index, smallest);
            index = smallest;
        }
    }
    
    /**
     * Swap two elements in the heap
     * @param i First index
     * @param j Second index
     */
    private void swap(int i, int j) {
        T temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }
    
    @Override
    public String toString() {
        if (isEmpty()) {
            return "PriorityQueue{empty}";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("PriorityQueue{size=").append(size());
        if (capacity > 0) {
            sb.append("/").append(capacity);
        }
        sb.append(", top=").append(heap.get(0));
        sb.append(", elements=").append(heap);
        sb.append("}");
        return sb.toString();
    }
}