package com.bookstore.controller;

import com.bookstore.dao.CustomerDAO;
import com.bookstore.model.Customer;
import com.bookstore.service.CustomerService;
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

    public CustomerManagementController(CustomerDAO customerDAO, CustomerService customerService) {
        this.customerDAO = customerDAO;
        this.customerService = customerService;
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
            System.out.println("4. Refresh Customer List");
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
}
