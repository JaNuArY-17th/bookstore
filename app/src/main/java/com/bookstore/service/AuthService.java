package com.bookstore.service;

import com.bookstore.dao.UserDAO;
import com.bookstore.model.User;
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
     * Register a new user (admin only)
     */
    public boolean registerUser(String username, String password, String email, 
                               String firstName, String lastName, Role role) {
        if (!hasAdminPermission()) {
            System.err.println("Access denied: Admin permission required to register users.");
            return false;
        }

        String hashedPassword = PasswordUtil.hashPassword(password);
        User newUser = new User(username, hashedPassword, email, firstName, lastName, role);
        
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
        User existingAdmin = userDAO.getUserByUsername("admin");
        if (existingAdmin == null) {
            String hashedPassword = PasswordUtil.hashPassword("admin123");
            User adminUser = new User("admin", hashedPassword, "admin@bookstore.com", 
                                    "System", "Administrator", Role.ADMIN);
            
            int userId = userDAO.addUser(adminUser);
            if (userId != -1) {
                System.out.println("Default admin user created:");
                System.out.println("Username: admin");
                System.out.println("Password: admin123");
                System.out.println("Please change the password after first login.");
            }
        }
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