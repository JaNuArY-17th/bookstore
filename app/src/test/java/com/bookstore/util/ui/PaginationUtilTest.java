package com.bookstore.util.ui;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for PaginationUtil functionality
 */
public class PaginationUtilTest {
    
    @Test
    void testGetPage() {
        List<String> items = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L");
        
        // Test first page
        List<String> page1 = PaginationUtil.getPage(items, 1, 5);
        assertEquals(5, page1.size());
        assertEquals("A", page1.get(0));
        assertEquals("E", page1.get(4));
        
        // Test second page
        List<String> page2 = PaginationUtil.getPage(items, 2, 5);
        assertEquals(5, page2.size());
        assertEquals("F", page2.get(0));
        assertEquals("J", page2.get(4));
        
        // Test last page (partial)
        List<String> page3 = PaginationUtil.getPage(items, 3, 5);
        assertEquals(2, page3.size());
        assertEquals("K", page3.get(0));
        assertEquals("L", page3.get(1));
    }
    
    @Test
    void testGetTotalPages() {
        assertEquals(3, PaginationUtil.getTotalPages(12, 5));
        assertEquals(2, PaginationUtil.getTotalPages(10, 5));
        assertEquals(1, PaginationUtil.getTotalPages(5, 5));
        assertEquals(1, PaginationUtil.getTotalPages(3, 5));
        assertEquals(0, PaginationUtil.getTotalPages(0, 5));
    }
    
    @Test
    void testValidatePageNumber() {
        assertEquals(1, PaginationUtil.validatePageNumber(0, 5));
        assertEquals(1, PaginationUtil.validatePageNumber(1, 5));
        assertEquals(3, PaginationUtil.validatePageNumber(3, 5));
        assertEquals(5, PaginationUtil.validatePageNumber(5, 5));
        assertEquals(5, PaginationUtil.validatePageNumber(10, 5));
    }
    
    @Test
    void testHandleNavigationInput() {
        // Test navigation commands
        assertEquals(2, PaginationUtil.handleNavigationInput("N", 1, 5));
        assertEquals(1, PaginationUtil.handleNavigationInput("P", 2, 5));
        assertEquals(1, PaginationUtil.handleNavigationInput("F", 3, 5));
        assertEquals(5, PaginationUtil.handleNavigationInput("L", 3, 5));
        assertEquals(-2, PaginationUtil.handleNavigationInput("G", 3, 5));
        
        // Test page number input
        assertEquals(3, PaginationUtil.handleNavigationInput("3", 1, 5));
        assertEquals(-1, PaginationUtil.handleNavigationInput("10", 1, 5)); // Out of range
        assertEquals(-1, PaginationUtil.handleNavigationInput("invalid", 1, 5));
    }
    
    @Test
    void testEmptyList() {
        List<String> emptyList = Arrays.asList();
        List<String> result = PaginationUtil.getPage(emptyList, 1, 10);
        assertTrue(result.isEmpty());
    }
    
    @Test
    void testSinglePage() {
        List<String> items = Arrays.asList("A", "B", "C");
        List<String> page = PaginationUtil.getPage(items, 1, 10);
        assertEquals(3, page.size());
        assertEquals(items, page);
    }
}
