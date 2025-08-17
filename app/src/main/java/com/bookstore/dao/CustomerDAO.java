package com.bookstore.dao;

import com.bookstore.model.Customer;
import com.bookstore.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    public int addCustomer(Customer customer) {
        String sql = "INSERT INTO Customers (name, email, address) VALUES (?, ?, ?)";
        int customerId = -1;
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getEmail());
            pstmt.setString(3, customer.getAddress());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        customerId = rs.getInt(1);
                        customer.setCustomerId(customerId); // Update ID for Customer object
                    }
                }
            }
        } catch (SQLException e) {
            // Log detailed error in real application
            System.err.println("Error adding customer: " + e.getMessage());
            // For UNIQUE email, you can check specific error to notify
            if (e.getErrorCode() == 1062) { // MySQL error code for duplicate entry for key 'email'
                System.err.println("Customer with this email already exists.");
            }
        }
        return customerId;
    }

    public Customer getCustomerById(int customerId) {
        String sql = "SELECT * FROM Customers WHERE customer_id = ?";
        Customer customer = null;
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    customer = new Customer();
                    customer.setCustomerId(rs.getInt("customer_id"));
                    customer.setName(rs.getString("name"));
                    customer.setEmail(rs.getString("email"));
                    customer.setAddress(rs.getString("address"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting customer by ID: " + e.getMessage());
        }
        return customer;
    }

    public Customer getCustomerByEmail(String email) {
        String sql = "SELECT * FROM Customers WHERE email = ?";
        Customer customer = null;
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    customer = new Customer();
                    customer.setCustomerId(rs.getInt("customer_id"));
                    customer.setName(rs.getString("name"));
                    customer.setEmail(rs.getString("email"));
                    customer.setAddress(rs.getString("address"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting customer by email: " + e.getMessage());
        }
        return customer;
    }

    public boolean updateCustomer(Customer customer) {
        String sql = "UPDATE Customers SET name = ?, email = ?, phone = ?, address = ?, " +
                    "city = ?, state = ?, postal_code = ?, country = ?, " +
                    "preferred_payment_method = ?, email_notifications = ? " +
                    "WHERE customer_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getEmail());
            pstmt.setString(3, customer.getPhoneNumber());
            pstmt.setString(4, customer.getAddress());
            pstmt.setString(5, customer.getCity());
            pstmt.setString(6, customer.getState());
            pstmt.setString(7, customer.getPostalCode());
            pstmt.setString(8, customer.getCountry());
            pstmt.setString(9, customer.getPreferredPaymentMethod());
            pstmt.setBoolean(10, customer.isEmailNotifications());
            pstmt.setInt(11, customer.getCustomerId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating customer: " + e.getMessage());
            if (e.getErrorCode() == 1062) { // MySQL error code for duplicate entry for key 'email'
                System.err.println("Cannot update: Customer with this email already exists.");
            }
            return false;
        }
    }

    public boolean deleteCustomer(int customerId) {
        // Note: Need to handle foreign key constraints
        // If there are orders associated with this customer, you can:
        // 1. Configure ON DELETE CASCADE on database (orders also deleted)
        // 2. Delete all orders associated before deleting customer
        // 3. Don't allow deletion if there are orders
        String sql = "DELETE FROM Customers WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting customer: " + e.getMessage());
            if (e.getErrorCode() == 1451) { // MySQL error code for foreign key constraint fail
                System.err.println("Cannot delete customer: There are existing orders associated with this customer.");
            }
            return false;
        }
    }

    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM Customers";
        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerId(rs.getInt("customer_id"));
                customer.setName(rs.getString("name"));
                customer.setEmail(rs.getString("email"));
                customer.setAddress(rs.getString("address"));
                customers.add(customer);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all customers: " + e.getMessage());
        }
        return customers;
    }

    /**
     * Get customer by user ID (for registered customers)
     * @param userId The user ID from Users table
     * @return Customer object if found, null otherwise
     */
    public Customer getCustomerByUserId(int userId) {
        String sql = "SELECT * FROM Customers WHERE user_id = ?";
        Customer customer = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    customer = new Customer();
                    customer.setCustomerId(rs.getInt("customer_id"));
                    customer.setName(rs.getString("name"));
                    customer.setEmail(rs.getString("email"));
                    customer.setAddress(rs.getString("address"));
                    // Set other customer fields as needed
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting customer by user ID: " + e.getMessage());
        }
        return customer;
    }



    /**
     * Update customer profile by user ID (for registered customers)
     * @param userId The user ID
     * @param customer Customer object with updated information
     * @return true if update successful, false otherwise
     */
    public boolean updateCustomerByUserId(int userId, Customer customer) {
        String sql = "UPDATE Customers SET name = ?, phone = ?, address = ?, " +
                    "city = ?, state = ?, postal_code = ?, country = ?, " +
                    "preferred_payment_method = ?, email_notifications = ? " +
                    "WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getPhoneNumber());
            pstmt.setString(3, customer.getAddress());
            pstmt.setString(4, customer.getCity());
            pstmt.setString(5, customer.getState());
            pstmt.setString(6, customer.getPostalCode());
            pstmt.setString(7, customer.getCountry());
            pstmt.setString(8, customer.getPreferredPaymentMethod());
            pstmt.setBoolean(9, customer.isEmailNotifications());
            pstmt.setInt(10, userId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating customer by user ID: " + e.getMessage());
            return false;
        }
    }
}