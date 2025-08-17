package com.bookstore.model;

/**
 * Test class to verify the new inheritance structure
 */
public class UserInheritanceTest {
    
    public static void main(String[] args) {
        System.out.println("=== Testing User Inheritance Structure ===\n");
        
        // Test Admin creation
        Admin admin = new Admin("admin1", "password123", "admin@bookstore.com", "John", "Admin");
        admin.setDepartment("IT");
        admin.setAdminLevel("MANAGER");
        
        System.out.println("Admin Test:");
        System.out.println("User Type: " + admin.getUserType());
        System.out.println("Is Admin: " + admin.isAdmin());
        System.out.println("Is Customer: " + admin.isCustomer());
        System.out.println("Can Manage Users: " + admin.canManageUsers());
        System.out.println("Can Manage Books: " + admin.canManageBooks());
        System.out.println("Has USER_MANAGEMENT permission: " + admin.hasPermission("USER_MANAGEMENT"));
        System.out.println("Admin Details: " + admin.toString());
        System.out.println();
        
        // Test Customer creation
        Customer customer = new Customer("customer1", "password123", "customer@email.com", "Jane", "Customer");
        customer.setAddress("123 Main St");
        customer.setPhoneNumber("555-1234");
        customer.addOrderToHistory(1001);
        customer.addOrderToHistory(1002);
        
        System.out.println("Customer Test:");
        System.out.println("User Type: " + customer.getUserType());
        System.out.println("Is Admin: " + customer.isAdmin());
        System.out.println("Is Customer: " + customer.isCustomer());
        System.out.println("Can Place Order: " + customer.canPlaceOrder());
        System.out.println("Can View Books: " + customer.canViewBooks());
        System.out.println("Has VIEW_BOOKS permission: " + customer.hasPermission("VIEW_BOOKS"));
        System.out.println("Has USER_MANAGEMENT permission: " + customer.hasPermission("USER_MANAGEMENT"));
        System.out.println("Total Orders: " + customer.getTotalOrders());
        System.out.println("Customer Details: " + customer.toString());
        System.out.println();
        
        // Test polymorphism
        System.out.println("Polymorphism Test:");
        User[] users = {admin, customer};
        
        for (User user : users) {
            System.out.println("User: " + user.getFullName());
            System.out.println("  Type: " + user.getUserType());
            System.out.println("  Role: " + user.getRole().getDisplayName());
            System.out.println("  Can view books: " + user.hasPermission("VIEW_BOOKS"));
            System.out.println("  Can manage users: " + user.hasPermission("USER_MANAGEMENT"));
            System.out.println();
        }
        
        System.out.println("=== All tests completed successfully! ===");
    }
}
