package com.bookstore.service;

import com.bookstore.dao.UserDAO;
import com.bookstore.model.User;
import com.bookstore.model.Admin;
import com.bookstore.model.Customer;
import com.bookstore.model.Role;
import com.bookstore.util.security.PasswordUtil;

/**
 * Authentication and Authorization Service
 */
public class AuthService {
    private UserDAO userDAO;
    private User currentUser;
    private CartService cartService;

    public AuthService() {
        this.userDAO = new UserDAO();
        this.cartService = new CartService();
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
        this.cartService.clearCart(); // Clear cart on logout
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

    // Note: hasAdminPermission() method removed as it duplicated isCurrentUserAdmin() functionality

    /**
     * Register a new customer (admin only)
     */
    public boolean registerCustomer(String username, String password, String email, 
                                   String firstName, String lastName) {
        if (!isCurrentUserAdmin()) {
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
        if (!isCurrentUserAdmin()) {
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
        if (!isCurrentUserAdmin()) {
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

    // Note: createDefaultAdminIfNeeded() method removed as it was empty and unused

    /**
     * Get user DAO for admin operations
     */
    public UserDAO getUserDAO() {
        if (!isCurrentUserAdmin()) {
            throw new SecurityException("Access denied: Admin permission required.");
        }
        return userDAO;
    }

    /**
     * Get user DAO for system initialization (bypasses security check)
     * This method should only be used during system bootstrap
     */
    public UserDAO getUserDAOForInitialization() {
        return userDAO;
    }

    /**
     * Register the first admin during system initialization (bypasses security check)
     * This method should only be used during system bootstrap
     */
    public boolean registerAdminForInitialization(String username, String password, String email,
                                                 String firstName, String lastName) {
        String hashedPassword = PasswordUtil.hashPassword(password);
        Admin newAdmin = new Admin(username, hashedPassword, email, firstName, lastName);
        return userDAO.addUser(newAdmin) != -1;
    }

    /**
     * Get cart service for current user
     * @return CartService instance
     */
    public CartService getCartService() {
        return cartService;
    }
}