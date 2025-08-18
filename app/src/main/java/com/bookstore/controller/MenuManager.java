package com.bookstore.controller;

import com.bookstore.model.Role;
import com.bookstore.model.User;
import com.bookstore.service.AuthService;
import com.bookstore.util.ui.InputValidator;

/**
 * Manages all menu displays and navigation logic
 * Extracted from Main.java to improve code organization
 */
public class MenuManager {
    private AuthService authService;
    private BookManagementController bookController;
    private CustomerManagementController customerController;
    private OrderManagementController orderController;
    private AuthenticationController authController;

    public MenuManager(AuthService authService,
            BookManagementController bookController,
            CustomerManagementController customerController,
            OrderManagementController orderController,
            AuthenticationController authController) {
        this.authService = authService;
        this.bookController = bookController;
        this.customerController = customerController;
        this.orderController = orderController;
        this.authController = authController;
    }

    /**
     * Display and handle login menu
     * 
     * @return true to continue, false to exit
     */
    public boolean showLoginMenu() {
        System.out.println("=== WELCOME TO BOOKSTORE ===");
        System.out.println("1. Login");
        System.out.println("2. Sign Up (Customer)");
        System.out.println("0. Exit");

        int choice = InputValidator.getIntInput("Enter your choice: ");

        switch (choice) {
            case 1:
                return authController.performLogin();
            case 2:
                return authController.performSignup();
            case 0:
                return false;
            default:
                System.out.println("Invalid choice.");
                return showLoginMenu();
        }
    }

    /**
     * Display and handle admin main menu
     * 
     * @return true to continue, false to exit
     */
    public boolean showAdminMainMenu() {
        System.out.println("=== ADMIN DASHBOARD ===");
        System.out.println("1. Book Management");
        System.out.println("2. Customer Management");
        System.out.println("3. Order Management");
        System.out.println("4. Account Management");
        System.out.println("5. Queue Processing");
        System.out.println("6. Algorithms Demo");
        System.out.println("7. Profile Settings");
        System.out.println("8. Logout");
        System.out.println("0. Exit");

        int choice = InputValidator.getIntInput("Enter your choice: ");

        switch (choice) {
            case 1:
                bookController.showBookManagementMenu();
                break;
            case 2:
                customerController.showCustomerManagementMenu();
                break;
            case 3:
                orderController.showOrderManagementMenu();
                break;
            case 4:
                authController.showAccountManagementMenu();
                break;
            case 5:
                orderController.showOrderQueueProcessing();
                break;
            case 6:
                orderController.demonstrateAlgorithms();
                break;
            case 7:
                authController.showUserProfileMenu();
                break;
            case 8:
                authService.logout();
                System.out.println("Logged out successfully.");
                return true; // Continue to login menu
            case 0:
                authService.logout();
                System.out.println("Thank you for using Bookstore Management System!");
                return false; // Exit application
            default:
                System.out.println("Invalid choice. Please try again.");
        }
        return true;
    }

    /**
     * Display and handle customer main menu
     * 
     * @return true to continue, false to exit
     */
    public boolean showCustomerMainMenu() {
        System.out.println("=== CUSTOMER DASHBOARD ===");
        System.out.println("1. Browse Books");
        System.out.println("2. Search Books");
        System.out.println("3. Filter Books by Category");
        System.out.println("4. My Cart (" + authService.getCartService().getItemCount() + " items)");
        System.out.println("5. My Orders");
        System.out.println("6. Place New Order");
        System.out.println("7. Profile Settings");
        System.out.println("8. Logout");
        System.out.println("0. Exit");

        int choice = InputValidator.getIntInput("Enter your choice: ");

        switch (choice) {
            case 1:
                orderController.showCustomerBrowseBooks();
                break;
            case 2:
                orderController.showCustomerSearchBooks();
                break;
            case 3:
                orderController.showCustomerFilterBooks();
                break;
            case 4:
                orderController.showCartManagement();
                break;
            case 5:
                orderController.showCustomerViewOrders();
                break;
            case 6:
                orderController.showCustomerPlaceOrder();
                break;
            case 7:
                authController.showUserProfileMenu();
                break;
            case 8:
                authService.logout();
                System.out.println("Logged out successfully.");
                return true; // Continue to login menu
            case 0:
                authService.logout();
                System.out.println("Thank you for using Bookstore Management System!");
                return false; // Exit application
            default:
                System.out.println("Invalid choice. Please try again.");
        }
        return true;
    }

    /**
     * Show appropriate main menu based on user role
     * 
     * @return true to continue, false to exit
     */
    public boolean showMainMenu() {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            // This should not happen if login flow is correct, but handle gracefully
            System.out.println("Error: No user logged in. Returning to login menu.");
            return true;
        }

        if (currentUser.getRole() == Role.ADMIN) {
            return showAdminMainMenu();
        } else {
            return showCustomerMainMenu();
        }
    }

    /**
     * Display welcome message and system information
     */
    public void displayWelcomeMessage() {
        System.out.println("=== Welcome to Bookstore Management System ===");
        System.out.println("Console Application - Java 21 with MySQL Database");
        System.out.println();
    }

    /**
     * Display exit message
     */
    public void displayExitMessage() {
        System.out.println("Thank you for using Bookstore Management System!");
    }
}
