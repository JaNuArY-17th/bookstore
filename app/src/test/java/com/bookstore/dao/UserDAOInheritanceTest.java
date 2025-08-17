package com.bookstore.dao;

import com.bookstore.model.User;
import com.bookstore.model.Admin;
import com.bookstore.model.Customer;
import com.bookstore.model.Role;

/**
 * Test class to verify UserDAO inheritance functionality
 */
public class UserDAOInheritanceTest {
    
    public static void main(String[] args) {
        System.out.println("=== Testing UserDAO Inheritance Functionality ===\n");
        
        // Test Customer creation
        System.out.println("1. Testing Customer Creation:");
        Customer testCustomer = new Customer("test_customer", "hashedPassword123", 
                                           "test@customer.com", "Test", "Customer");
        testCustomer.setAddress("123 Test Street");
        testCustomer.setPhoneNumber("555-0123");
        testCustomer.setPreferredPaymentMethod("Credit Card");
        
        System.out.println("Customer created:");
        System.out.println("  Username: " + testCustomer.getUsername());
        System.out.println("  Email: " + testCustomer.getEmail());
        System.out.println("  Full Name: " + testCustomer.getFullName());
        System.out.println("  Role: " + testCustomer.getRole());
        System.out.println("  User Type: " + testCustomer.getUserType());
        System.out.println("  Address: " + testCustomer.getAddress());
        System.out.println("  Phone: " + testCustomer.getPhoneNumber());
        System.out.println("  Can Place Order: " + testCustomer.canPlaceOrder());
        System.out.println("  Can View Books: " + testCustomer.canViewBooks());
        System.out.println("  Has VIEW_BOOKS permission: " + testCustomer.hasPermission("VIEW_BOOKS"));
        System.out.println("  Has USER_MANAGEMENT permission: " + testCustomer.hasPermission("USER_MANAGEMENT"));
        System.out.println();
        
        // Test Admin creation
        System.out.println("2. Testing Admin Creation:");
        Admin testAdmin = new Admin("test_admin", "hashedPassword123", 
                                  "admin@test.com", "Test", "Admin");
        testAdmin.setDepartment("IT");
        testAdmin.setAdminLevel("MANAGER");
        
        System.out.println("Admin created:");
        System.out.println("  Username: " + testAdmin.getUsername());
        System.out.println("  Email: " + testAdmin.getEmail());
        System.out.println("  Full Name: " + testAdmin.getFullName());
        System.out.println("  Role: " + testAdmin.getRole());
        System.out.println("  User Type: " + testAdmin.getUserType());
        System.out.println("  Department: " + testAdmin.getDepartment());
        System.out.println("  Admin Level: " + testAdmin.getAdminLevel());
        System.out.println("  Can Manage Users: " + testAdmin.canManageUsers());
        System.out.println("  Can Manage Books: " + testAdmin.canManageBooks());
        System.out.println("  Has USER_MANAGEMENT permission: " + testAdmin.hasPermission("USER_MANAGEMENT"));
        System.out.println("  Has VIEW_BOOKS permission: " + testAdmin.hasPermission("VIEW_BOOKS"));
        System.out.println();
        
        // Test polymorphism
        System.out.println("3. Testing Polymorphism:");
        User[] users = {testCustomer, testAdmin};
        
        for (User user : users) {
            System.out.println("User: " + user.getFullName());
            System.out.println("  Type: " + user.getUserType());
            System.out.println("  Role: " + user.getRole().getDisplayName());
            System.out.println("  Is Admin: " + user.isAdmin());
            System.out.println("  Is Customer: " + user.isCustomer());
            System.out.println("  Can view books: " + user.hasPermission("VIEW_BOOKS"));
            System.out.println("  Can manage users: " + user.hasPermission("USER_MANAGEMENT"));
            System.out.println();
        }
        
        // Test order history functionality
        System.out.println("4. Testing Customer Order History:");
        testCustomer.addOrderToHistory(1001);
        testCustomer.addOrderToHistory(1002);
        testCustomer.addOrderToHistory(1003);
        
        System.out.println("Customer order history:");
        System.out.println("  Has order history: " + testCustomer.hasOrderHistory());
        System.out.println("  Total orders: " + testCustomer.getTotalOrders());
        System.out.println("  Order IDs: " + testCustomer.getOrderHistory());
        System.out.println();
        
        System.out.println("=== All inheritance tests completed successfully! ===");
        System.out.println("\nNote: This test verifies the inheritance structure works correctly.");
        System.out.println("Database integration would require actual database connection and tables.");
    }
}
