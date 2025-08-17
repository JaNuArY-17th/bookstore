package com.bookstore.controller;

import com.bookstore.dao.CustomerDAO;
import com.bookstore.dao.UserDAO;
import com.bookstore.model.Customer;
import com.bookstore.model.User;
import com.bookstore.service.AuthService;
import com.bookstore.util.DisplayFormatter;
import com.bookstore.util.InputValidator;
import com.bookstore.util.PasswordUtil;

import java.util.List;

/**
 * Controller for authentication and user management operations
 * Extracted from Main.java to improve code organization
 */
public class AuthenticationController {
    private AuthService authService;
    private UserDAO userDAO;
    private CustomerDAO customerDAO;

    public AuthenticationController(AuthService authService, UserDAO userDAO, CustomerDAO customerDAO) {
        this.authService = authService;
        this.userDAO = userDAO;
        this.customerDAO = customerDAO;
    }

    /**
     * Perform user login
     * @return true if login successful and should continue, false to exit
     */
    public boolean performLogin() {
        System.out.println("=== LOGIN ===");
        String username = InputValidator.getTrimmedStringInput("Username: ");

        if (username.isEmpty()) {
            System.out.println("Username cannot be empty.");
            System.out.println("Please try again.\n");
            return true; // Return to login menu
        }

        String password = InputValidator.getMaskedPassword("Password: ");

        if (authService.login(username, password)) {
            User user = authService.getCurrentUser();
            System.out.println("Login successful! Welcome, " + user.getFullName());

            // Check if this is a customer's first login and profile is incomplete
            if (user.isCustomer() && isFirstLoginOrIncompleteProfile((Customer) user)) {
                System.out.println("\n=== COMPLETE YOUR PROFILE ===");
                System.out.println("To provide you with the best service, please complete your profile information.");

                if (completeCustomerProfile((Customer) user)) {
                    System.out.println("Profile completed successfully!");
                } else {
                    System.out.println("Profile completion was skipped. You can update it later in Profile Settings.");
                }
                System.out.println();
            }

            return true;
        } else {
            System.out.println("Invalid username or password.");
            System.out.println("Please try again.\n");
            return true; // Return to login menu
        }
    }

    /**
     * Perform user signup
     * @return true to continue, false to exit
     */
    public boolean performSignup() {
        System.out.println("=== CUSTOMER SIGN UP ===");
        String username = InputValidator.getTrimmedStringInput("Username: ");

        if (username.isEmpty()) {
            System.out.println("Username cannot be empty.");
            System.out.println("Please try again.\n");
            return true;
        }

        // Check if username already exists
        if (userDAO.getUserByUsername(username) != null) {
            System.out.println("Username already exists. Please choose a different username.");
            System.out.println("Please try again.\n");
            return true;
        }

        String email = InputValidator.getTrimmedStringInput("Email: ");
        if (email.isEmpty()) {
            System.out.println("Email cannot be empty.");
            System.out.println("Please try again.\n");
            return true;
        }

        String firstName = InputValidator.getTrimmedStringInput("First Name: ");
        if (firstName.isEmpty()) {
            System.out.println("First name cannot be empty.");
            System.out.println("Please try again.\n");
            return true;
        }

        String lastName = InputValidator.getTrimmedStringInput("Last Name: ");
        if (lastName.isEmpty()) {
            System.out.println("Last name cannot be empty.");
            System.out.println("Please try again.\n");
            return true;
        }

        String password = InputValidator.getMaskedPassword("Password: ");
        String confirmPassword = InputValidator.getMaskedPassword("Confirm Password: ");

        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match.");
            System.out.println("Please try again.\n");
            return true;
        }

        // Create customer account
        String hashedPassword = PasswordUtil.hashPassword(password);
        Customer newCustomer = new Customer(username, hashedPassword, email, firstName, lastName);

        int userId = userDAO.addUser(newCustomer);
        if (userId != -1) {
            System.out.println("Account created successfully! You can now login.");
            System.out.println();
            return true;
        } else {
            System.out.println("Failed to create account. Please try again.");
            System.out.println();
            return true;
        }
    }

    /**
     * Display and handle account management menu
     */
    public void showAccountManagementMenu() {
        System.out.println("=== ACCOUNT MANAGEMENT ===");
        System.out.println("1. Create New Admin Account");
        System.out.println("2. Create New Customer Account");
        System.out.println("3. View All Users");
        System.out.println("0. Back to Main Menu");

        int choice = InputValidator.getIntInput("Enter your choice: ");

        switch (choice) {
            case 1:
                createAdminAccount();
                break;
            case 2:
                createCustomerAccount();
                break;
            case 3:
                viewAllUsers();
                break;
            case 0:
                return;
            default:
                System.out.println("Invalid choice. Please try again.");
                showAccountManagementMenu();
        }
    }

    /**
     * Create a new admin account
     */
    public void createAdminAccount() {
        System.out.println("=== CREATE ADMIN ACCOUNT ===");

        String username = InputValidator.getTrimmedStringInput("Username: ");
        if (username.isEmpty()) {
            System.out.println("Username cannot be empty.");
            return;
        }

        // Check if username already exists
        if (userDAO.getUserByUsername(username) != null) {
            System.out.println("Username already exists. Please choose a different username.");
            return;
        }

        String email = InputValidator.getTrimmedStringInput("Email: ");
        if (email.isEmpty()) {
            System.out.println("Email cannot be empty.");
            return;
        }

        String firstName = InputValidator.getTrimmedStringInput("First Name: ");
        if (firstName.isEmpty()) {
            System.out.println("First name cannot be empty.");
            return;
        }

        String lastName = InputValidator.getTrimmedStringInput("Last Name: ");
        if (lastName.isEmpty()) {
            System.out.println("Last name cannot be empty.");
            return;
        }

        String password = InputValidator.getMaskedPassword("Password: ");
        String confirmPassword = InputValidator.getMaskedPassword("Confirm Password: ");

        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match.");
            return;
        }

        if (authService.registerAdmin(username, password, email, firstName, lastName)) {
            System.out.println("Admin account created successfully!");
        } else {
            System.out.println("Failed to create admin account.");
        }
    }

    /**
     * Create a new customer account
     */
    public void createCustomerAccount() {
        System.out.println("=== CREATE CUSTOMER ACCOUNT ===");

        String username = InputValidator.getTrimmedStringInput("Username: ");
        if (username.isEmpty()) {
            System.out.println("Username cannot be empty.");
            return;
        }

        // Check if username already exists
        if (userDAO.getUserByUsername(username) != null) {
            System.out.println("Username already exists. Please choose a different username.");
            return;
        }

        String email = InputValidator.getTrimmedStringInput("Email: ");
        if (email.isEmpty()) {
            System.out.println("Email cannot be empty.");
            return;
        }

        String firstName = InputValidator.getTrimmedStringInput("First Name: ");
        if (firstName.isEmpty()) {
            System.out.println("First name cannot be empty.");
            return;
        }

        String lastName = InputValidator.getTrimmedStringInput("Last Name: ");
        if (lastName.isEmpty()) {
            System.out.println("Last name cannot be empty.");
            return;
        }

        String password = InputValidator.getMaskedPassword("Password: ");
        String confirmPassword = InputValidator.getMaskedPassword("Confirm Password: ");

        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match.");
            return;
        }

        if (authService.registerCustomer(username, password, email, firstName, lastName)) {
            System.out.println("Customer account created successfully!");
        } else {
            System.out.println("Failed to create customer account.");
        }
    }

    /**
     * View all users in the system
     */
    public void viewAllUsers() {
        System.out.println("=== ALL USERS ===");
        try {
            List<User> users = authService.getUserDAO().getAllUsers();
            DisplayFormatter.displayUsersTable(users);
        } catch (SecurityException e) {
            System.out.println("Access denied: " + e.getMessage());
        }
    }

    /**
     * Check if this is a customer's first login or if their profile is incomplete
     */
    private boolean isFirstLoginOrIncompleteProfile(Customer customer) {
        // Check if essential customer information is missing
        return customer.getAddress() == null || customer.getAddress().trim().isEmpty() ||
               customer.getPhoneNumber() == null || customer.getPhoneNumber().trim().isEmpty() ||
               customer.getPreferredPaymentMethod() == null || customer.getPreferredPaymentMethod().trim().isEmpty();
    }

    /**
     * Guide the customer through completing their profile
     */
    private boolean completeCustomerProfile(Customer customer) {
        System.out.println("\nLet's set up your profile information:");

        try {
            // Address information
            if (customer.getAddress() == null || customer.getAddress().trim().isEmpty()) {
                String address = InputValidator.getTrimmedStringInput("Street Address: ");
                if (!address.isEmpty()) {
                    customer.setAddress(address);
                }
            }

            // City
            if (customer.getCity() == null || customer.getCity().trim().isEmpty()) {
                String city = InputValidator.getTrimmedStringInput("City: ");
                if (!city.isEmpty()) {
                    customer.setCity(city);
                }
            }

            // State
            if (customer.getState() == null || customer.getState().trim().isEmpty()) {
                String state = InputValidator.getTrimmedStringInput("State/Province: ");
                if (!state.isEmpty()) {
                    customer.setState(state);
                }
            }

            // Postal Code
            if (customer.getPostalCode() == null || customer.getPostalCode().trim().isEmpty()) {
                String postalCode = InputValidator.getTrimmedStringInput("Postal/ZIP Code: ");
                if (!postalCode.isEmpty()) {
                    customer.setPostalCode(postalCode);
                }
            }

            // Country (optional, defaults to USA)
            String country = InputValidator.getTrimmedStringInput("Country (default: USA): ");
            if (!country.isEmpty()) {
                customer.setCountry(country);
            } else {
                customer.setCountry("USA");
            }

            // Phone number
            if (customer.getPhoneNumber() == null || customer.getPhoneNumber().trim().isEmpty()) {
                String phone = InputValidator.getTrimmedStringInput("Phone Number: ");
                if (!phone.isEmpty()) {
                    customer.setPhoneNumber(phone);
                }
            }

            // Preferred payment method
            if (customer.getPreferredPaymentMethod() == null || customer.getPreferredPaymentMethod().trim().isEmpty()) {
                System.out.println("\nPreferred Payment Method:");
                System.out.println("1. Credit Card");
                System.out.println("2. Debit Card");
                System.out.println("3. PayPal");
                System.out.println("4. Bank Transfer");
                System.out.println("5. Cash on Delivery");

                int paymentChoice = InputValidator.getIntInput("Select payment method (1-5): ");
                String paymentMethod = switch (paymentChoice) {
                    case 1 -> "Credit Card";
                    case 2 -> "Debit Card";
                    case 3 -> "PayPal";
                    case 4 -> "Bank Transfer";
                    case 5 -> "Cash on Delivery";
                    default -> "Credit Card"; // Default
                };
                customer.setPreferredPaymentMethod(paymentMethod);
            }

            // Email notifications preference
            boolean emailChoice = InputValidator.getConfirmation("Would you like to receive email notifications about your orders? (y/n): ");
            customer.setEmailNotifications(emailChoice);

            // Update the customer in the database
            return updateCustomerProfile(customer);

        } catch (Exception e) {
            System.out.println("Error completing profile: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update customer profile in the database
     */
    private boolean updateCustomerProfile(Customer customer) {
        try {
            System.out.println("Saving profile information...");

            // Get the customer record from database using user ID
            Customer dbCustomer = customerDAO.getCustomerByUserId(customer.getUserId());
            if (dbCustomer == null) {
                System.err.println("Customer record not found in database.");
                return false;
            }

            // Update the database customer record with new information
            dbCustomer.setAddress(customer.getAddress());
            dbCustomer.setPhoneNumber(customer.getPhoneNumber());
            dbCustomer.setPreferredPaymentMethod(customer.getPreferredPaymentMethod());
            dbCustomer.setEmailNotifications(customer.isEmailNotifications());

            // Parse address for city, state, postal code if needed
            if (customer.getCity() != null) {
                dbCustomer.setCity(customer.getCity());
            }
            if (customer.getState() != null) {
                dbCustomer.setState(customer.getState());
            }
            if (customer.getPostalCode() != null) {
                dbCustomer.setPostalCode(customer.getPostalCode());
            }
            if (customer.getCountry() != null) {
                dbCustomer.setCountry(customer.getCountry());
            } else {
                dbCustomer.setCountry("USA"); // Default country
            }

            // Update in database using CustomerDAO
            boolean success = customerDAO.updateCustomerByUserId(customer.getUserId(), dbCustomer);

            if (success) {
                System.out.println("Profile information saved successfully!");
            } else {
                System.err.println("Failed to save profile information to database.");
            }

            return success;

        } catch (Exception e) {
            System.err.println("Error updating customer profile: " + e.getMessage());
            return false;
        }
    }

    /**
     * Display and handle user profile menu
     */
    public void showUserProfileMenu() {
        System.out.println("=== MY PROFILE ===");
        User currentUser = authService.getCurrentUser();
        System.out.println("Username: " + currentUser.getUsername());
        System.out.println("Name: " + currentUser.getFullName());
        System.out.println("Email: " + currentUser.getEmail());
        System.out.println("Role: " + currentUser.getRole());
        System.out.println("Last Login: " + (currentUser.getLastLogin() != null ? currentUser.getLastLogin() : "Never"));

        // Show customer-specific information
        if (currentUser.isCustomer()) {
            Customer customer = (Customer) currentUser;
            System.out.println("\n--- Customer Information ---");
            System.out.println("Address: " + (customer.getAddress() != null ? customer.getAddress() : "Not set"));
            System.out.println("City: " + (customer.getCity() != null ? customer.getCity() : "Not set"));
            System.out.println("State: " + (customer.getState() != null ? customer.getState() : "Not set"));
            System.out.println("Postal Code: " + (customer.getPostalCode() != null ? customer.getPostalCode() : "Not set"));
            System.out.println("Country: " + (customer.getCountry() != null ? customer.getCountry() : "Not set"));
            System.out.println("Phone: " + (customer.getPhoneNumber() != null ? customer.getPhoneNumber() : "Not set"));
            System.out.println("Preferred Payment: " + (customer.getPreferredPaymentMethod() != null ? customer.getPreferredPaymentMethod() : "Not set"));
            System.out.println("Email Notifications: " + (customer.isEmailNotifications() ? "Enabled" : "Disabled"));
        }

        System.out.println();

        System.out.println("1. Change Password");
        if (currentUser.isCustomer()) {
            System.out.println("2. Update Profile Information");
        }
        System.out.println("0. Back to Main Menu");

        int choice = InputValidator.getIntInput("Enter your choice: ");

        switch (choice) {
            case 1:
                changePassword();
                break;
            case 2:
                if (currentUser.isCustomer()) {
                    updateCustomerProfileMenu((Customer) currentUser);
                } else {
                    System.out.println("Invalid choice.");
                }
                break;
            case 0:
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }

    /**
     * Change user password
     */
    public void changePassword() {
        System.out.println("=== CHANGE PASSWORD ===");

        String currentPassword = InputValidator.getStringInput("Current Password: ");
        String newPassword = InputValidator.getStringInput("New Password: ");

        if (!PasswordUtil.isPasswordStrong(newPassword)) {
            System.out.println("Password is not strong enough.");
            System.out.println(PasswordUtil.getPasswordRequirements());
            return;
        }

        String confirmPassword = InputValidator.getStringInput("Confirm New Password: ");

        if (!newPassword.equals(confirmPassword)) {
            System.out.println("Passwords do not match.");
            return;
        }

        if (authService.changePassword(currentPassword, newPassword)) {
            System.out.println("Password changed successfully.");
        } else {
            System.out.println("Failed to change password. Please check your current password.");
        }
    }

    /**
     * Display and handle customer profile update menu
     */
    public void updateCustomerProfileMenu(Customer customer) {
        System.out.println("=== UPDATE PROFILE INFORMATION ===");
        System.out.println("Current Information:");
        System.out.println("Address: " + (customer.getAddress() != null ? customer.getAddress() : "Not set"));
        System.out.println("City: " + (customer.getCity() != null ? customer.getCity() : "Not set"));
        System.out.println("State: " + (customer.getState() != null ? customer.getState() : "Not set"));
        System.out.println("Postal Code: " + (customer.getPostalCode() != null ? customer.getPostalCode() : "Not set"));
        System.out.println("Country: " + (customer.getCountry() != null ? customer.getCountry() : "Not set"));
        System.out.println("Phone: " + (customer.getPhoneNumber() != null ? customer.getPhoneNumber() : "Not set"));
        System.out.println("Preferred Payment: " + (customer.getPreferredPaymentMethod() != null ? customer.getPreferredPaymentMethod() : "Not set"));
        System.out.println("Email Notifications: " + (customer.isEmailNotifications() ? "Enabled" : "Disabled"));
        System.out.println();

        System.out.println("What would you like to update?");
        System.out.println("1. Street Address");
        System.out.println("2. City/State/Postal Code");
        System.out.println("3. Phone Number");
        System.out.println("4. Preferred Payment Method");
        System.out.println("5. Email Notifications");
        System.out.println("6. Update All Information");
        System.out.println("0. Back to Profile Menu");

        int choice = InputValidator.getIntInput("Enter your choice: ");

        switch (choice) {
            case 1:
                updateAddress(customer);
                break;
            case 2:
                updateCityStatePostal(customer);
                break;
            case 3:
                updatePhoneNumber(customer);
                break;
            case 4:
                updatePaymentMethod(customer);
                break;
            case 5:
                updateEmailNotifications(customer);
                break;
            case 6:
                completeCustomerProfile(customer);
                break;
            case 0:
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }

    /**
     * Update customer address
     */
    private void updateAddress(Customer customer) {
        String newAddress = InputValidator.getTrimmedStringInput("Enter new street address (current: " +
                        (customer.getAddress() != null ? customer.getAddress() : "Not set") + "): ");
        if (!newAddress.isEmpty()) {
            customer.setAddress(newAddress);
            if (updateCustomerProfile(customer)) {
                System.out.println("Street address updated successfully!");
            }
        }
    }

    /**
     * Update customer city, state, and postal code
     */
    private void updateCityStatePostal(Customer customer) {
        System.out.println("=== UPDATE CITY/STATE/POSTAL CODE ===");

        String newCity = InputValidator.getTrimmedStringInput("Enter new city (current: " +
                        (customer.getCity() != null ? customer.getCity() : "Not set") + "): ");
        if (!newCity.isEmpty()) {
            customer.setCity(newCity);
        }

        String newState = InputValidator.getTrimmedStringInput("Enter new state/province (current: " +
                        (customer.getState() != null ? customer.getState() : "Not set") + "): ");
        if (!newState.isEmpty()) {
            customer.setState(newState);
        }

        String newPostalCode = InputValidator.getTrimmedStringInput("Enter new postal/ZIP code (current: " +
                        (customer.getPostalCode() != null ? customer.getPostalCode() : "Not set") + "): ");
        if (!newPostalCode.isEmpty()) {
            customer.setPostalCode(newPostalCode);
        }

        String newCountry = InputValidator.getTrimmedStringInput("Enter new country (current: " +
                        (customer.getCountry() != null ? customer.getCountry() : "Not set") + "): ");
        if (!newCountry.isEmpty()) {
            customer.setCountry(newCountry);
        }

        if (updateCustomerProfile(customer)) {
            System.out.println("Location information updated successfully!");
        }
    }

    /**
     * Update customer phone number
     */
    private void updatePhoneNumber(Customer customer) {
        String newPhone = InputValidator.getTrimmedStringInput("Enter new phone number (current: " +
                        (customer.getPhoneNumber() != null ? customer.getPhoneNumber() : "Not set") + "): ");
        if (!newPhone.isEmpty()) {
            customer.setPhoneNumber(newPhone);
            if (updateCustomerProfile(customer)) {
                System.out.println("Phone number updated successfully!");
            }
        }
    }

    /**
     * Update customer payment method
     */
    private void updatePaymentMethod(Customer customer) {
        System.out.println("Current payment method: " +
                          (customer.getPreferredPaymentMethod() != null ? customer.getPreferredPaymentMethod() : "Not set"));
        System.out.println("\nSelect new payment method:");
        System.out.println("1. Credit Card");
        System.out.println("2. Debit Card");
        System.out.println("3. PayPal");
        System.out.println("4. Bank Transfer");
        System.out.println("5. Cash on Delivery");

        int choice = InputValidator.getIntInput("Select payment method (1-5): ");
        String paymentMethod = switch (choice) {
            case 1 -> "Credit Card";
            case 2 -> "Debit Card";
            case 3 -> "PayPal";
            case 4 -> "Bank Transfer";
            case 5 -> "Cash on Delivery";
            default -> customer.getPreferredPaymentMethod(); // Keep current
        };

        if (paymentMethod != null && !paymentMethod.equals(customer.getPreferredPaymentMethod())) {
            customer.setPreferredPaymentMethod(paymentMethod);
            if (updateCustomerProfile(customer)) {
                System.out.println("Payment method updated successfully!");
            }
        }
    }

    /**
     * Update customer email notification preferences
     */
    private void updateEmailNotifications(Customer customer) {
        System.out.println("Current email notifications: " + (customer.isEmailNotifications() ? "Enabled" : "Disabled"));
        boolean newSetting = InputValidator.getConfirmation("Enable email notifications? (y/n): ");

        if (newSetting != customer.isEmailNotifications()) {
            customer.setEmailNotifications(newSetting);
            if (updateCustomerProfile(customer)) {
                System.out.println("Email notification preference updated successfully!");
            }
        }
    }
}
