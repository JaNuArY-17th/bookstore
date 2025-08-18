package com.bookstore.service;

import com.bookstore.dao.CustomerDAO;
import com.bookstore.model.Customer;
import com.bookstore.model.Role;
import com.bookstore.model.User;
import com.bookstore.util.algorithms.SearchingAlgorithms;
import com.bookstore.util.algorithms.SortingAlgorithms;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Service class for Customer operations
 * Handles business logic for customer management using cached data
 */
public class CustomerService {
    
    private SessionDataManager sessionManager;
    private CustomerDAO customerDAO;
    
    /**
     * Constructor with session manager dependency
     */
    public CustomerService(SessionDataManager sessionManager) {
        this.sessionManager = sessionManager;
        this.customerDAO = new CustomerDAO();
    }
    
    /**
     * Sort customers by specified field and order
     * @param field The field to sort by (customer_id, name, email)
     * @param ascending True for ascending order, false for descending
     * @return Sorted list of customers
     */
    public List<Customer> sort(String field, boolean ascending) {
        List<Customer> customers = getCachedCustomersForCurrentUser();
        if (customers == null || customers.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Create a copy to avoid modifying the original cached list
        List<Customer> sortedCustomers = new ArrayList<>(customers);
        Comparator<Customer> comparator = getCustomerComparator(field);
        
        if (!ascending) {
            comparator = comparator.reversed();
        }
        
        SortingAlgorithms.quickSort(sortedCustomers, comparator);
        return sortedCustomers;
    }
    
    /**
     * Search customers by search term
     * @param searchTerm The term to search for (customer_id, name, email)
     * @return List of customers matching the search term
     */
    public List<Customer> search(String searchTerm) {
        List<Customer> customers = getCachedCustomersForCurrentUser();
        if (customers == null || customers.isEmpty()) {
            return new ArrayList<>();
        }
        
        return SearchingAlgorithms.searchCustomers(customers, searchTerm);
    }
    
    /**
     * Add new customer (admin or signup)
     * @param customer The customer to add
     * @param user The user performing the operation (null for signup)
     * @return The ID of the added customer, or -1 if failed
     */
    public int addCustomer(Customer customer, User user) {
        // Allow signup (user is null) or admin operations
        if (user != null && !isAdminUser(user)) {
            System.err.println("Access denied: Admin permission required to add customers");
            return -1;
        }
        
        // Add to database
        int customerId = customerDAO.addCustomer(customer);
        
        if (customerId != -1) {
            // Add to cache if successful and user is admin
            customer.setCustomerId(customerId);
            if (user != null && isAdminUser(user)) {
                addCustomerToCache(customer);
            }
            System.out.println("Customer added successfully with ID: " + customerId);
        }
        
        return customerId;
    }
    
    /**
     * Update customer information
     * @param customer The customer with updated information
     * @param user The user performing the operation
     * @return True if update successful, false otherwise
     */
    public boolean updateCustomer(Customer customer, User user) {
        // Allow users to update their own profile or admin to update any
        if (!canUpdateCustomer(customer, user)) {
            System.err.println("Access denied: You can only update your own profile");
            return false;
        }
        
        // Update in database
        boolean updated = customerDAO.updateCustomer(customer);
        
        if (updated) {
            // Update in cache if user is admin
            if (isAdminUser(user)) {
                updateCustomerInCache(customer);
            }
            System.out.println("Customer updated successfully: " + customer.getName());
        }
        
        return updated;
    }
    
    /**
     * Delete customer (admin only)
     * @param customerId The ID of the customer to delete
     * @param user The user performing the operation
     * @return True if deletion successful, false otherwise
     */
    public boolean deleteCustomer(int customerId, User user) {
        if (!isAdminUser(user)) {
            System.err.println("Access denied: Admin permission required to delete customers");
            return false;
        }
        
        // Delete from database
        boolean deleted = customerDAO.deleteCustomer(customerId);
        
        if (deleted) {
            // Remove from cache
            removeCustomerFromCache(customerId);
            System.out.println("Customer deleted successfully with ID: " + customerId);
        }
        
        return deleted;
    }
    
    /**
     * Get customer by ID
     * @param customerId The customer ID
     * @param user The user requesting the information
     * @return Customer if found and accessible, null otherwise
     */
    public Customer getCustomerById(int customerId, User user) {
        if (!isAdminUser(user)) {
            System.err.println("Access denied: Admin permission required to view customer details");
            return null;
        }
        
        List<Customer> customers = getCachedCustomersForCurrentUser();
        if (customers != null) {
            return customers.stream()
                    .filter(customer -> customer.getCustomerId() == customerId)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }
    
    /**
     * Get all customers (admin only)
     * @param user The user requesting the information
     * @return List of customers accessible to current user
     */
    public List<Customer> getAllCustomers(User user) {
        if (!isAdminUser(user)) {
            System.err.println("Access denied: Admin permission required to view all customers");
            return new ArrayList<>();
        }
        
        return getCachedCustomersForCurrentUser();
    }
    
    /**
     * Get customer by email
     * @param email The customer email
     * @param user The user requesting the information
     * @return Customer if found and accessible, null otherwise
     */
    public Customer getCustomerByEmail(String email, User user) {
        if (!isAdminUser(user)) {
            System.err.println("Access denied: Admin permission required to search customers by email");
            return null;
        }
        
        List<Customer> customers = getCachedCustomersForCurrentUser();
        if (customers != null) {
            return customers.stream()
                    .filter(customer -> email.equals(customer.getEmail()))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }
    
    // Helper methods
    
    /**
     * Get cached customers for current user (admin only)
     */
    private List<Customer> getCachedCustomersForCurrentUser() {
        if (sessionManager != null && sessionManager.hasCustomerAccess()) {
            return sessionManager.getCachedCustomers();
        }
        return new ArrayList<>();
    }
    
    /**
     * Check if user is admin
     */
    private boolean isAdminUser(User user) {
        return user != null && user.getRole() == Role.ADMIN;
    }
    
    /**
     * Check if user can update customer (admin or own profile)
     */
    private boolean canUpdateCustomer(Customer customer, User user) {
        if (user == null) {
            return false;
        }
        
        // Admin can update any customer
        if (isAdminUser(user)) {
            return true;
        }
        
        // Users can update their own profile
        // This would require linking Customer to User ID
        // For now, only allow admin updates
        return false;
    }
    
    /**
     * Get comparator for customer sorting
     */
    private Comparator<Customer> getCustomerComparator(String field) {
        switch (field.toLowerCase()) {
            case "customer_id":
                return Comparator.comparing(Customer::getCustomerId);
            case "name":
                return Comparator.comparing(Customer::getName, String.CASE_INSENSITIVE_ORDER);
            case "email":
                return Comparator.comparing(Customer::getEmail, String.CASE_INSENSITIVE_ORDER);
            default:
                throw new IllegalArgumentException("Invalid sort field: " + field);
        }
    }
    
    /**
     * Add customer to cache
     */
    private void addCustomerToCache(Customer customer) {
        List<Customer> customers = sessionManager.getCachedCustomers();
        if (customers != null) {
            customers.add(customer);
        }
    }
    
    /**
     * Update customer in cache
     */
    private void updateCustomerInCache(Customer updatedCustomer) {
        List<Customer> customers = sessionManager.getCachedCustomers();
        if (customers != null) {
            for (int i = 0; i < customers.size(); i++) {
                if (customers.get(i).getCustomerId() == updatedCustomer.getCustomerId()) {
                    customers.set(i, updatedCustomer);
                    break;
                }
            }
        }
    }
    
    /**
     * Remove customer from cache
     */
    private void removeCustomerFromCache(int customerId) {
        List<Customer> customers = sessionManager.getCachedCustomers();
        if (customers != null) {
            customers.removeIf(customer -> customer.getCustomerId() == customerId);
        }
    }
}
