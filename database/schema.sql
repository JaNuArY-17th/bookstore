-- =====================================================
-- Bookstore Management System Database Schema
-- MySQL Database for XAMPP
-- =====================================================

-- Create database
CREATE DATABASE IF NOT EXISTS online_bookstore_db;
USE online_bookstore_db;

-- Set charset and collation
ALTER DATABASE online_bookstore_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- =====================================================
-- 1. Users Table (Base User Information - Abstract Superclass)
-- =====================================================
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

    -- Indexes for performance
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role (role),
    INDEX idx_active (is_active),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB COMMENT='Base user accounts for authentication and authorization (Abstract Superclass)';

-- =====================================================
-- 2. Admins Table (Admin-specific Information - Subclass)
-- =====================================================
CREATE TABLE IF NOT EXISTS Admins (
    admin_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNIQUE NOT NULL,
    department VARCHAR(100) NULL COMMENT 'Admin department (IT, Sales, Management, etc.)',
    admin_level ENUM('STAFF', 'MANAGER', 'SUPER_ADMIN') NOT NULL DEFAULT 'STAFF',
    permissions JSON NULL COMMENT 'JSON array of specific permissions',
    employee_id VARCHAR(50) NULL COMMENT 'Employee identification number',
    hire_date DATE NULL,
    supervisor_id INT NULL COMMENT 'Reference to supervising admin',
    office_location VARCHAR(100) NULL,
    phone_extension VARCHAR(10) NULL,
    emergency_contact VARCHAR(255) NULL,
    notes TEXT NULL COMMENT 'Administrative notes about the admin',

    -- Foreign key constraints
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (supervisor_id) REFERENCES Admins(admin_id) ON DELETE SET NULL,

    -- Indexes for performance
    INDEX idx_user_id (user_id),
    INDEX idx_department (department),
    INDEX idx_admin_level (admin_level),
    INDEX idx_employee_id (employee_id),
    INDEX idx_supervisor_id (supervisor_id),
    INDEX idx_hire_date (hire_date)
) ENGINE=InnoDB COMMENT='Admin-specific information and permissions (Subclass of Users)';

-- =====================================================
-- 3. Customers Table (Customer-specific Information - Subclass)
-- =====================================================
CREATE TABLE IF NOT EXISTS Customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNIQUE NULL COMMENT 'Reference to Users table for registered customers',
    name VARCHAR(255) NOT NULL COMMENT 'Full name for guest customers or display name',
    email VARCHAR(100) NOT NULL COMMENT 'Email for guest customers or reference',
    phone VARCHAR(20) NULL,
    address TEXT NULL,
    city VARCHAR(100) NULL,
    state VARCHAR(100) NULL,
    postal_code VARCHAR(20) NULL,
    country VARCHAR(100) DEFAULT 'USA',
    date_of_birth DATE NULL,
    preferred_payment_method VARCHAR(50) NULL,
    email_notifications BOOLEAN DEFAULT TRUE,
    marketing_consent BOOLEAN DEFAULT FALSE,
    loyalty_points INT DEFAULT 0,
    customer_type ENUM('GUEST', 'REGISTERED', 'PREMIUM') DEFAULT 'GUEST',
    total_orders INT DEFAULT 0,
    total_spent DECIMAL(10, 2) DEFAULT 0.00,
    last_order_date TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- Foreign key constraints
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE,

    -- Indexes for performance
    INDEX idx_user_id (user_id),
    INDEX idx_email (email),
    INDEX idx_name (name),
    INDEX idx_phone (phone),
    INDEX idx_city (city),
    INDEX idx_customer_type (customer_type),
    INDEX idx_loyalty_points (loyalty_points),
    INDEX idx_last_order_date (last_order_date)
) ENGINE=InnoDB COMMENT='Customer-specific information and preferences (Subclass of Users + Guest customers)';

-- =====================================================
-- 4. Books Table (Inventory Management)
-- =====================================================
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
    
    -- Indexes for performance
    INDEX idx_isbn (isbn),
    INDEX idx_title (title),
    INDEX idx_author (author),
    INDEX idx_category (category),
    INDEX idx_price (price),
    INDEX idx_stock (stock_quantity),
    
    -- Full-text search index
    FULLTEXT idx_search (title, author, description)
) ENGINE=InnoDB COMMENT='Book inventory and catalog';

-- =====================================================
-- 5. Orders Table (Order Management)
-- =====================================================
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
    
    -- Foreign key constraints
    FOREIGN KEY (customer_id) REFERENCES Customers(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE SET NULL,
    
    -- Indexes for performance
    INDEX idx_customer_id (customer_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_payment_status (payment_status),
    INDEX idx_order_date (order_date),
    INDEX idx_tracking_number (tracking_number)
) ENGINE=InnoDB COMMENT='Customer orders and order tracking';

-- =====================================================
-- 6. OrderItems Table (Order Line Items)
-- =====================================================
CREATE TABLE IF NOT EXISTS OrderItems (
    order_item_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    book_id INT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10, 2) NOT NULL CHECK (unit_price >= 0),
    discount_amount DECIMAL(10, 2) DEFAULT 0.00,
    total_price DECIMAL(10, 2) GENERATED ALWAYS AS ((quantity * unit_price) - discount_amount) STORED,
    
    -- Foreign key constraints
    FOREIGN KEY (order_id) REFERENCES Orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES Books(book_id) ON DELETE CASCADE,
    
    -- Indexes for performance
    INDEX idx_order_id (order_id),
    INDEX idx_book_id (book_id),
    
    -- Unique constraint to prevent duplicate items in same order
    UNIQUE KEY unique_order_book (order_id, book_id)
) ENGINE=InnoDB COMMENT='Individual items within orders';

-- =====================================================
-- 7. Order Status History (Audit Trail)
-- =====================================================
CREATE TABLE IF NOT EXISTS OrderStatusHistory (
    history_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    old_status VARCHAR(50) NULL,
    new_status VARCHAR(50) NOT NULL,
    changed_by INT NULL COMMENT 'User who made the change',
    change_reason TEXT NULL,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign key constraints
    FOREIGN KEY (order_id) REFERENCES Orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (changed_by) REFERENCES Users(user_id) ON DELETE SET NULL,
    
    -- Indexes
    INDEX idx_order_id (order_id),
    INDEX idx_changed_by (changed_by),
    INDEX idx_changed_at (changed_at)
) ENGINE=InnoDB COMMENT='Audit trail for order status changes';

-- =====================================================
-- 8. Inventory Transactions (Stock Movement Tracking)
-- =====================================================
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
    
    -- Foreign key constraints
    FOREIGN KEY (book_id) REFERENCES Books(book_id) ON DELETE CASCADE,
    FOREIGN KEY (performed_by) REFERENCES Users(user_id) ON DELETE SET NULL,
    
    -- Indexes
    INDEX idx_book_id (book_id),
    INDEX idx_transaction_type (transaction_type),
    INDEX idx_transaction_date (transaction_date),
    INDEX idx_performed_by (performed_by)
) ENGINE=InnoDB COMMENT='Track all inventory movements and changes';

-- =====================================================
-- 9. User Sessions (Optional - for web sessions)
-- =====================================================
CREATE TABLE IF NOT EXISTS UserSessions (
    session_id VARCHAR(128) PRIMARY KEY,
    user_id INT NOT NULL,
    ip_address VARCHAR(45) NULL,
    user_agent TEXT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_activity TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    
    -- Foreign key constraints
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    
    -- Indexes
    INDEX idx_user_id (user_id),
    INDEX idx_expires_at (expires_at),
    INDEX idx_last_activity (last_activity)
) ENGINE=InnoDB COMMENT='User session management';

-- =====================================================
-- TRIGGERS FOR AUTOMATIC OPERATIONS
-- =====================================================

-- Trigger to update inventory when order items are inserted
DELIMITER //
CREATE TRIGGER tr_orderitem_insert_update_stock
    AFTER INSERT ON OrderItems
    FOR EACH ROW
BEGIN
    -- Decrease stock quantity
    UPDATE Books 
    SET stock_quantity = stock_quantity - NEW.quantity 
    WHERE book_id = NEW.book_id;
    
    -- Log inventory transaction
    INSERT INTO InventoryTransactions (
        book_id, transaction_type, quantity_change, 
        old_quantity, new_quantity, reference_type, 
        reference_id, performed_by, notes
    ) VALUES (
        NEW.book_id, 'SALE', -NEW.quantity,
        (SELECT stock_quantity + NEW.quantity FROM Books WHERE book_id = NEW.book_id),
        (SELECT stock_quantity FROM Books WHERE book_id = NEW.book_id),
        'ORDER', NEW.order_id, NULL, 
        CONCAT('Stock reduced for order #', NEW.order_id)
    );
END//

-- Trigger to log order status changes
CREATE TRIGGER tr_order_status_change
    AFTER UPDATE ON Orders
    FOR EACH ROW
BEGIN
    IF OLD.status != NEW.status THEN
        INSERT INTO OrderStatusHistory (
            order_id, old_status, new_status, 
            changed_by, change_reason
        ) VALUES (
            NEW.order_id, OLD.status, NEW.status,
            NEW.user_id, 'Status updated via system'
        );
    END IF;
END//

DELIMITER ;

-- =====================================================
-- VIEWS FOR COMMON QUERIES
-- =====================================================

-- View for order summary with customer details
CREATE OR REPLACE VIEW OrderSummary AS
SELECT 
    o.order_id,
    o.order_date,
    c.name AS customer_name,
    c.email AS customer_email,
    o.total_amount,
    o.status,
    o.payment_status,
    COUNT(oi.order_item_id) AS item_count,
    SUM(oi.quantity) AS total_quantity,
    u.username AS processed_by
FROM Orders o
JOIN Customers c ON o.customer_id = c.customer_id
LEFT JOIN OrderItems oi ON o.order_id = oi.order_id
LEFT JOIN Users u ON o.user_id = u.user_id
GROUP BY o.order_id, o.order_date, c.name, c.email, o.total_amount, o.status, o.payment_status, u.username;

-- View for low stock books
CREATE OR REPLACE VIEW LowStockBooks AS
SELECT 
    book_id,
    title,
    author,
    isbn,
    stock_quantity,
    price,
    (stock_quantity * price) AS stock_value
FROM Books 
WHERE stock_quantity <= 10
ORDER BY stock_quantity ASC;

-- View for sales summary
CREATE OR REPLACE VIEW SalesSummary AS
SELECT
    DATE(o.order_date) AS sale_date,
    COUNT(o.order_id) AS orders_count,
    SUM(o.total_amount) AS total_sales,
    AVG(o.total_amount) AS average_order_value,
    SUM(oi.quantity) AS books_sold
FROM Orders o
JOIN OrderItems oi ON o.order_id = oi.order_id
WHERE o.status IN ('DELIVERED', 'SHIPPED')
GROUP BY DATE(o.order_date)
ORDER BY sale_date DESC;

-- View for complete user information (Users with their specific role data)
CREATE OR REPLACE VIEW CompleteUserInfo AS
SELECT
    u.user_id,
    u.username,
    u.email,
    CONCAT(u.first_name, ' ', u.last_name) AS full_name,
    u.role,
    u.is_active,
    u.created_at,
    u.last_login,
    -- Admin specific fields
    a.admin_id,
    a.department,
    a.admin_level,
    a.employee_id,
    a.hire_date,
    a.supervisor_id,
    -- Customer specific fields
    c.customer_id,
    c.phone,
    c.address,
    c.city,
    c.state,
    c.postal_code,
    c.customer_type,
    c.loyalty_points,
    c.total_orders,
    c.total_spent,
    c.last_order_date
FROM Users u
LEFT JOIN Admins a ON u.user_id = a.user_id AND u.role = 'ADMIN'
LEFT JOIN Customers c ON u.user_id = c.user_id AND u.role = 'CUSTOMER';

-- View for admin hierarchy
CREATE OR REPLACE VIEW AdminHierarchy AS
SELECT
    a.admin_id,
    u.username,
    CONCAT(u.first_name, ' ', u.last_name) AS admin_name,
    a.department,
    a.admin_level,
    a.employee_id,
    s.admin_id AS supervisor_admin_id,
    su.username AS supervisor_username,
    CONCAT(su.first_name, ' ', su.last_name) AS supervisor_name
FROM Admins a
JOIN Users u ON a.user_id = u.user_id
LEFT JOIN Admins s ON a.supervisor_id = s.admin_id
LEFT JOIN Users su ON s.user_id = su.user_id;

-- View for customer statistics
CREATE OR REPLACE VIEW CustomerStatistics AS
SELECT
    c.customer_id,
    c.name,
    c.email,
    c.customer_type,
    c.loyalty_points,
    c.total_orders,
    c.total_spent,
    c.last_order_date,
    CASE
        WHEN c.total_spent >= 1000 THEN 'VIP'
        WHEN c.total_spent >= 500 THEN 'Premium'
        WHEN c.total_spent >= 100 THEN 'Regular'
        ELSE 'New'
    END AS customer_tier,
    DATEDIFF(CURDATE(), c.last_order_date) AS days_since_last_order
FROM Customers c
WHERE c.customer_type IN ('REGISTERED', 'PREMIUM');

-- =====================================================
-- INDEXES FOR PERFORMANCE OPTIMIZATION
-- =====================================================

-- Composite indexes for common query patterns
CREATE INDEX idx_orders_customer_date ON Orders(customer_id, order_date);
CREATE INDEX idx_orders_status_date ON Orders(status, order_date);
CREATE INDEX idx_orderitems_book_order ON OrderItems(book_id, order_id);
CREATE INDEX idx_books_category_price ON Books(category, price);
CREATE INDEX idx_users_role_active ON Users(role, is_active);

-- =====================================================
-- STORED PROCEDURES FOR COMMON OPERATIONS
-- =====================================================

DELIMITER //

-- Procedure to get order details with items
CREATE PROCEDURE GetOrderDetails(IN p_order_id INT)
BEGIN
    -- Order header
    SELECT 
        o.*,
        c.name AS customer_name,
        c.email AS customer_email,
        c.address AS customer_address,
        u.username AS processed_by_user
    FROM Orders o
    JOIN Customers c ON o.customer_id = c.customer_id
    LEFT JOIN Users u ON o.user_id = u.user_id
    WHERE o.order_id = p_order_id;
    
    -- Order items
    SELECT 
        oi.*,
        b.title,
        b.author,
        b.isbn,
        (oi.quantity * oi.unit_price) AS line_total
    FROM OrderItems oi
    JOIN Books b ON oi.book_id = b.book_id
    WHERE oi.order_id = p_order_id;
END//

-- Procedure to update book stock
CREATE PROCEDURE UpdateBookStock(
    IN p_book_id INT,
    IN p_quantity_change INT,
    IN p_transaction_type VARCHAR(20),
    IN p_user_id INT,
    IN p_notes TEXT
)
BEGIN
    DECLARE v_old_quantity INT;
    DECLARE v_new_quantity INT;
    
    -- Get current stock
    SELECT stock_quantity INTO v_old_quantity 
    FROM Books WHERE book_id = p_book_id;
    
    -- Calculate new quantity
    SET v_new_quantity = v_old_quantity + p_quantity_change;
    
    -- Validate new quantity
    IF v_new_quantity < 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Insufficient stock quantity';
    END IF;
    
    -- Update stock
    UPDATE Books 
    SET stock_quantity = v_new_quantity 
    WHERE book_id = p_book_id;
    
    -- Log transaction
    INSERT INTO InventoryTransactions (
        book_id, transaction_type, quantity_change,
        old_quantity, new_quantity, reference_type,
        performed_by, notes
    ) VALUES (
        p_book_id, p_transaction_type, p_quantity_change,
        v_old_quantity, v_new_quantity, 'MANUAL',
        p_user_id, p_notes
    );
END//

-- Procedure to create a new admin user
CREATE PROCEDURE CreateAdminUser(
    IN p_username VARCHAR(50),
    IN p_password VARCHAR(255),
    IN p_email VARCHAR(100),
    IN p_first_name VARCHAR(50),
    IN p_last_name VARCHAR(50),
    IN p_department VARCHAR(100),
    IN p_admin_level VARCHAR(20),
    IN p_employee_id VARCHAR(50)
)
BEGIN
    DECLARE v_user_id INT;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    -- Insert into Users table
    INSERT INTO Users (username, password, email, first_name, last_name, role)
    VALUES (p_username, p_password, p_email, p_first_name, p_last_name, 'ADMIN');

    SET v_user_id = LAST_INSERT_ID();

    -- Insert into Admins table
    INSERT INTO Admins (user_id, department, admin_level, employee_id, hire_date)
    VALUES (v_user_id, p_department, p_admin_level, p_employee_id, CURDATE());

    COMMIT;

    SELECT v_user_id AS user_id, 'Admin user created successfully' AS message;
END//

-- Procedure to create a new customer user
CREATE PROCEDURE CreateCustomerUser(
    IN p_username VARCHAR(50),
    IN p_password VARCHAR(255),
    IN p_email VARCHAR(100),
    IN p_first_name VARCHAR(50),
    IN p_last_name VARCHAR(50),
    IN p_address TEXT,
    IN p_city VARCHAR(100),
    IN p_state VARCHAR(100),
    IN p_postal_code VARCHAR(20)
)
BEGIN
    DECLARE v_user_id INT;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    -- Insert into Users table
    INSERT INTO Users (username, password, email, first_name, last_name, role)
    VALUES (p_username, p_password, p_email, p_first_name, p_last_name, 'CUSTOMER');

    SET v_user_id = LAST_INSERT_ID();

    -- Insert into Customers table
    INSERT INTO Customers (user_id, name, email, address, city, state, postal_code, customer_type)
    VALUES (v_user_id, CONCAT(p_first_name, ' ', p_last_name), p_email, p_address, p_city, p_state, p_postal_code, 'REGISTERED');

    COMMIT;

    SELECT v_user_id AS user_id, 'Customer user created successfully' AS message;
END//

-- Procedure to get complete user information
CREATE PROCEDURE GetCompleteUserInfo(IN p_user_id INT)
BEGIN
    SELECT * FROM CompleteUserInfo WHERE user_id = p_user_id;
END//

DELIMITER ;

-- =====================================================
-- SAMPLE DATA INSERTION
-- =====================================================

-- Insert default admin user (password is hashed: password@123)
INSERT IGNORE INTO Users (username, password, email, first_name, last_name, role)
VALUES ('admin', 'TRukV3lw+q2bWDcz23H19sT2/d7baPuO3eYor1UVa4M9f7lt5/D8nn356PaogG30', 'admin@bookstore.com', 'System', 'Administrator', 'ADMIN');

-- Insert admin details for the default admin user
INSERT IGNORE INTO Admins (user_id, department, admin_level, permissions, employee_id, hire_date, office_location)
SELECT
    u.user_id,
    'IT',
    'SUPER_ADMIN',
    '["ALL"]',
    'EMP001',
    CURDATE(),
    'Main Office'
FROM Users u
WHERE u.username = 'admin' AND u.role = 'ADMIN';

-- Insert sample customer users
INSERT IGNORE INTO Users (username, password, email, first_name, last_name, role) VALUES
('john_smith', 'TRukV3lw+q2bWDcz23H19sT2/d7baPuO3eYor1UVa4M9f7lt5/D8nn356PaogG30', 'john.smith@email.com', 'John', 'Smith', 'CUSTOMER'),
('jane_doe', 'TRukV3lw+q2bWDcz23H19sT2/d7baPuO3eYor1UVa4M9f7lt5/D8nn356PaogG30', 'jane.doe@email.com', 'Jane', 'Doe', 'CUSTOMER'),
('bob_johnson', 'TRukV3lw+q2bWDcz23H19sT2/d7baPuO3eYor1UVa4M9f7lt5/D8nn356PaogG30', 'bob.johnson@email.com', 'Bob', 'Johnson', 'CUSTOMER');

-- Insert customer details for registered customers
INSERT IGNORE INTO Customers (user_id, name, email, address, city, state, postal_code, customer_type, preferred_payment_method)
SELECT
    u.user_id,
    CONCAT(u.first_name, ' ', u.last_name),
    u.email,
    CASE
        WHEN u.username = 'john_smith' THEN '123 Main St'
        WHEN u.username = 'jane_doe' THEN '456 Oak Ave'
        WHEN u.username = 'bob_johnson' THEN '789 Pine Rd'
    END,
    CASE
        WHEN u.username = 'john_smith' THEN 'New York'
        WHEN u.username = 'jane_doe' THEN 'Los Angeles'
        WHEN u.username = 'bob_johnson' THEN 'Chicago'
    END,
    CASE
        WHEN u.username = 'john_smith' THEN 'NY'
        WHEN u.username = 'jane_doe' THEN 'CA'
        WHEN u.username = 'bob_johnson' THEN 'IL'
    END,
    CASE
        WHEN u.username = 'john_smith' THEN '10001'
        WHEN u.username = 'jane_doe' THEN '90210'
        WHEN u.username = 'bob_johnson' THEN '60601'
    END,
    'REGISTERED',
    'Credit Card'
FROM Users u
WHERE u.role = 'CUSTOMER' AND u.username IN ('john_smith', 'jane_doe', 'bob_johnson');

-- Insert guest customers (customers without user accounts)
INSERT IGNORE INTO Customers (name, email, address, city, state, postal_code, customer_type) VALUES
('Alice Brown', 'alice.brown@email.com', '321 Elm St', 'Houston', 'TX', '77001', 'GUEST'),
('Charlie Wilson', 'charlie.wilson@email.com', '654 Maple Dr', 'Phoenix', 'AZ', '85001', 'GUEST');

-- Insert sample books
INSERT IGNORE INTO Books (title, author, isbn, price, stock_quantity, description, category) VALUES
('The Great Gatsby', 'F. Scott Fitzgerald', '978-0-7432-7356-5', 12.99, 50, 'A classic American novel', 'Fiction'),
('To Kill a Mockingbird', 'Harper Lee', '978-0-06-112008-4', 14.99, 30, 'A gripping tale of racial injustice', 'Fiction'),
('1984', 'George Orwell', '978-0-452-28423-4', 13.99, 40, 'Dystopian social science fiction', 'Fiction'),
('Pride and Prejudice', 'Jane Austen', '978-0-14-143951-8', 11.99, 25, 'A romantic novel', 'Romance'),
('The Catcher in the Rye', 'J.D. Salinger', '978-0-316-76948-0', 13.50, 35, 'Coming-of-age story', 'Fiction'),
('Java: The Complete Reference', 'Herbert Schildt', '978-1-26-041195-7', 45.99, 20, 'Comprehensive Java programming guide', 'Technology'),
('Clean Code', 'Robert C. Martin', '978-0-13-235088-4', 42.99, 15, 'A handbook of agile software craftsmanship', 'Technology'),
('Design Patterns', 'Gang of Four', '978-0-20-163361-0', 54.99, 10, 'Elements of reusable object-oriented software', 'Technology');



-- =====================================================
-- SECURITY AND MAINTENANCE
-- =====================================================

-- Create database user for application (run this separately with admin privileges)
-- CREATE USER 'bookstore_app'@'localhost' IDENTIFIED BY 'secure_password_here';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON online_bookstore_db.* TO 'bookstore_app'@'localhost';
-- FLUSH PRIVILEGES;

-- =====================================================
-- BACKUP AND MAINTENANCE PROCEDURES
-- =====================================================

-- Event to clean up old sessions (run daily)
CREATE EVENT IF NOT EXISTS cleanup_expired_sessions
ON SCHEDULE EVERY 1 DAY
STARTS CURRENT_TIMESTAMP
DO
  DELETE FROM UserSessions WHERE expires_at < NOW();

-- Enable event scheduler
SET GLOBAL event_scheduler = ON;

-- =====================================================
-- COMPLETION MESSAGE
-- =====================================================
SELECT 'Database schema created successfully with inheritance structure!' AS Status,
       'Users table serves as abstract superclass' AS Architecture,
       'Admins and Customers tables extend Users with role-specific data' AS Inheritance,
       'Default admin user: admin (password: password@123)' AS AdminLogin,
       'Sample registered customers and guest customers created' AS SampleData,
       'Use CompleteUserInfo view for full user details' AS ViewInfo;