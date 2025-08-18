package com.bookstore.main;

import com.bookstore.controller.AuthenticationController;
import com.bookstore.controller.BookManagementController;
import com.bookstore.controller.CustomerManagementController;
import com.bookstore.controller.MenuManager;
import com.bookstore.controller.OrderManagementController;
import com.bookstore.dao.BookDAO;
import com.bookstore.dao.CustomerDAO;
import com.bookstore.dao.OrderDAO;
import com.bookstore.dao.UserDAO;
import com.bookstore.service.AuthService;
import com.bookstore.service.OrderService;
import com.bookstore.service.BookService;
import com.bookstore.service.CustomerService;
import com.bookstore.service.SessionDataManager;
import com.bookstore.util.database.DatabaseInitializer;
import com.bookstore.util.database.DatabaseTestUtil;

/**
 * Console-based Bookstore Management System
 * Refactored to use controller components for better code organization
 * Demonstrates core functionality including:
 * - Book management (CRUD operations)
 * - Customer management
 * - Order processing with queue
 * - Search and sorting algorithms
 */
public class Main {
    // DAOs
    private static BookDAO bookDAO = new BookDAO();
    private static CustomerDAO customerDAO = new CustomerDAO();
    private static OrderDAO orderDAO = new OrderDAO();
    private static UserDAO userDAO = new UserDAO();
    
    // Services
    private static AuthService authService = new AuthService();
    private static OrderService orderService = new OrderService();
    private static BookService bookService;
    private static CustomerService customerService;
    
    // Controllers
    private static BookManagementController bookController;
    private static CustomerManagementController customerController;
    private static OrderManagementController orderController;
    private static AuthenticationController authController;
    private static MenuManager menuManager;

    public static void main(String[] args) {
        // Initialize controllers
        initializeControllers();
        
        // Display welcome message
        menuManager.displayWelcomeMessage();

        // Initialize database and create default admin
        initializeSystem();

        // Main application loop
        boolean running = true;
        while (running) {
            if (!authService.isLoggedIn()) {
                if (!menuManager.showLoginMenu()) {
                    running = false;
                }
            } else {
                // Only show main menu if user is logged in
                if (!menuManager.showMainMenu()) {
                    running = false;
                }
            }
            System.out.println();
        }

        menuManager.displayExitMessage();
    }

    /**
     * Initialize all controller components
     */
    private static void initializeControllers() {
        // Initialize services with session data manager
        initializeServices();

        // Initialize controllers with their dependencies
        bookController = new BookManagementController(bookDAO, bookService);
        customerController = new CustomerManagementController(customerDAO, customerService, authService);
        orderController = new OrderManagementController(orderDAO, bookDAO, customerDAO, orderService, authService, bookService);
        authController = new AuthenticationController(authService, userDAO, customerDAO);
        menuManager = new MenuManager(authService, bookController, customerController, orderController, authController);
    }

    /**
     * Initialize services with session data manager
     */
    private static void initializeServices() {
        // Get session data manager from auth service
        SessionDataManager sessionManager = authService.getSessionDataManager();

        // Initialize services with session manager
        bookService = new BookService(sessionManager);
        customerService = new CustomerService(sessionManager);

        // Update order service to use session manager
        orderService = new OrderService(sessionManager);
    }

    /**
     * Initialize database and create default admin user
     */
    private static void initializeSystem() {
        try {
            System.out.println("Initializing database...");
            DatabaseInitializer.initializeDatabase();
            
            // Test database connection
            if (DatabaseTestUtil.testDatabaseConnection()) {
                System.out.println("Database connection successful!");
            } else {
                System.err.println("Database connection failed!");
                System.exit(1);
            }

            // Create default admin if it doesn't exist
            if (authService.getUserDAOForInitialization().getUserByUsername("admin") == null) {
                System.out.println("Creating default admin account...");
                if (authService.registerAdminForInitialization("admin", "admin123", "admin@bookstore.com", "System", "Administrator")) {
                    System.out.println("Default admin account created successfully!");
                    System.out.println("Username: admin");
                    System.out.println("Password: admin123");
                } else {
                    System.err.println("Failed to create default admin account!");
                }
            }

            System.out.println("System initialization complete!");
            System.out.println();

        } catch (Exception e) {
            System.err.println("Error initializing system: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
