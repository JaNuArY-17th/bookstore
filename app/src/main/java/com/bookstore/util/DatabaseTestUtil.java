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
        System.out.println("=== Database Connection Test ===");
        
        try (Connection conn = DBConnection.getConnection()) {
            System.out.println("✓ Database connection successful");
            
            // Test basic query
            String testQuery = "SELECT 1 as test_value";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(testQuery)) {
                
                if (rs.next() && rs.getInt("test_value") == 1) {
                    System.out.println("✓ Basic query execution successful");
                } else {
                    System.out.println("✗ Basic query failed");
                    return false;
                }
            }
            
            // Test database name
            String dbQuery = "SELECT DATABASE() as current_db";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(dbQuery)) {
                
                if (rs.next()) {
                    String dbName = rs.getString("current_db");
                    System.out.println("✓ Connected to database: " + dbName);
                    
                    if (!"online_bookstore_db".equals(dbName)) {
                        System.out.println("⚠ Warning: Expected 'online_bookstore_db', got '" + dbName + "'");
                    }
                }
            }
            
            return true;
            
        } catch (SQLException e) {
            System.out.println("✗ Database connection failed: " + e.getMessage());
            System.out.println("Please check:");
            System.out.println("  - XAMPP MySQL service is running");
            System.out.println("  - Database 'online_bookstore_db' exists");
            System.out.println("  - Connection settings in DBConnection.java");
            return false;
        }
    }

    /**
     * Verify all required tables exist
     */
    public static boolean verifyDatabaseSchema() {
        System.out.println("\n=== Database Schema Verification ===");
        
        String[] requiredTables = {
            "Users", "Books", "Customers", "Orders", "OrderItems",
            "OrderStatusHistory", "InventoryTransactions", "UserSessions"
        };
        
        try (Connection conn = DBConnection.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            
            List<String> missingTables = new ArrayList<>();
            
            for (String tableName : requiredTables) {
                try (ResultSet rs = metaData.getTables(null, null, tableName, new String[]{"TABLE"})) {
                    if (rs.next()) {
                        System.out.println("✓ Table '" + tableName + "' exists");
                    } else {
                        System.out.println("✗ Table '" + tableName + "' missing");
                        missingTables.add(tableName);
                    }
                }
            }
            
            if (missingTables.isEmpty()) {
                System.out.println("✓ All required tables exist");
                return true;
            } else {
                System.out.println("✗ Missing tables: " + String.join(", ", missingTables));
                System.out.println("Please run the database schema script (database/schema.sql)");
                return false;
            }
            
        } catch (SQLException e) {
            System.out.println("✗ Schema verification failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if sample data exists
     */
    public static void checkSampleData() {
        System.out.println("\n=== Sample Data Check ===");
        
        try (Connection conn = DBConnection.getConnection()) {
            
            // Check books
            String bookQuery = "SELECT COUNT(*) as count FROM Books";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(bookQuery)) {
                
                if (rs.next()) {
                    int bookCount = rs.getInt("count");
                    System.out.println("📚 Books in database: " + bookCount);
                    
                    if (bookCount == 0) {
                        System.out.println("ℹ Consider running sample_data.sql for test data");
                    }
                }
            }
            
            // Check customers
            String customerQuery = "SELECT COUNT(*) as count FROM Customers";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(customerQuery)) {
                
                if (rs.next()) {
                    int customerCount = rs.getInt("count");
                    System.out.println("👥 Customers in database: " + customerCount);
                }
            }
            
            // Check users
            String userQuery = "SELECT COUNT(*) as count FROM Users";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(userQuery)) {
                
                if (rs.next()) {
                    int userCount = rs.getInt("count");
                    System.out.println("👤 Users in database: " + userCount);
                    
                    if (userCount == 0) {
                        System.out.println("ℹ No users found - default admin will be created");
                    }
                }
            }
            
            // Check orders
            String orderQuery = "SELECT COUNT(*) as count FROM Orders";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(orderQuery)) {
                
                if (rs.next()) {
                    int orderCount = rs.getInt("count");
                    System.out.println("📦 Orders in database: " + orderCount);
                }
            }
            
        } catch (SQLException e) {
            System.out.println("✗ Sample data check failed: " + e.getMessage());
        }
    }

    /**
     * Test basic CRUD operations
     */
    public static boolean testBasicOperations() {
        System.out.println("\n=== Basic Operations Test ===");
        
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
                            System.out.println("✓ INSERT operation successful (ID: " + testBookId + ")");
                        } else {
                            System.out.println("✗ INSERT failed - no ID returned");
                            return false;
                        }
                    }
                } else {
                    System.out.println("✗ INSERT operation failed");
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
                        if ("Test Book".equals(title)) {
                            System.out.println("✓ SELECT operation successful");
                        } else {
                            System.out.println("✗ SELECT returned incorrect data");
                            return false;
                        }
                    } else {
                        System.out.println("✗ SELECT operation failed - no data found");
                        return false;
                    }
                }
            }
            
            // Test UPDATE
            String updateQuery = "UPDATE Books SET price = ? WHERE book_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
                pstmt.setDouble(1, 19.99);
                pstmt.setInt(2, testBookId);
                
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("✓ UPDATE operation successful");
                } else {
                    System.out.println("✗ UPDATE operation failed");
                    return false;
                }
            }
            
            // Test DELETE
            String deleteQuery = "DELETE FROM Books WHERE book_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
                pstmt.setInt(1, testBookId);
                
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("✓ DELETE operation successful");
                } else {
                    System.out.println("✗ DELETE operation failed");
                    return false;
                }
            }
            
            System.out.println("✓ All basic CRUD operations working correctly");
            return true;
            
        } catch (SQLException e) {
            System.out.println("✗ Basic operations test failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Run comprehensive database test
     */
    public static boolean runFullDatabaseTest() {
        System.out.println("Starting comprehensive database test...\n");
        
        boolean connectionOk = testDatabaseConnection();
        boolean schemaOk = verifyDatabaseSchema();
        boolean operationsOk = testBasicOperations();
        
        checkSampleData();
        
        System.out.println("\n=== Test Summary ===");
        System.out.println("Database Connection: " + (connectionOk ? "✓ PASS" : "✗ FAIL"));
        System.out.println("Schema Verification: " + (schemaOk ? "✓ PASS" : "✗ FAIL"));
        System.out.println("Basic Operations: " + (operationsOk ? "✓ PASS" : "✗ FAIL"));
        
        boolean allTestsPassed = connectionOk && schemaOk && operationsOk;
        
        if (allTestsPassed) {
            System.out.println("\n🎉 All database tests PASSED! System is ready to use.");
        } else {
            System.out.println("\n❌ Some database tests FAILED! Please check the issues above.");
            System.out.println("\nTroubleshooting steps:");
            System.out.println("1. Ensure XAMPP MySQL service is running");
            System.out.println("2. Create database 'online_bookstore_db' if it doesn't exist");
            System.out.println("3. Run database/schema.sql to create tables");
            System.out.println("4. Check connection settings in DBConnection.java");
        }
        
        return allTestsPassed;
    }

    /**
     * Display database information
     */
    public static void displayDatabaseInfo() {
        System.out.println("\n=== Database Information ===");
        
        try (Connection conn = DBConnection.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            
            System.out.println("Database Product: " + metaData.getDatabaseProductName());
            System.out.println("Database Version: " + metaData.getDatabaseProductVersion());
            System.out.println("Driver Name: " + metaData.getDriverName());
            System.out.println("Driver Version: " + metaData.getDriverVersion());
            System.out.println("URL: " + metaData.getURL());
            System.out.println("Username: " + metaData.getUserName());
            
        } catch (SQLException e) {
            System.out.println("Could not retrieve database information: " + e.getMessage());
        }
    }
}