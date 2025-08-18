package com.bookstore.service;

import com.bookstore.dao.BookDAO;
import com.bookstore.dao.CustomerDAO;
import com.bookstore.dao.OrderDAO;
import com.bookstore.dao.UserDAO;
import com.bookstore.model.Book;
import com.bookstore.model.Customer;
import com.bookstore.model.Order;
import com.bookstore.model.User;
import com.bookstore.model.Role;
import com.bookstore.util.queue.OrderQueueManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Session Data Manager for role-based data caching and queue integration
 * Manages cached data during user sessions and integrates with existing queue system
 */
public class SessionDataManager {
    
    // Cached data based on user role
    private List<User> cachedUsers;        // Admin only
    private List<Book> cachedBooks;        // Both admin and customer
    private List<Order> cachedOrders;      // Role-based: all orders for admin, user orders for customer
    private List<Customer> cachedCustomers; // Admin only
    
    // Current session user
    private User currentUser;
    
    // DAO instances for data loading
    private UserDAO userDAO;
    private BookDAO bookDAO;
    private OrderDAO orderDAO;
    private CustomerDAO customerDAO;
    
    /**
     * Constructor - initializes DAO instances
     */
    public SessionDataManager() {
        this.userDAO = new UserDAO();
        this.bookDAO = new BookDAO();
        this.orderDAO = new OrderDAO();
        this.customerDAO = new CustomerDAO();
    }
    
    /**
     * Initialize user session with role-based data caching and queue integration
     * @param user The logged-in user
     */
    public void initializeUserSession(User user) {
        this.currentUser = user;
        
        // Clear any existing cached data
        clearCachedData();
        
        // Load data based on user role
        if (user.getRole() == Role.ADMIN) {
            initializeAdminSession();
        } else {
            initializeCustomerSession();
        }
        
        // Initialize queues from cached orders
        initializeQueuesFromCache();
        
        System.out.println("Session initialized for " + user.getRole() + ": " + user.getUsername());
        logCacheStatistics();
    }
    
    /**
     * Initialize session for admin users - load all data
     */
    private void initializeAdminSession() {
        try {
            // Admin gets access to all data
            this.cachedUsers = userDAO.getAllUsers();
            this.cachedBooks = bookDAO.getAllBooks();
            this.cachedOrders = orderDAO.getAllOrders();
            this.cachedCustomers = customerDAO.getAllCustomers();
            
            System.out.println("Admin session data loaded successfully");
        } catch (Exception e) {
            System.err.println("Error loading admin session data: " + e.getMessage());
            // Initialize empty lists to prevent null pointer exceptions
            initializeEmptyLists();
        }
    }
    
    /**
     * Initialize session for customer users - load limited data
     */
    private void initializeCustomerSession() {
        try {
            // Customer gets limited access
            this.cachedUsers = null; // No access to user data
            this.cachedCustomers = null; // No access to customer data
            this.cachedBooks = bookDAO.getAllBooks(); // Can view all books
            this.cachedOrders = orderDAO.getOrdersByCustomerId(currentUser.getUserId()); // Only their orders
            
            System.out.println("Customer session data loaded successfully");
        } catch (Exception e) {
            System.err.println("Error loading customer session data: " + e.getMessage());
            // Initialize empty lists to prevent null pointer exceptions
            this.cachedBooks = new ArrayList<>();
            this.cachedOrders = new ArrayList<>();
        }
    }
    
    /**
     * Initialize queues from cached order data
     * Integrates with existing OrderQueueManager
     */
    private void initializeQueuesFromCache() {
        try {
            // Clear existing queues first
            OrderQueueManager.clearAllQueues();
            
            if (cachedOrders == null || cachedOrders.isEmpty()) {
                System.out.println("No orders to load into queues");
                return;
            }
            
            int loadedCount = 0;
            for (Order order : cachedOrders) {
                String status = order.getStatus().name();
                // Only load pending and processing orders into queues
                if ("PENDING".equals(status) || "PROCESSING".equals(status)) {
                    OrderQueueManager.addOrderToQueues(order, currentUser);
                    loadedCount++;
                }
            }
            
            System.out.println("Loaded " + loadedCount + " orders into queues from cached data");
            
        } catch (Exception e) {
            System.err.println("Error initializing queues from cache: " + e.getMessage());
        }
    }
    
    /**
     * Clear all cached data and queues
     */
    public void clearSession() {
        // Clear cached data
        clearCachedData();
        
        // Clear queues
        OrderQueueManager.clearAllQueues();
        
        // Clear current user
        this.currentUser = null;
        
        System.out.println("Session data and queues cleared");
    }
    
    /**
     * Clear cached data lists
     */
    private void clearCachedData() {
        this.cachedUsers = null;
        this.cachedBooks = null;
        this.cachedOrders = null;
        this.cachedCustomers = null;
    }
    
    /**
     * Initialize empty lists to prevent null pointer exceptions
     */
    private void initializeEmptyLists() {
        this.cachedUsers = new ArrayList<>();
        this.cachedBooks = new ArrayList<>();
        this.cachedOrders = new ArrayList<>();
        this.cachedCustomers = new ArrayList<>();
    }
    
    /**
     * Add new order to cache and queue
     * @param order The new order to add
     */
    public void addOrderToCache(Order order) {
        if (cachedOrders != null) {
            cachedOrders.add(order);
            
            // Add to queue if it's pending or processing
            String status = order.getStatus().name();
            if ("PENDING".equals(status) || "PROCESSING".equals(status)) {
                OrderQueueManager.addOrderToQueues(order, currentUser);
            }
        }
    }
    
    /**
     * Find order in cache by ID
     * @param orderId The order ID to find
     * @return Order if found, null otherwise
     */
    public Order findOrderInCache(int orderId) {
        if (cachedOrders != null) {
            return cachedOrders.stream()
                    .filter(order -> order.getOrderId() == orderId)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }
    
    /**
     * Update order in cache
     * @param updatedOrder The updated order
     */
    public void updateOrderInCache(Order updatedOrder) {
        if (cachedOrders != null) {
            for (int i = 0; i < cachedOrders.size(); i++) {
                if (cachedOrders.get(i).getOrderId() == updatedOrder.getOrderId()) {
                    cachedOrders.set(i, updatedOrder);
                    break;
                }
            }
        }
    }
    
    /**
     * Log cache statistics for debugging
     */
    private void logCacheStatistics() {
        System.out.println("=== CACHE STATISTICS ===");
        System.out.println("Users: " + (cachedUsers != null ? cachedUsers.size() : "N/A"));
        System.out.println("Books: " + (cachedBooks != null ? cachedBooks.size() : "N/A"));
        System.out.println("Orders: " + (cachedOrders != null ? cachedOrders.size() : "N/A"));
        System.out.println("Customers: " + (cachedCustomers != null ? cachedCustomers.size() : "N/A"));
        System.out.println("========================");
    }
    
    // Getters for cached data
    public List<User> getCachedUsers() {
        return cachedUsers;
    }
    
    public List<Book> getCachedBooks() {
        return cachedBooks;
    }
    
    public List<Order> getCachedOrders() {
        return cachedOrders;
    }
    
    public List<Customer> getCachedCustomers() {
        return cachedCustomers;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Check if user has access to user data (admin only)
     */
    public boolean hasUserAccess() {
        return currentUser != null && currentUser.getRole() == Role.ADMIN && cachedUsers != null;
    }
    
    /**
     * Check if user has access to customer data (admin only)
     */
    public boolean hasCustomerAccess() {
        return currentUser != null && currentUser.getRole() == Role.ADMIN && cachedCustomers != null;
    }
}
