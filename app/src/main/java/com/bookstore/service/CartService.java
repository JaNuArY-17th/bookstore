package com.bookstore.service;

import com.bookstore.dao.BookDAO;
import com.bookstore.model.Book;
import com.bookstore.model.OrderItem;
import com.bookstore.util.ui.DisplayFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class for managing customer shopping cart
 * Provides session-based cart functionality
 */
public class CartService {
    private List<OrderItem> cartItems;
    private BookDAO bookDAO;
    
    public CartService() {
        this.cartItems = new ArrayList<>();
        this.bookDAO = new BookDAO();
    }
    
    /**
     * Add item to cart
     * @param bookId The book ID to add
     * @param quantity The quantity to add
     * @return true if successfully added, false otherwise
     */
    public boolean addToCart(int bookId, int quantity) {
        if (quantity <= 0) {
            System.out.println("Quantity must be greater than 0.");
            return false;
        }
        
        Book book = bookDAO.getBookById(bookId);
        if (book == null) {
            System.out.println("Book not found.");
            return false;
        }
        
        if (book.getStockQuantity() < quantity) {
            System.out.println("Insufficient stock. Available: " + book.getStockQuantity());
            return false;
        }
        
        // Check if book is already in cart
        for (OrderItem item : cartItems) {
            if (item.getBookId() == bookId) {
                int newQuantity = item.getQuantity() + quantity;
                if (newQuantity > book.getStockQuantity()) {
                    System.out.println("Total quantity would exceed available stock (" + book.getStockQuantity() + ").");
                    return false;
                }
                item.setQuantity(newQuantity);
                System.out.println("Updated quantity for '" + book.getTitle() + "' to " + newQuantity);
                return true;
            }
        }
        
        // Add new item to cart
        OrderItem newItem = new OrderItem(0, 0, bookId, quantity, book.getPrice());
        cartItems.add(newItem);
        System.out.println("Added " + quantity + " copies of '" + book.getTitle() + "' to cart!");
        return true;
    }
    
    /**
     * Remove item from cart
     * @param bookId The book ID to remove
     * @return true if successfully removed, false otherwise
     */
    public boolean removeFromCart(int bookId) {
        for (int i = 0; i < cartItems.size(); i++) {
            if (cartItems.get(i).getBookId() == bookId) {
                Book book = bookDAO.getBookById(bookId);
                String bookTitle = book != null ? book.getTitle() : "Unknown Book";
                cartItems.remove(i);
                System.out.println("Removed '" + bookTitle + "' from cart.");
                return true;
            }
        }
        System.out.println("Book not found in cart.");
        return false;
    }
    
    /**
     * Update quantity of item in cart
     * @param bookId The book ID to update
     * @param newQuantity The new quantity
     * @return true if successfully updated, false otherwise
     */
    public boolean updateQuantity(int bookId, int newQuantity) {
        if (newQuantity <= 0) {
            return removeFromCart(bookId);
        }
        
        Book book = bookDAO.getBookById(bookId);
        if (book == null) {
            System.out.println("Book not found.");
            return false;
        }
        
        if (newQuantity > book.getStockQuantity()) {
            System.out.println("Quantity exceeds available stock (" + book.getStockQuantity() + ").");
            return false;
        }
        
        for (OrderItem item : cartItems) {
            if (item.getBookId() == bookId) {
                item.setQuantity(newQuantity);
                System.out.println("Updated quantity for '" + book.getTitle() + "' to " + newQuantity);
                return true;
            }
        }
        
        System.out.println("Book not found in cart.");
        return false;
    }
    
    /**
     * View cart contents
     */
    public void viewCart() {
        if (cartItems.isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }
        
        System.out.println("\n=== YOUR SHOPPING CART ===");
        double totalAmount = 0.0;
        
        System.out.printf("%-5s %-30s %-10s %-8s %-10s%n", "ID", "Title", "Price", "Qty", "Subtotal");
        System.out.println("=".repeat(70));
        
        for (OrderItem item : cartItems) {
            Book book = bookDAO.getBookById(item.getBookId());
            if (book != null) {
                double subtotal = item.getQuantity() * item.getUnitPrice();
                totalAmount += subtotal;
                
                System.out.printf("%-5d %-30s $%-9.2f %-8d $%-9.2f%n",
                    book.getBookId(),
                    book.getTitle().length() > 30 ? book.getTitle().substring(0, 27) + "..." : book.getTitle(),
                    item.getUnitPrice(),
                    item.getQuantity(),
                    subtotal);
            }
        }
        
        System.out.println("=".repeat(70));
        System.out.printf("Total: $%.2f%n", totalAmount);
        System.out.println("Items in cart: " + cartItems.size());
    }
    
    /**
     * Get cart items
     * @return List of cart items
     */
    public List<OrderItem> getCartItems() {
        return new ArrayList<>(cartItems);
    }
    
    /**
     * Get cart total amount
     * @return Total amount
     */
    public double getCartTotal() {
        double total = 0.0;
        for (OrderItem item : cartItems) {
            total += item.getQuantity() * item.getUnitPrice();
        }
        return total;
    }
    
    /**
     * Check if cart is empty
     * @return true if cart is empty, false otherwise
     */
    public boolean isEmpty() {
        return cartItems.isEmpty();
    }
    
    /**
     * Get cart item count
     * @return Number of items in cart
     */
    public int getItemCount() {
        return cartItems.size();
    }
    
    /**
     * Clear cart
     */
    public void clearCart() {
        cartItems.clear();
        System.out.println("Cart cleared.");
    }
    
    /**
     * Transfer cart items to order items list
     * @return List of order items
     */
    public List<OrderItem> transferToOrder() {
        List<OrderItem> orderItems = new ArrayList<>(cartItems);
        cartItems.clear();
        return orderItems;
    }
}
