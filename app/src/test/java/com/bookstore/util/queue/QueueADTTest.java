package com.bookstore.util.queue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Test suite for Queue ADT implementations
 * Tests ArrayQueue and LinkedQueue
 */
public class QueueADTTest {
    
    private QueueADT<Integer> arrayQueue;
    private QueueADT<Integer> linkedQueue;
    
    @BeforeEach
    void setUp() {
        arrayQueue = new ArrayQueue<>(5);
        linkedQueue = new LinkedQueue<>(5);
    }
    
    @Test
    void testEmptyQueueOperations() {
        // Test all queue implementations
        QueueADT<Integer>[] queues = new QueueADT[]{arrayQueue, linkedQueue};
        
        for (QueueADT<Integer> queue : queues) {
            assertTrue(queue.isEmpty());
            assertFalse(queue.isFull());
            assertEquals(0, queue.size());
            assertEquals(5, queue.capacity());
            assertEquals(0.0, queue.getUtilization(), 0.001);
            
            // Test exceptions on empty queue
            assertThrows(QueueEmptyException.class, queue::dequeue);
            assertThrows(QueueEmptyException.class, queue::peek);
        }
    }
    
    @Test
    void testBasicEnqueueDequeue() throws QueueFullException, QueueEmptyException {
        QueueADT<Integer>[] queues = new QueueADT[]{arrayQueue, linkedQueue};
        
        for (QueueADT<Integer> queue : queues) {
            // Test single enqueue/dequeue
            assertTrue(queue.enqueue(1));
            assertFalse(queue.isEmpty());
            assertEquals(1, queue.size());
            assertEquals(Integer.valueOf(1), queue.peek());
            assertEquals(Integer.valueOf(1), queue.dequeue());
            assertTrue(queue.isEmpty());
            
            // Test multiple enqueue/dequeue
            queue.enqueue(1);
            queue.enqueue(2);
            queue.enqueue(3);
            assertEquals(3, queue.size());
            
            assertEquals(Integer.valueOf(1), queue.dequeue());
            assertEquals(Integer.valueOf(2), queue.dequeue());
            assertEquals(Integer.valueOf(3), queue.dequeue());
            assertTrue(queue.isEmpty());
        }
    }
    
    @Test
    void testQueueCapacity() throws QueueFullException {
        QueueADT<Integer>[] queues = new QueueADT[]{arrayQueue, linkedQueue};
        
        for (QueueADT<Integer> queue : queues) {
            // Fill queue to capacity
            for (int i = 1; i <= 5; i++) {
                assertTrue(queue.enqueue(i));
                assertEquals(i, queue.size());
            }
            
            assertTrue(queue.isFull());
            assertEquals(5, queue.size());
            assertEquals(1.0, queue.getUtilization(), 0.001);
            
            // Test exception on full queue
            assertThrows(QueueFullException.class, () -> queue.enqueue(6));
        }
    }
    
    @Test
    void testQueueOrder() throws QueueFullException, QueueEmptyException {
        QueueADT<Integer>[] queues = new QueueADT[]{arrayQueue, linkedQueue};
        
        for (QueueADT<Integer> queue : queues) {
            // Test FIFO order
            queue.enqueue(10);
            queue.enqueue(20);
            queue.enqueue(30);
            
            assertEquals(Integer.valueOf(10), queue.dequeue());
            assertEquals(Integer.valueOf(20), queue.dequeue());
            assertEquals(Integer.valueOf(30), queue.dequeue());
        }
    }
    
    @Test
    void testQueueUtilization() throws QueueFullException {
        QueueADT<Integer>[] queues = new QueueADT[]{arrayQueue, linkedQueue};
        
        for (QueueADT<Integer> queue : queues) {
            assertEquals(0.0, queue.getUtilization(), 0.001);
            
            queue.enqueue(1);
            assertEquals(0.2, queue.getUtilization(), 0.001); // 1/5
            
            queue.enqueue(2);
            assertEquals(0.4, queue.getUtilization(), 0.001); // 2/5
            
            queue.enqueue(3);
            queue.enqueue(4);
            queue.enqueue(5);
            assertEquals(1.0, queue.getUtilization(), 0.001); // 5/5
        }
    }
    
    @Test
    void testQueueClear() throws QueueFullException {
        QueueADT<Integer>[] queues = new QueueADT[]{arrayQueue, linkedQueue};
        
        for (QueueADT<Integer> queue : queues) {
            queue.enqueue(1);
            queue.enqueue(2);
            queue.enqueue(3);
            
            assertFalse(queue.isEmpty());
            assertEquals(3, queue.size());
            
            queue.clear();
            
            assertTrue(queue.isEmpty());
            assertEquals(0, queue.size());
            assertEquals(0.0, queue.getUtilization(), 0.001);
        }
    }
    
    @Test
    void testQueueContains() throws QueueFullException {
        QueueADT<Integer>[] queues = new QueueADT[]{arrayQueue, linkedQueue};
        
        for (QueueADT<Integer> queue : queues) {
            assertFalse(queue.contains(1));
            
            queue.enqueue(1);
            queue.enqueue(2);
            queue.enqueue(3);
            
            assertTrue(queue.contains(1));
            assertTrue(queue.contains(2));
            assertTrue(queue.contains(3));
            assertFalse(queue.contains(4));
        }
    }
    
    @Test
    void testQueueToList() throws QueueFullException {
        QueueADT<Integer>[] queues = new QueueADT[]{arrayQueue, linkedQueue};
        
        for (QueueADT<Integer> queue : queues) {
            List<Integer> emptyList = queue.toList();
            assertTrue(emptyList.isEmpty());
            
            queue.enqueue(1);
            queue.enqueue(2);
            queue.enqueue(3);
            
            List<Integer> list = queue.toList();
            assertEquals(3, list.size());
            assertEquals(Integer.valueOf(1), list.get(0));
            assertEquals(Integer.valueOf(2), list.get(1));
            assertEquals(Integer.valueOf(3), list.get(2));
        }
    }
}