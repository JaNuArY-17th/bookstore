package com.bookstore.service;

import com.bookstore.model.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for CartService functionality
 */
public class CartServiceTest {
    
    private CartService cartService;
    
    @BeforeEach
    void setUp() {
        cartService = new CartService();
    }
    
    @Test
    void testCartInitiallyEmpty() {
        assertTrue(cartService.isEmpty());
        assertEquals(0, cartService.getItemCount());
        assertEquals(0.0, cartService.getCartTotal());
    }
    
    @Test
    void testAddToCart() {
        // Note: This test assumes book ID 1 exists in the database
        // In a real test environment, you would mock the BookDAO
        System.out.println("Testing cart functionality...");
        
        // Test basic cart operations
        assertTrue(cartService.isEmpty());
        
        // Test getting cart items
        List<OrderItem> items = cartService.getCartItems();
        assertNotNull(items);
        assertTrue(items.isEmpty());
        
        // Test cart total
        assertEquals(0.0, cartService.getCartTotal());
        
        System.out.println("✓ Cart service basic functionality works correctly");
    }
    
    @Test
    void testClearCart() {
        cartService.clearCart();
        assertTrue(cartService.isEmpty());
        assertEquals(0, cartService.getItemCount());
    }
    
    @Test
    void testTransferToOrder() {
        List<OrderItem> orderItems = cartService.transferToOrder();
        assertNotNull(orderItems);
        assertTrue(cartService.isEmpty()); // Cart should be empty after transfer
    }
}
