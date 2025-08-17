package com.bookstore.dao;

import com.bookstore.model.User;
import com.bookstore.model.Admin;
import com.bookstore.model.Customer;
import com.bookstore.model.Role;
import com.bookstore.util.database.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User operations
 */
public class UserDAO {

    public int addUser(User user) {
        String userSql = "INSERT INTO Users (username, password, email, first_name, last_name, role, is_active) VALUES (?, ?, ?, ?, ?, ?, ?)";
        int userId = -1;

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Insert into Users table
            try (PreparedStatement pstmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, user.getUsername());
                pstmt.setString(2, user.getPassword());
                pstmt.setString(3, user.getEmail());
                pstmt.setString(4, user.getFirstName());
                pstmt.setString(5, user.getLastName());
                pstmt.setString(6, user.getRole().name());
                pstmt.setBoolean(7, user.isActive());

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            userId = rs.getInt(1);
                            user.setUserId(userId);
                        }
                    }
                }
            }

            // Insert into appropriate subclass table based on role
            if (userId != -1) {
                if (user instanceof Admin) {
                    addAdminDetails(conn, (Admin) user, userId);
                } else if (user instanceof Customer) {
                    addCustomerDetails(conn, (Customer) user, userId);
                }
            }

            conn.commit(); // Commit transaction

        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback(); // Rollback on error
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Error during rollback: " + rollbackEx.getMessage());
            }

            System.err.println("Error adding user: " + e.getMessage());
            if (e.getErrorCode() == 1062) { // MySQL error code for duplicate entry
                System.err.println("Username or email already exists.");
            }
            userId = -1;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // Reset auto-commit
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }

        return userId;
    }

    /**
     * Add admin-specific details to the Admins table
     */
    private void addAdminDetails(Connection conn, Admin admin, int userId) throws SQLException {
        String adminSql = "INSERT INTO Admins (user_id, department, admin_level, permissions, employee_id, hire_date) " +
                         "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(adminSql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, admin.getDepartment());
            pstmt.setString(3, admin.getAdminLevel());

            // Convert permissions list to JSON string
            String permissionsJson = "[";
            if (admin.getPermissions() != null && !admin.getPermissions().isEmpty()) {
                for (int i = 0; i < admin.getPermissions().size(); i++) {
                    permissionsJson += "\"" + admin.getPermissions().get(i) + "\"";
                    if (i < admin.getPermissions().size() - 1) {
                        permissionsJson += ",";
                    }
                }
            }
            permissionsJson += "]";

            pstmt.setString(4, permissionsJson);
            pstmt.setString(5, null); // employee_id - can be set later
            pstmt.setDate(6, new java.sql.Date(System.currentTimeMillis())); // hire_date

            pstmt.executeUpdate();
        }
    }

    /**
     * Add customer-specific details to the Customers table
     */
    private void addCustomerDetails(Connection conn, Customer customer, int userId) throws SQLException {
        String customerSql = "INSERT INTO Customers (user_id, name, email, address, phone, " +
                           "preferred_payment_method, email_notifications, customer_type) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(customerSql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, customer.getFullName());
            pstmt.setString(3, customer.getEmail());
            pstmt.setString(4, customer.getAddress());
            pstmt.setString(5, customer.getPhoneNumber());
            pstmt.setString(6, customer.getPreferredPaymentMethod());
            pstmt.setBoolean(7, customer.isEmailNotifications());
            pstmt.setString(8, "REGISTERED"); // Default to registered customer

            pstmt.executeUpdate();

            // Set the customer ID from the generated key
            try (PreparedStatement selectStmt = conn.prepareStatement(
                    "SELECT customer_id FROM Customers WHERE user_id = ?")) {
                selectStmt.setInt(1, userId);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        customer.setCustomerId(rs.getInt("customer_id"));
                    }
                }
            }
        }
    }

    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM Users WHERE username = ? AND is_active = true";
        User user = null;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting user by username: " + e.getMessage());
        }
        return user;
    }

    public User getUserById(int userId) {
        String sql = "SELECT * FROM Users WHERE user_id = ?";
        User user = null;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
        }
        return user;
    }

    public boolean updateLastLogin(int userId) {
        String sql = "UPDATE Users SET last_login = CURRENT_TIMESTAMP WHERE user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating last login: " + e.getMessage());
            return false;
        }
    }

    public boolean updateUser(User user) {
        String sql = "UPDATE Users SET username = ?, email = ?, first_name = ?, last_name = ?, role = ?, is_active = ? WHERE user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getFirstName());
            pstmt.setString(4, user.getLastName());
            pstmt.setString(5, user.getRole().name());
            pstmt.setBoolean(6, user.isActive());
            pstmt.setInt(7, user.getUserId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        }
    }

    public boolean changePassword(int userId, String newPassword) {
        String sql = "UPDATE Users SET password = ? WHERE user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newPassword);
            pstmt.setInt(2, userId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error changing password: " + e.getMessage());
            return false;
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users ORDER BY created_at DESC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
        }
        return users;
    }

    public boolean deactivateUser(int userId) {
        String sql = "UPDATE Users SET is_active = false WHERE user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deactivating user: " + e.getMessage());
            return false;
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        Role role = Role.valueOf(rs.getString("role"));
        int userId = rs.getInt("user_id");
        User user;

        // Create appropriate subclass based on role and load role-specific data
        if (role == Role.ADMIN) {
            user = loadAdminWithDetails(userId);
        } else {
            user = loadCustomerWithDetails(userId);
        }

        // If role-specific loading failed, create basic object
        if (user == null) {
            if (role == Role.ADMIN) {
                user = new Admin();
            } else {
                user = new Customer();
            }
        }

        // Set common properties
        user.setUserId(userId);
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setRole(role);
        user.setActive(rs.getBoolean("is_active"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setLastLogin(rs.getTimestamp("last_login"));

        return user;
    }

    /**
     * Load admin with complete details from Admins table
     */
    private Admin loadAdminWithDetails(int userId) {
        String sql = "SELECT a.*, u.* FROM Admins a JOIN Users u ON a.user_id = u.user_id WHERE a.user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Admin admin = new Admin();
                    admin.setDepartment(rs.getString("department"));
                    admin.setAdminLevel(rs.getString("admin_level"));
                    // Note: permissions JSON parsing would go here if needed
                    return admin;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading admin details: " + e.getMessage());
        }
        return null;
    }

    /**
     * Load customer with complete details from Customers table
     */
    private Customer loadCustomerWithDetails(int userId) {
        String sql = "SELECT c.*, u.* FROM Customers c JOIN Users u ON c.user_id = u.user_id WHERE c.user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer();
                    customer.setCustomerId(rs.getInt("customer_id"));
                    customer.setAddress(rs.getString("address"));
                    customer.setPhoneNumber(rs.getString("phone"));
                    customer.setPreferredPaymentMethod(rs.getString("preferred_payment_method"));
                    customer.setEmailNotifications(rs.getBoolean("email_notifications"));
                    return customer;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading customer details: " + e.getMessage());
        }
        return null;
    }
}