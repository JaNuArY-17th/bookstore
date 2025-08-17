-- =====================================================
-- Additional Sample Data for Testing
-- Run this after the main schema.sql
-- =====================================================

USE online_bookstore_db;

-- =====================================================
-- Additional Books (More Variety)
-- =====================================================
INSERT IGNORE INTO Books (title, author, isbn, price, stock_quantity, description, category, publisher, publication_date) VALUES
-- Science Fiction
('Dune', 'Frank Herbert', '978-0-441-17271-9', 16.99, 45, 'Epic science fiction novel', 'Science Fiction', 'Ace Books', '1965-08-01'),
('Foundation', 'Isaac Asimov', '978-0-553-29335-0', 15.99, 30, 'Classic science fiction series', 'Science Fiction', 'Bantam Spectra', '1951-05-01'),
('Neuromancer', 'William Gibson', '978-0-441-56956-9', 14.99, 25, 'Cyberpunk masterpiece', 'Science Fiction', 'Ace Books', '1984-07-01'),

-- Mystery/Thriller
('The Girl with the Dragon Tattoo', 'Stieg Larsson', '978-0-307-45454-1', 15.99, 35, 'Swedish crime thriller', 'Mystery', 'Vintage Books', '2005-08-01'),
('Gone Girl', 'Gillian Flynn', '978-0-307-58836-4', 16.99, 40, 'Psychological thriller', 'Thriller', 'Crown Publishers', '2012-06-05'),
('The Da Vinci Code', 'Dan Brown', '978-0-307-47572-5', 17.99, 50, 'Mystery thriller', 'Mystery', 'Doubleday', '2003-03-18'),

-- Non-Fiction
('Sapiens', 'Yuval Noah Harari', '978-0-062-31609-7', 18.99, 60, 'A brief history of humankind', 'Non-Fiction', 'Harper', '2014-09-04'),
('Educated', 'Tara Westover', '978-0-399-59050-4', 16.99, 45, 'A memoir', 'Biography', 'Random House', '2018-02-20'),
('Atomic Habits', 'James Clear', '978-0-735-21129-2', 19.99, 55, 'An easy way to build good habits', 'Self-Help', 'Avery', '2018-10-16'),

-- Programming/Technology
('Python Crash Course', 'Eric Matthes', '978-1-593-27928-8', 39.99, 25, 'A hands-on introduction to programming', 'Technology', 'No Starch Press', '2019-05-03'),
('JavaScript: The Good Parts', 'Douglas Crockford', '978-0-596-51774-8', 29.99, 20, 'JavaScript programming guide', 'Technology', 'O\'Reilly Media', '2008-05-01'),
('System Design Interview', 'Alex Xu', '978-1-736-04920-1', 44.99, 15, 'An insider\'s guide', 'Technology', 'Independently Published', '2020-06-01'),

-- Business
('Think and Grow Rich', 'Napoleon Hill', '978-1-585-42433-7', 12.99, 40, 'Classic business book', 'Business', 'TarcherPerigee', '1937-01-01'),
('The Lean Startup', 'Eric Ries', '978-0-307-88789-4', 21.99, 30, 'How innovation works', 'Business', 'Crown Business', '2011-09-13'),
('Good to Great', 'Jim Collins', '978-0-066-62099-2', 22.99, 25, 'Why some companies make the leap', 'Business', 'HarperBusiness', '2001-10-16');

-- =====================================================
-- Additional Customers
-- =====================================================
INSERT IGNORE INTO Customers (name, email, phone, address, city, state, postal_code, country) VALUES
('Michael Davis', 'michael.davis@email.com', '555-0101', '789 Broadway', 'New York', 'NY', '10003', 'USA'),
('Sarah Wilson', 'sarah.wilson@email.com', '555-0102', '456 Sunset Blvd', 'Los Angeles', 'CA', '90028', 'USA'),
('David Miller', 'david.miller@email.com', '555-0103', '123 Michigan Ave', 'Chicago', 'IL', '60611', 'USA'),
('Emily Johnson', 'emily.johnson@email.com', '555-0104', '321 Main St', 'Houston', 'TX', '77002', 'USA'),
('Robert Taylor', 'robert.taylor@email.com', '555-0105', '654 Central Ave', 'Phoenix', 'AZ', '85004', 'USA'),
('Lisa Anderson', 'lisa.anderson@email.com', '555-0106', '987 First St', 'Philadelphia', 'PA', '19102', 'USA'),
('James Thomas', 'james.thomas@email.com', '555-0107', '147 Oak St', 'San Antonio', 'TX', '78205', 'USA'),
('Jennifer White', 'jennifer.white@email.com', '555-0108', '258 Pine Ave', 'San Diego', 'CA', '92101', 'USA'),
('William Harris', 'william.harris@email.com', '555-0109', '369 Elm Dr', 'Dallas', 'TX', '75201', 'USA'),
('Amanda Clark', 'amanda.clark@email.com', '555-0110', '741 Maple Ln', 'San Jose', 'CA', '95113', 'USA');

-- =====================================================
-- Sample Orders (for testing)
-- =====================================================

-- Order 1: John Smith
INSERT IGNORE INTO Orders (customer_id, total_amount, status, payment_status, payment_method, shipping_address) 
VALUES (1, 42.97, 'DELIVERED', 'PAID', 'Credit Card', '123 Main St, New York, NY 10001');

SET @order1_id = LAST_INSERT_ID();

INSERT IGNORE INTO OrderItems (order_id, book_id, quantity, unit_price) VALUES
(@order1_id, 1, 2, 12.99),  -- The Great Gatsby x2
(@order1_id, 6, 1, 16.99);  -- Java book

-- Order 2: Jane Doe
INSERT IGNORE INTO Orders (customer_id, total_amount, status, payment_status, payment_method, shipping_address) 
VALUES (2, 31.98, 'SHIPPED', 'PAID', 'PayPal', '456 Oak Ave, Los Angeles, CA 90210');

SET @order2_id = LAST_INSERT_ID();

INSERT IGNORE INTO OrderItems (order_id, book_id, quantity, unit_price) VALUES
(@order2_id, 3, 1, 13.99),  -- 1984
(@order2_id, 4, 1, 17.99);  -- Pride and Prejudice

-- Order 3: Bob Johnson
INSERT IGNORE INTO Orders (customer_id, total_amount, status, payment_status, payment_method, shipping_address) 
VALUES (3, 88.97, 'PROCESSING', 'PAID', 'Credit Card', '789 Pine Rd, Chicago, IL 60601');

SET @order3_id = LAST_INSERT_ID();

INSERT IGNORE INTO OrderItems (order_id, book_id, quantity, unit_price) VALUES
(@order3_id, 7, 1, 45.99),  -- Java Complete Reference
(@order3_id, 8, 1, 42.98);  -- Clean Code

-- =====================================================
-- Additional Users for Testing
-- =====================================================
INSERT IGNORE INTO Users (username, password, email, first_name, last_name, role) VALUES
('manager1', 'temp_password', 'manager1@bookstore.com', 'John', 'Manager', 'ADMIN'),
('customer1', 'temp_password', 'customer1@bookstore.com', 'Alice', 'Customer', 'CUSTOMER'),
('customer2', 'temp_password', 'customer2@bookstore.com', 'Bob', 'Customer', 'CUSTOMER');

-- =====================================================
-- Sample Inventory Transactions
-- =====================================================
INSERT IGNORE INTO InventoryTransactions (book_id, transaction_type, quantity_change, old_quantity, new_quantity, reference_type, notes) VALUES
(1, 'PURCHASE', 100, 0, 100, 'MANUAL', 'Initial stock purchase'),
(2, 'PURCHASE', 50, 0, 50, 'MANUAL', 'Initial stock purchase'),
(3, 'PURCHASE', 75, 0, 75, 'MANUAL', 'Initial stock purchase'),
(6, 'PURCHASE', 30, 0, 30, 'MANUAL', 'Initial stock purchase - Java books'),
(7, 'PURCHASE', 25, 0, 25, 'MANUAL', 'Initial stock purchase - Programming books');

-- =====================================================
-- Update stock quantities to reflect transactions
-- =====================================================
UPDATE Books SET stock_quantity = 48 WHERE book_id = 1;  -- After selling 2
UPDATE Books SET stock_quantity = 49 WHERE book_id = 3;  -- After selling 1
UPDATE Books SET stock_quantity = 23 WHERE book_id = 4;  -- After selling 1
UPDATE Books SET stock_quantity = 19 WHERE book_id = 6;  -- After selling 1
UPDATE Books SET stock_quantity = 24 WHERE book_id = 7;  -- After selling 1
UPDATE Books SET stock_quantity = 14 WHERE book_id = 8;  -- After selling 1

-- =====================================================
-- Test Data Summary
-- =====================================================
SELECT 'Sample data inserted successfully!' AS Status;

SELECT 'Books in inventory:' AS Info, COUNT(*) AS Count FROM Books;
SELECT 'Customers registered:' AS Info, COUNT(*) AS Count FROM Customers;
SELECT 'Orders placed:' AS Info, COUNT(*) AS Count FROM Orders;
SELECT 'Users in system:' AS Info, COUNT(*) AS Count FROM Users;

-- Show some sample data
SELECT 'Sample Books:' AS Category;
SELECT title, author, price, stock_quantity FROM Books LIMIT 5;

SELECT 'Sample Orders:' AS Category;
SELECT o.order_id, c.name AS customer, o.total_amount, o.status 
FROM Orders o 
JOIN Customers c ON o.customer_id = c.customer_id;

SELECT 'Low Stock Alert:' AS Category;
SELECT title, stock_quantity FROM Books WHERE stock_quantity < 20;