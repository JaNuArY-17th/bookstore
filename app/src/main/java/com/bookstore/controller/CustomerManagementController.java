package com.bookstore.controller;

import com.bookstore.dao.CustomerDAO;
import com.bookstore.model.Customer;
import com.bookstore.service.CustomerService;
import com.bookstore.service.AuthService;
import com.bookstore.util.ui.DisplayFormatter;
import com.bookstore.util.ui.InputValidator;

import java.util.List;

/**
 * Controller for customer management operations
 * Extracted from Main.java to improve code organization
 */
public class CustomerManagementController {
    private CustomerDAO customerDAO;
    private CustomerService customerService;
    private AuthService authService;

    public CustomerManagementController(CustomerDAO customerDAO, CustomerService customerService, AuthService authService) {
        this.customerDAO = customerDAO;
        this.customerService = customerService;
        this.authService = authService;
    }

    /**
     * Display and handle customer management menu
     */
    public void showCustomerManagementMenu() {
        while (true) {
            System.out.println("\n=== CUSTOMER MANAGEMENT ===");
            System.out.println("1. Add New Customer");
            System.out.println("2. View All Customers");
            System.out.println("3. Search Customer by ID");
            System.out.println("4. Advanced Search Customers");
            System.out.println("5. Sort Customers");
            System.out.println("6. Refresh Customer List");
            System.out.println("0. Back to Main Menu");

            int choice = InputValidator.getIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    addNewCustomer();
                    break;
                case 2:
                    viewAllCustomers();
                    break;
                case 3:
                    searchCustomerById();
                    break;
                case 4:
                    advancedSearchCustomers();
                    break;
                case 5:
                    sortCustomers();
                    break;
                case 6:
                    // Refresh - just continue the loop
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    /**
     * Add a new customer to the system
     */
    public void addNewCustomer() {
        System.out.println("=== ADD NEW CUSTOMER ===");
        String name = InputValidator.getTrimmedStringInput("Name: ");
        String email = InputValidator.getTrimmedStringInput("Email: ");
        String address = InputValidator.getTrimmedStringInput("Address: ");

        Customer customer = new Customer(0, name, email, address);
        int customerId = customerDAO.addCustomer(customer);

        if (customerId != -1) {
            System.out.println("Customer added successfully with ID: " + customerId);
        } else {
            System.out.println("Failed to add customer.");
        }
    }

    /**
     * View all customers in the system
     */
    public void viewAllCustomers() {
        System.out.println("=== ALL CUSTOMERS ===");
        List<Customer> customers = customerDAO.getAllCustomers();
        DisplayFormatter.displayCustomersTable(customers);
    }

    /**
     * Search for a customer by ID
     */
    public void searchCustomerById() {
        int customerId = InputValidator.getIntInput("Enter Customer ID: ");
        Customer customer = customerDAO.getCustomerById(customerId);

        if (customer != null) {
            System.out.println("Customer found:");
            System.out.println(customer);
        } else {
            System.out.println("Customer not found with ID: " + customerId);
        }
    }

    /**
     * Advanced search customers using CustomerService
     */
    public void advancedSearchCustomers() {
        System.out.println("=== ADVANCED CUSTOMER SEARCH ===");
        String searchTerm = InputValidator.getTrimmedStringInput("Enter search term (customer ID, name, or email): ");

        if (searchTerm.isEmpty()) {
            System.out.println("Search term cannot be empty.");
            return;
        }

        try {
            List<Customer> searchResults = customerService.search(searchTerm);

            if (searchResults.isEmpty()) {
                System.out.println("No customers found matching: " + searchTerm);
            } else {
                System.out.println("\n=== SEARCH RESULTS ===");
                System.out.println("Found " + searchResults.size() + " customer(s) matching: " + searchTerm);
                DisplayFormatter.displayCustomersTable(searchResults);
            }
        } catch (Exception e) {
            System.out.println("Error performing search: " + e.getMessage());
        }

        System.out.println("\nPress Enter to continue...");
        InputValidator.getStringInput("");
    }

    /**
     * Sort customers using CustomerService
     */
    public void sortCustomers() {
        System.out.println("=== SORT CUSTOMERS ===");
        System.out.println("Sort by:");
        System.out.println("1. Customer ID");
        System.out.println("2. Name");
        System.out.println("3. Email");
        System.out.println("0. Cancel");

        int choice = InputValidator.getIntInput("Enter your choice: ");

        String field;
        switch (choice) {
            case 1:
                field = "customer_id";
                break;
            case 2:
                field = "name";
                break;
            case 3:
                field = "email";
                break;
            case 0:
                return;
            default:
                System.out.println("Invalid choice.");
                return;
        }

        boolean ascending = InputValidator.getConfirmation("Sort in ascending order? (y/n): ");

        try {
            List<Customer> sortedCustomers = customerService.sort(field, ascending);

            if (sortedCustomers.isEmpty()) {
                System.out.println("No customers available to sort.");
            } else {
                System.out.println("\n=== SORTED CUSTOMERS ===");
                System.out.println("Sorted by " + field + " (" + (ascending ? "ascending" : "descending") + "):");
                DisplayFormatter.displayCustomersTable(sortedCustomers);
            }
        } catch (Exception e) {
            System.out.println("Error sorting customers: " + e.getMessage());
        }

        System.out.println("\nPress Enter to continue...");
        InputValidator.getStringInput("");
    }
}
