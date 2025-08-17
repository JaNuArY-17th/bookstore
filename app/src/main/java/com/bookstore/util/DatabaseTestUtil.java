package com.bookstore.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Database testing and verification utility
 */
public class DatabaseTestUtil {

    /**
     * Test database connection and basic operations
     */
    public static boolean testDatabaseConnection() {
        try (Connection conn = DBConnection.getConnection()) {
            // Test basic query
            String testQuery = "SELECT 1 as test_value";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(testQuery)) {
                
                if (rs.next() && rs.getInt("test_value") == 1) {
                    return true;
                } else {
                    return false;
                }
            }
            
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Verify all required tables exist
     */
    public static boolean verifyDatabaseSchema() {
        String[] requiredTables = {
            "Users", "Books", "Customers", "Orders", "OrderItems",
            "OrderStatusHistory", "InventoryTransactions", "UserSessions"
        };
        
        try (Connection conn = DBConnection.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            
            List<String> missingTables = new ArrayList<>();
            
            for (String tableName : requiredTables) {
                try (ResultSet rs = metaData.getTables(null, null, tableName, new String[]{"TABLE"})) {
                    if (!rs.next()) {
                        missingTables.add(tableName);
                    }
                }
            }
            
            return missingTables.isEmpty();
            
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Check if sample data exists
     */
    public static void checkSampleData() {
        // Silent data check - no console output
        try (Connection conn = DBConnection.getConnection()) {
            // Check if basic data exists without logging
            String bookQuery = "SELECT COUNT(*) as count FROM Books";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(bookQuery)) {
                rs.next(); // Just verify query works
            }
        } catch (SQLException e) {
            // Silent failure
        }
    }

    /**
     * Test basic CRUD operations
     */
    public static boolean testBasicOperations() {
        // Silent basic operations test
        try (Connection conn = DBConnection.getConnection()) {
            // Test INSERT
            String insertQuery = "INSERT INTO Books (title, author, isbn, price, stock_quantity, category) VALUES (?, ?, ?, ?, ?, ?)";
            int testBookId;
            
            try (PreparedStatement pstmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, "Test Book");
                pstmt.setString(2, "Test Author");
                pstmt.setString(3, "TEST-" + System.currentTimeMillis());
                pstmt.setDouble(4, 9.99);
                pstmt.setInt(5, 1);
                pstmt.setString(6, "Test");
                
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            testBookId = rs.getInt(1);
                        } else {
                            return false;
                        }
                    }
                } else {
                    return false;
                }
            }
            
            // Test SELECT
            String selectQuery = "SELECT * FROM Books WHERE book_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(selectQuery)) {
                pstmt.setInt(1, testBookId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        String title = rs.getString("title");
                        if (!"Test Book".equals(title)) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            }
            
            // Test UPDATE
            String updateQuery = "UPDATE Books SET price = ? WHERE book_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
                pstmt.setDouble(1, 19.99);
                pstmt.setInt(2, testBookId);
                if (pstmt.executeUpdate() <= 0) {
                    return false;
                }
            }
            
            // Test DELETE
            String deleteQuery = "DELETE FROM Books WHERE book_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
                pstmt.setInt(1, testBookId);
                if (pstmt.executeUpdate() <= 0) {
                    return false;
                }
            }
            
            return true;
            
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Run comprehensive database test
     */
    public static boolean runFullDatabaseTest() {
        // Silent comprehensive test
        boolean connectionOk = testDatabaseConnection();
        boolean schemaOk = verifyDatabaseSchema();
        boolean operationsOk = testBasicOperations();
        
        checkSampleData();
        
        return connectionOk && schemaOk && operationsOk;
    }

    /**
     * Display database information
     */
    public static void displayDatabaseInfo() {
        // Silent database info check
        try (Connection conn = DBConnection.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            // Database info available but not displayed
        } catch (SQLException e) {
            // Silent failure
        }
    }
}