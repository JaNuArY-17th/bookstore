-- Bookstore Database Schema
-- This script creates all necessary tables for the enhanced bookstore system

-- Drop existing tables if they exist (for clean setup)
DROP TABLE IF EXISTS QueueOrders;
DROP TABLE IF EXISTS OrderQueues;
DROP TABLE IF EXISTS OrderStatusHistory;
DROP TABLE IF EXISTS OrderItems;
DROP TABLE IF EXISTS Orders;
DROP TABLE IF EXISTS Customers;
DROP TABLE IF EXISTS Books;
DROP TABLE IF EXISTS Users;

-- Create Users table for authentication and authorization
CREATE TABLE Users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('USER', 'ADMIN') DEFAULT 'USER',
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    last_login TIMESTAMP NULL,
    
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role (role),
    INDEX idx_active (is_active)
);

-- Create Books table (enhanced with additional fields)
CREATE TABLE Books (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isbn VARCHAR(20) UNIQUE NOT NULL,
    price DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
    stock_quantity INT NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0),
    description TEXT,
    category VARCHAR(100),
    publisher VARCHAR(255),
    publication_date DATE,
    pages INT,
    language VARCHAR(50) DEFAULT 'English',
    image_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    
    INDEX idx_title (title),
    INDEX idx_author (author),
    INDEX idx_isbn (isbn),
    INDEX idx_category (category),
    INDEX idx_price (price),
    INDEX idx_stock (stock_quantity),
    INDEX idx_active (is_active)
);

-- Create Customers table (linked to Users)
CREATE TABLE Customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNIQUE,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    address TEXT,
    phone VARCHAR(20),
    date_of_birth DATE,
    preferred_payment_method VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE SET NULL,
    INDEX idx_email (email),
    INDEX idx_user_id (user_id)
);

-- Create Orders table (enhanced with tracking)
CREATE TABLE Orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    user_id INT,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10, 2) NOT NULL CHECK (total_amount >= 0),
    status ENUM('PENDING', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED') DEFAULT 'PENDING',
    tracking_number VARCHAR(100) UNIQUE,
    shipping_address TEXT,
    billing_address TEXT,
    payment_method VARCHAR(50),
    payment_status ENUM('PENDING', 'PAID', 'FAILED', 'REFUNDED') DEFAULT 'PENDING',
    shipped_date TIMESTAMP NULL,
    delivered_date TIMESTAMP NULL,
    estimated_delivery_date DATE,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (customer_id) REFERENCES Customers(customer_id) ON DELETE RESTRICT,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE SET NULL,
    INDEX idx_customer_id (customer_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_order_date (order_date),
    INDEX idx_tracking_number (tracking_number),
    INDEX idx_payment_status (payment_status)
);

-- Create OrderItems table
CREATE TABLE OrderItems (
    order_item_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    book_id INT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10, 2) NOT NULL CHECK (unit_price >= 0),
    total_price DECIMAL(10, 2) GENERATED ALWAYS AS (quantity * unit_price) STORED,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (order_id) REFERENCES Orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES Books(book_id) ON DELETE RESTRICT,
    INDEX idx_order_id (order_id),
    INDEX idx_book_id (book_id),
    UNIQUE KEY unique_order_book (order_id, book_id)
);

-- Create OrderStatusHistory table for tracking order changes
CREATE TABLE OrderStatusHistory (
    history_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    old_status VARCHAR(20),
    new_status VARCHAR(20) NOT NULL,
    changed_by INT,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    ip_address VARCHAR(45),
    user_agent TEXT,
    
    FOREIGN KEY (order_id) REFERENCES Orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (changed_by) REFERENCES Users(user_id) ON DELETE SET NULL,
    INDEX idx_order_id (order_id),
    INDEX idx_changed_by (changed_by),
    INDEX idx_changed_at (changed_at),
    INDEX idx_new_status (new_status)
);

-- Create OrderQueues table for queue management
CREATE TABLE OrderQueues (
    queue_id INT AUTO_INCREMENT PRIMARY KEY,
    queue_name VARCHAR(100) NOT NULL,
    queue_type ENUM('USER', 'ADMIN', 'PRIORITY', 'PROCESSING') DEFAULT 'USER',
    description TEXT,
    max_capacity INT DEFAULT 1000,
    processing_order ENUM('FIFO', 'LIFO', 'PRIORITY') DEFAULT 'FIFO',
    created_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    
    FOREIGN KEY (created_by) REFERENCES Users(user_id) ON DELETE SET NULL,
    INDEX idx_queue_type (queue_type),
    INDEX idx_queue_name (queue_name),
    INDEX idx_active (is_active)
);

-- Create QueueOrders table for order-queue relationships
CREATE TABLE QueueOrders (
    queue_order_id INT AUTO_INCREMENT PRIMARY KEY,
    queue_id INT NOT NULL,
    order_id INT NOT NULL,
    user_id INT,
    priority_level INT DEFAULT 1 CHECK (priority_level BETWEEN 1 AND 10),
    position_in_queue INT,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP NULL,
    processing_started_at TIMESTAMP NULL,
    processing_completed_at TIMESTAMP NULL,
    assigned_to INT,
    status ENUM('QUEUED', 'PROCESSING', 'COMPLETED', 'FAILED', 'CANCELLED') DEFAULT 'QUEUED',
    retry_count INT DEFAULT 0,
    error_message TEXT,
    
    FOREIGN KEY (queue_id) REFERENCES OrderQueues(queue_id) ON DELETE CASCADE,
    FOREIGN KEY (order_id) REFERENCES Orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (assigned_to) REFERENCES Users(user_id) ON DELETE SET NULL,
    INDEX idx_queue_id (queue_id),
    INDEX idx_order_id (order_id),
    INDEX idx_user_id (user_id),
    INDEX idx_priority (priority_level),
    INDEX idx_status (status),
    INDEX idx_added_at (added_at),
    UNIQUE KEY unique_queue_order (queue_id, order_id)
);

-- Create triggers for automatic tracking number generation
DELIMITER //

CREATE TRIGGER generate_tracking_number 
BEFORE INSERT ON Orders 
FOR EACH ROW 
BEGIN 
    IF NEW.tracking_number IS NULL THEN
        SET NEW.tracking_number = CONCAT('TRK', YEAR(NOW()), MONTH(NOW()), DAY(NOW()), '-', LPAD(NEW.order_id, 6, '0'));
    END IF;
END//

-- Create trigger for order status history
CREATE TRIGGER order_status_history_insert
AFTER INSERT ON Orders
FOR EACH ROW
BEGIN
    INSERT INTO OrderStatusHistory (order_id, old_status, new_status, changed_by, notes)
    VALUES (NEW.order_id, NULL, NEW.status, NEW.user_id, 'Order created');
END//

CREATE TRIGGER order_status_history_update
AFTER UPDATE ON Orders
FOR EACH ROW
BEGIN
    IF OLD.status != NEW.status THEN
        INSERT INTO OrderStatusHistory (order_id, old_status, new_status, changed_by, notes)
        VALUES (NEW.order_id, OLD.status, NEW.status, NEW.user_id, 'Status updated');
    END IF;
END//

DELIMITER ;

-- Create views for common queries
CREATE VIEW UserOrderSummary AS
SELECT 
    u.user_id,
    u.username,
    u.email,
    COUNT(o.order_id) as total_orders,
    COALESCE(SUM(o.total_amount), 0) as total_spent,
    MAX(o.order_date) as last_order_date,
    COUNT(CASE WHEN o.status = 'PENDING' THEN 1 END) as pending_orders,
    COUNT(CASE WHEN o.status = 'PROCESSING' THEN 1 END) as processing_orders,
    COUNT(CASE WHEN o.status = 'SHIPPED' THEN 1 END) as shipped_orders,
    COUNT(CASE WHEN o.status = 'DELIVERED' THEN 1 END) as delivered_orders
FROM Users u
LEFT JOIN Orders o ON u.user_id = o.user_id
WHERE u.is_active = TRUE
GROUP BY u.user_id, u.username, u.email;

CREATE VIEW BookInventorySummary AS
SELECT 
    b.book_id,
    b.title,
    b.author,
    b.isbn,
    b.price,
    b.stock_quantity,
    COALESCE(SUM(oi.quantity), 0) as total_sold,
    COUNT(DISTINCT o.order_id) as total_orders,
    COALESCE(SUM(oi.total_price), 0) as total_revenue
FROM Books b
LEFT JOIN OrderItems oi ON b.book_id = oi.book_id
LEFT JOIN Orders o ON oi.order_id = o.order_id
WHERE b.is_active = TRUE
GROUP BY b.book_id, b.title, b.author, b.isbn, b.price, b.stock_quantity;

CREATE VIEW QueueSummary AS
SELECT 
    q.queue_id,
    q.queue_name,
    q.queue_type,
    COUNT(qo.queue_order_id) as total_orders,
    COUNT(CASE WHEN qo.status = 'QUEUED' THEN 1 END) as queued_orders,
    COUNT(CASE WHEN qo.status = 'PROCESSING' THEN 1 END) as processing_orders,
    COUNT(CASE WHEN qo.status = 'COMPLETED' THEN 1 END) as completed_orders,
    AVG(qo.priority_level) as avg_priority,
    MIN(qo.added_at) as oldest_order,
    MAX(qo.added_at) as newest_order
FROM OrderQueues q
LEFT JOIN QueueOrders qo ON q.queue_id = qo.queue_id
WHERE q.is_active = TRUE
GROUP BY q.queue_id, q.queue_name, q.queue_type;