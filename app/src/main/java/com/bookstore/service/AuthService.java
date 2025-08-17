package com.bookstore.service;

import com.bookstore.dao.UserDAO;
import com.bookstore.model.User;
import com.bookstore.model.Admin;
import com.bookstore.model.Customer;
import com.bookstore.model.Role;
import com.bookstore.util.PasswordUtil;

/**
 * Authentication and Authorization Service
 */
public class AuthService {
    private UserDAO userDAO;
    private User currentUser;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    /**
     * Authenticate user with username and password
     */
    public boolean login(String username, String password) {
        User user = userDAO.getUserByUsername(username);
        
        if (user != null && PasswordUtil.verifyPassword(password, user.getPassword())) {
            this.currentUser = user;
            userDAO.updateLastLogin(user.getUserId());
            return true;
        }
        return false;
    }

    /**
     * Logout current user
     */
    public void logout() {
        this.currentUser = null;
    }

    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Get current logged-in user
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Check if current user is admin
     */
    public boolean isCurrentUserAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }

    /**
     * Check if current user has permission for admin operations
     */
    public boolean hasAdminPermission() {
        return isCurrentUserAdmin();
    }

    /**
     * Register a new customer (admin only)
     */
    public boolean registerCustomer(String username, String password, String email, 
                                   String firstName, String lastName) {
        if (!hasAdminPermission()) {
            System.err.println("Access denied: Admin permission required to register customers.");
            return false;
        }

        String hashedPassword = PasswordUtil.hashPassword(password);
        Customer newCustomer = new Customer(username, hashedPassword, email, firstName, lastName);

        int userId = userDAO.addUser(newCustomer);
        return userId != -1;
    }

    /**
     * Register a new admin (admin only)
     */
    public boolean registerAdmin(String username, String password, String email,
                                String firstName, String lastName) {
        if (!hasAdminPermission()) {
            System.err.println("Access denied: Admin permission required to register admins.");
            return false;
        }

        String hashedPassword = PasswordUtil.hashPassword(password);
        Admin newAdmin = new Admin(username, hashedPassword, email, firstName, lastName);

        int userId = userDAO.addUser(newAdmin);
        return userId != -1;
    }

    /**
     * Register a new user with specified role (admin only)
     */
    public boolean registerUser(String username, String password, String email,
                               String firstName, String lastName, Role role) {
        if (!hasAdminPermission()) {
            System.err.println("Access denied: Admin permission required to register users.");
            return false;
        }

        String hashedPassword = PasswordUtil.hashPassword(password);
        User newUser;

        if (role == Role.ADMIN) {
            newUser = new Admin(username, hashedPassword, email, firstName, lastName);
        } else {
            newUser = new Customer(username, hashedPassword, email, firstName, lastName);
        }

        int userId = userDAO.addUser(newUser);
        return userId != -1;
    }

    /**
     * Change password for current user
     */
    public boolean changePassword(String oldPassword, String newPassword) {
        if (!isLoggedIn()) {
            System.err.println("User must be logged in to change password.");
            return false;
        }

        if (!PasswordUtil.verifyPassword(oldPassword, currentUser.getPassword())) {
            System.err.println("Current password is incorrect.");
            return false;
        }

        String hashedNewPassword = PasswordUtil.hashPassword(newPassword);
        boolean success = userDAO.changePassword(currentUser.getUserId(), hashedNewPassword);
        
        if (success) {
            currentUser.setPassword(hashedNewPassword);
        }
        
        return success;
    }

    /**
     * Create default admin user if none exists
     */
    public void createDefaultAdminIfNeeded() {
        // Admin users are managed through database setup
        // No automatic admin creation needed
    }

    /**
     * Get user DAO for admin operations
     */
    public UserDAO getUserDAO() {
        if (!hasAdminPermission()) {
            throw new SecurityException("Access denied: Admin permission required.");
        }
        return userDAO;
    }
}