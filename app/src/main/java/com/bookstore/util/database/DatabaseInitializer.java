package com.bookstore.util.database;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

/**
 * Database initialization utility
 * Creates necessary tables for the bookstore application
 */
public class DatabaseInitializer {

    public static void initializeDatabase() {
        System.out.println("Checking database connection and schema...");
        
        // First test the connection
        if (!DatabaseTestUtil.testDatabaseConnection()) {
            System.err.println("Database connection failed. Please check XAMPP and database setup.");
            return;
        }
        
        // Check if schema already exists
        if (DatabaseTestUtil.verifyDatabaseSchema()) {
            System.out.println("✓ Database schema already exists and is complete");
            DatabaseTestUtil.checkSampleData();
            return;
        }
        
        System.out.println("Creating missing database tables...");
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Create Users table
            String createUsersTable = """
                CREATE TABLE IF NOT EXISTS Users (
                    user_id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(50) UNIQUE NOT NULL,
                    password VARCHAR(255) NOT NULL COMMENT 'Hashed password with salt',
                    email VARCHAR(100) UNIQUE NOT NULL,
                    first_name VARCHAR(50) NOT NULL,
                    last_name VARCHAR(50) NOT NULL,
                    role ENUM('ADMIN', 'CUSTOMER') NOT NULL DEFAULT 'CUSTOMER',
                    is_active BOOLEAN DEFAULT TRUE,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    last_login TIMESTAMP NULL,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    INDEX idx_username (username),
                    INDEX idx_email (email),
                    INDEX idx_role (role),
                    INDEX idx_active (is_active)
                ) ENGINE=InnoDB COMMENT='User accounts for authentication and authorization'
                """;

            // Create Books table
            String createBooksTable = """
                CREATE TABLE IF NOT EXISTS Books (
                    book_id INT AUTO_INCREMENT PRIMARY KEY,
                    title VARCHAR(255) NOT NULL,
                    author VARCHAR(255) NOT NULL,
                    isbn VARCHAR(20) UNIQUE NOT NULL,
                    price DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
                    stock_quantity INT NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0),
                    description TEXT NULL,
                    category VARCHAR(100) NULL,
                    publisher VARCHAR(255) NULL,
                    publication_date DATE NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    INDEX idx_isbn (isbn),
                    INDEX idx_title (title),
                    INDEX idx_author (author),
                    INDEX idx_category (category),
                    INDEX idx_price (price),
                    INDEX idx_stock (stock_quantity)
                ) ENGINE=InnoDB COMMENT='Book inventory and catalog'
                """;

            // Create Customers table
            String createCustomersTable = """
                CREATE TABLE IF NOT EXISTS Customers (
                    customer_id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    email VARCHAR(100) UNIQUE NOT NULL,
                    phone VARCHAR(20) NULL,
                    address TEXT NULL,
                    city VARCHAR(100) NULL,
                    state VARCHAR(100) NULL,
                    postal_code VARCHAR(20) NULL,
                    country VARCHAR(100) DEFAULT 'USA',
                    date_of_birth DATE NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    INDEX idx_email (email),
                    INDEX idx_name (name),
                    INDEX idx_phone (phone)
                ) ENGINE=InnoDB COMMENT='Customer information and contact details'
                """;

            // Create Orders table
            String createOrdersTable = """
                CREATE TABLE IF NOT EXISTS Orders (
                    order_id INT AUTO_INCREMENT PRIMARY KEY,
                    customer_id INT NOT NULL,
                    user_id INT NULL COMMENT 'User who processed the order',
                    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    total_amount DECIMAL(10, 2) NOT NULL CHECK (total_amount >= 0),
                    status ENUM('PENDING', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED') DEFAULT 'PENDING',
                    payment_status ENUM('PENDING', 'PAID', 'FAILED', 'REFUNDED') DEFAULT 'PENDING',
                    payment_method VARCHAR(50) NULL,
                    tracking_number VARCHAR(100) NULL,
                    shipped_date TIMESTAMP NULL,
                    delivered_date TIMESTAMP NULL,
                    estimated_delivery_date DATE NULL,
                    shipping_address TEXT NULL,
                    billing_address TEXT NULL,
                    notes TEXT NULL,
                    discount_amount DECIMAL(10, 2) DEFAULT 0.00,
                    tax_amount DECIMAL(10, 2) DEFAULT 0.00,
                    shipping_cost DECIMAL(10, 2) DEFAULT 0.00,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    FOREIGN KEY (customer_id) REFERENCES Customers(customer_id) ON DELETE CASCADE,
                    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE SET NULL,
                    INDEX idx_customer_id (customer_id),
                    INDEX idx_user_id (user_id),
                    INDEX idx_status (status),
                    INDEX idx_payment_status (payment_status),
                    INDEX idx_order_date (order_date)
                ) ENGINE=InnoDB COMMENT='Customer orders and order tracking'
                """;

            // Create OrderItems table
            String createOrderItemsTable = """
                CREATE TABLE IF NOT EXISTS OrderItems (
                    order_item_id INT AUTO_INCREMENT PRIMARY KEY,
                    order_id INT NOT NULL,
                    book_id INT NOT NULL,
                    quantity INT NOT NULL CHECK (quantity > 0),
                    unit_price DECIMAL(10, 2) NOT NULL CHECK (unit_price >= 0),
                    discount_amount DECIMAL(10, 2) DEFAULT 0.00,
                    total_price DECIMAL(10, 2) GENERATED ALWAYS AS ((quantity * unit_price) - discount_amount) STORED,
                    FOREIGN KEY (order_id) REFERENCES Orders(order_id) ON DELETE CASCADE,
                    FOREIGN KEY (book_id) REFERENCES Books(book_id) ON DELETE CASCADE,
                    INDEX idx_order_id (order_id),
                    INDEX idx_book_id (book_id),
                    UNIQUE KEY unique_order_book (order_id, book_id)
                ) ENGINE=InnoDB COMMENT='Individual items within orders'
                """;

            // Create additional tables for advanced features
            String createOrderStatusHistoryTable = """
                CREATE TABLE IF NOT EXISTS OrderStatusHistory (
                    history_id INT AUTO_INCREMENT PRIMARY KEY,
                    order_id INT NOT NULL,
                    old_status VARCHAR(50) NULL,
                    new_status VARCHAR(50) NOT NULL,
                    changed_by INT NULL COMMENT 'User who made the change',
                    change_reason TEXT NULL,
                    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (order_id) REFERENCES Orders(order_id) ON DELETE CASCADE,
                    FOREIGN KEY (changed_by) REFERENCES Users(user_id) ON DELETE SET NULL,
                    INDEX idx_order_id (order_id),
                    INDEX idx_changed_by (changed_by),
                    INDEX idx_changed_at (changed_at)
                ) ENGINE=InnoDB COMMENT='Audit trail for order status changes'
                """;

            String createInventoryTransactionsTable = """
                CREATE TABLE IF NOT EXISTS InventoryTransactions (
                    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
                    book_id INT NOT NULL,
                    transaction_type ENUM('PURCHASE', 'SALE', 'ADJUSTMENT', 'RETURN') NOT NULL,
                    quantity_change INT NOT NULL COMMENT 'Positive for increase, negative for decrease',
                    old_quantity INT NOT NULL,
                    new_quantity INT NOT NULL,
                    reference_type ENUM('ORDER', 'MANUAL', 'SYSTEM') NOT NULL,
                    reference_id INT NULL COMMENT 'Order ID or other reference',
                    performed_by INT NULL,
                    notes TEXT NULL,
                    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (book_id) REFERENCES Books(book_id) ON DELETE CASCADE,
                    FOREIGN KEY (performed_by) REFERENCES Users(user_id) ON DELETE SET NULL,
                    INDEX idx_book_id (book_id),
                    INDEX idx_transaction_type (transaction_type),
                    INDEX idx_transaction_date (transaction_date)
                ) ENGINE=InnoDB COMMENT='Track all inventory movements and changes'
                """;

            String createUserSessionsTable = """
                CREATE TABLE IF NOT EXISTS UserSessions (
                    session_id VARCHAR(128) PRIMARY KEY,
                    user_id INT NOT NULL,
                    ip_address VARCHAR(45) NULL,
                    user_agent TEXT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    last_activity TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    expires_at TIMESTAMP NOT NULL,
                    is_active BOOLEAN DEFAULT TRUE,
                    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
                    INDEX idx_user_id (user_id),
                    INDEX idx_expires_at (expires_at),
                    INDEX idx_last_activity (last_activity)
                ) ENGINE=InnoDB COMMENT='User session management'
                """;

            // Execute table creation statements
            System.out.println("Creating database tables...");
            
            stmt.executeUpdate(createUsersTable);
            System.out.println("✓ Users table created/verified");
            
            stmt.executeUpdate(createBooksTable);
            System.out.println("✓ Books table created/verified");
            
            stmt.executeUpdate(createCustomersTable);
            System.out.println("✓ Customers table created/verified");
            
            stmt.executeUpdate(createOrdersTable);
            System.out.println("✓ Orders table created/verified");
            
            stmt.executeUpdate(createOrderItemsTable);
            System.out.println("✓ OrderItems table created/verified");
            
            stmt.executeUpdate(createOrderStatusHistoryTable);
            System.out.println("✓ OrderStatusHistory table created/verified");
            
            stmt.executeUpdate(createInventoryTransactionsTable);
            System.out.println("✓ InventoryTransactions table created/verified");
            
            stmt.executeUpdate(createUserSessionsTable);
            System.out.println("✓ UserSessions table created/verified");
            
            System.out.println("✓ Database initialization completed successfully!");
            
            // Verify the schema was created correctly
            if (DatabaseTestUtil.verifyDatabaseSchema()) {
                System.out.println("✓ Schema verification passed");
            } else {
                System.out.println("⚠ Schema verification failed - some tables may be missing");
            }

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            System.err.println("Please check:");
            System.err.println("1. XAMPP MySQL service is running");
            System.err.println("2. Database 'online_bookstore_db' exists");
            System.err.println("3. User has proper permissions");
            e.printStackTrace();
        }
    }

    /**
     * Add user_id column to existing Orders table if it doesn't exist
     */
    public static void updateOrdersTableForUsers() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Check if user_id column exists, if not add it
            String addUserIdColumn = """
                ALTER TABLE Orders 
                ADD COLUMN IF NOT EXISTS user_id INT NULL,
                ADD FOREIGN KEY IF NOT EXISTS (user_id) REFERENCES Users(user_id) ON DELETE SET NULL
                """;

            stmt.executeUpdate(addUserIdColumn);
            System.out.println("✓ Orders table updated with user tracking");

        } catch (SQLException e) {
            // Column might already exist, which is fine
            if (!e.getMessage().contains("Duplicate column name")) {
                System.err.println("Error updating Orders table: " + e.getMessage());
            }
        }
    }
}