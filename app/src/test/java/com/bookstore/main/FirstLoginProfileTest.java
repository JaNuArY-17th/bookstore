package com.bookstore.main;

import com.bookstore.model.Customer;

/**
 * Test class to demonstrate first login profile completion functionality
 */
public class FirstLoginProfileTest {
    
    public static void main(String[] args) {
        System.out.println("=== Testing First Login Profile Completion ===\n");
        
        // Test 1: New customer with incomplete profile
        System.out.println("1. Testing new customer with incomplete profile:");
        Customer newCustomer = new Customer("new_user", "hashedPassword", 
                                          "newuser@test.com", "New", "User");
        
        System.out.println("Customer created:");
        System.out.println("  Username: " + newCustomer.getUsername());
        System.out.println("  Full Name: " + newCustomer.getFullName());
        System.out.println("  Email: " + newCustomer.getEmail());
        System.out.println("  Address: " + newCustomer.getAddress());
        System.out.println("  Phone: " + newCustomer.getPhoneNumber());
        System.out.println("  Payment Method: " + newCustomer.getPreferredPaymentMethod());
        
        boolean needsProfileCompletion = isIncompleteProfile(newCustomer);
        System.out.println("  Needs profile completion: " + needsProfileCompletion);
        System.out.println();
        
        // Test 2: Customer with complete profile
        System.out.println("2. Testing customer with complete profile:");
        Customer completeCustomer = new Customer("complete_user", "hashedPassword", 
                                                "complete@test.com", "Complete", "User");
        completeCustomer.setAddress("123 Complete Street");
        completeCustomer.setPhoneNumber("555-0123");
        completeCustomer.setPreferredPaymentMethod("Credit Card");
        completeCustomer.setEmailNotifications(true);
        
        System.out.println("Customer created:");
        System.out.println("  Username: " + completeCustomer.getUsername());
        System.out.println("  Full Name: " + completeCustomer.getFullName());
        System.out.println("  Email: " + completeCustomer.getEmail());
        System.out.println("  Address: " + completeCustomer.getAddress());
        System.out.println("  Phone: " + completeCustomer.getPhoneNumber());
        System.out.println("  Payment Method: " + completeCustomer.getPreferredPaymentMethod());
        System.out.println("  Email Notifications: " + completeCustomer.isEmailNotifications());
        
        boolean needsProfileCompletion2 = isIncompleteProfile(completeCustomer);
        System.out.println("  Needs profile completion: " + needsProfileCompletion2);
        System.out.println();
        
        // Test 3: Partially complete profile
        System.out.println("3. Testing customer with partially complete profile:");
        Customer partialCustomer = new Customer("partial_user", "hashedPassword", 
                                               "partial@test.com", "Partial", "User");
        partialCustomer.setAddress("456 Partial Avenue");
        // Missing phone and payment method
        
        System.out.println("Customer created:");
        System.out.println("  Username: " + partialCustomer.getUsername());
        System.out.println("  Full Name: " + partialCustomer.getFullName());
        System.out.println("  Email: " + partialCustomer.getEmail());
        System.out.println("  Address: " + partialCustomer.getAddress());
        System.out.println("  Phone: " + partialCustomer.getPhoneNumber());
        System.out.println("  Payment Method: " + partialCustomer.getPreferredPaymentMethod());
        
        boolean needsProfileCompletion3 = isIncompleteProfile(partialCustomer);
        System.out.println("  Needs profile completion: " + needsProfileCompletion3);
        System.out.println();
        
        System.out.println("=== Profile Completion Test Results ===");
        System.out.println("New customer (empty profile): " + (needsProfileCompletion ? "✓ DETECTED" : "✗ MISSED"));
        System.out.println("Complete customer: " + (!needsProfileCompletion2 ? "✓ COMPLETE" : "✗ FALSE POSITIVE"));
        System.out.println("Partial customer: " + (needsProfileCompletion3 ? "✓ DETECTED" : "✗ MISSED"));
        System.out.println();
        
        System.out.println("=== Expected Login Flow ===");
        System.out.println("1. User logs in for the first time");
        System.out.println("2. System checks if profile is incomplete using isFirstLoginOrIncompleteProfile()");
        System.out.println("3. If incomplete, system prompts: '=== COMPLETE YOUR PROFILE ==='");
        System.out.println("4. System guides user through completeCustomerProfile() method");
        System.out.println("5. User provides: Address, Phone, Payment Method, Email Preferences");
        System.out.println("6. System saves profile using updateCustomerProfile()");
        System.out.println("7. User proceeds to main dashboard with complete profile");
        System.out.println();
        
        System.out.println("=== Profile Update Features ===");
        System.out.println("- Users can update profile anytime via 'Profile Settings' menu");
        System.out.println("- Individual field updates: Address, Phone, Payment Method, Notifications");
        System.out.println("- Complete profile update option available");
        System.out.println("- Real-time validation and database updates");
    }
    
    /**
     * Helper method to check if profile is incomplete (same logic as in Main.java)
     */
    private static boolean isIncompleteProfile(Customer customer) {
        return customer.getAddress() == null || customer.getAddress().trim().isEmpty() ||
               customer.getPhoneNumber() == null || customer.getPhoneNumber().trim().isEmpty() ||
               customer.getPreferredPaymentMethod() == null || customer.getPreferredPaymentMethod().trim().isEmpty();
    }
}
