-- Sample Data for Bookstore Database
-- This script inserts sample data for testing and development

-- Insert sample users (passwords are BCrypt hashed for 'password123')
INSERT INTO Users (username, email, password_hash, role, first_name, last_name, phone, address, is_active) VALUES
('admin', 'admin@bookstore.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGfMxJA4b3jPEnL8QaVOKUoxe', 'ADMIN', 'Admin', 'User', '+1-555-0001', '123 Admin Street, Admin City, AC 12345', TRUE),
('john_doe', 'john.doe@email.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGfMxJA4b3jPEnL8QaVOKUoxe', 'USER', 'John', 'Doe', '+1-555-0101', '456 Oak Avenue, Springfield, IL 62701', TRUE),
('jane_smith', 'jane.smith@email.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGfMxJA4b3jPEnL8QaVOKUoxe', 'USER', 'Jane', 'Smith', '+1-555-0102', '789 Pine Street, Chicago, IL 60601', TRUE),
('bob_wilson', 'bob.wilson@email.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGfMxJA4b3jPEnL8QaVOKUoxe', 'USER', 'Bob', 'Wilson', '+1-555-0103', '321 Elm Drive, Austin, TX 73301', TRUE),
('alice_brown', 'alice.brown@email.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGfMxJA4b3jPEnL8QaVOKUoxe', 'USER', 'Alice', 'Brown', '+1-555-0104', '654 Maple Lane, Seattle, WA 98101', TRUE),
('manager', 'manager@bookstore.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGfMxJA4b3jPEnL8QaVOKUoxe', 'ADMIN', 'Store', 'Manager', '+1-555-0002', '789 Manager Boulevard, Business City, BC 54321', TRUE);

-- Insert sample books with comprehensive information
INSERT INTO Books (title, author, isbn, price, stock_quantity, description, category, publisher, publication_date, pages, language, image_url, is_active) VALUES
('The Great Gatsby', 'F. Scott Fitzgerald', '978-0-7432-7356-5', 12.99, 50, 'A classic American novel set in the Jazz Age, exploring themes of wealth, love, and the American Dream.', 'Fiction', 'Scribner', '1925-04-10', 180, 'English', 'https://example.com/images/great-gatsby.jpg', TRUE),
('To Kill a Mockingbird', 'Harper Lee', '978-0-06-112008-4', 14.99, 35, 'A gripping tale of racial injustice and childhood innocence in the American South.', 'Fiction', 'J.B. Lippincott & Co.', '1960-07-11', 376, 'English', 'https://example.com/images/mockingbird.jpg', TRUE),
('1984', 'George Orwell', '978-0-452-28423-4', 13.99, 40, 'A dystopian social science fiction novel about totalitarian control and surveillance.', 'Science Fiction', 'Secker & Warburg', '1949-06-08', 328, 'English', 'https://example.com/images/1984.jpg', TRUE),
('Pride and Prejudice', 'Jane Austen', '978-0-14-143951-8', 11.99, 30, 'A romantic novel that critiques the British landed gentry at the end of the 18th century.', 'Romance', 'T. Egerton', '1813-01-28', 432, 'English', 'https://example.com/images/pride-prejudice.jpg', TRUE),
('The Catcher in the Rye', 'J.D. Salinger', '978-0-316-76948-0', 13.50, 25, 'A controversial novel about teenage rebellion and alienation in post-war America.', 'Fiction', 'Little, Brown and Company', '1951-07-16', 277, 'English', 'https://example.com/images/catcher-rye.jpg', TRUE),
('Harry Potter and the Sorcerer\'s Stone', 'J.K. Rowling', '978-0-439-70818-8', 15.99, 60, 'The first book in the magical Harry Potter series about a young wizard\'s adventures.', 'Fantasy', 'Bloomsbury', '1997-06-26', 309, 'English', 'https://example.com/images/harry-potter-1.jpg', TRUE),
('The Lord of the Rings', 'J.R.R. Tolkien', '978-0-544-00341-5', 25.99, 20, 'An epic high fantasy novel about the quest to destroy the One Ring.', 'Fantasy', 'George Allen & Unwin', '1954-07-29', 1216, 'English', 'https://example.com/images/lotr.jpg', TRUE),
('Dune', 'Frank Herbert', '978-0-441-17271-9', 16.99, 15, 'A science fiction novel set in the distant future amidst a feudal interstellar society.', 'Science Fiction', 'Chilton Books', '1965-08-01', 688, 'English', 'https://example.com/images/dune.jpg', TRUE),
('The Hitchhiker\'s Guide to the Galaxy', 'Douglas Adams', '978-0-345-39180-3', 12.50, 45, 'A comedic science fiction series about space travel and the meaning of life.', 'Science Fiction', 'Pan Books', '1979-10-12', 224, 'English', 'https://example.com/images/hitchhiker.jpg', TRUE),
('Jane Eyre', 'Charlotte Brontë', '978-0-14-144114-6', 12.99, 28, 'A bildungsroman following the experiences of its eponymous heroine.', 'Romance', 'Smith, Elder & Co.', '1847-10-16', 507, 'English', 'https://example.com/images/jane-eyre.jpg', TRUE),
('The Hobbit', 'J.R.R. Tolkien', '978-0-547-92822-7', 14.99, 35, 'A fantasy novel about the adventures of hobbit Bilbo Baggins.', 'Fantasy', 'George Allen & Unwin', '1937-09-21', 310, 'English', 'https://example.com/images/hobbit.jpg', TRUE),
('Brave New World', 'Aldous Huxley', '978-0-06-085052-4', 13.99, 22, 'A dystopian novel exploring themes of technology, society, and human nature.', 'Science Fiction', 'Chatto & Windus', '1932-08-30', 311, 'English', 'https://example.com/images/brave-new-world.jpg', TRUE),
('The Chronicles of Narnia', 'C.S. Lewis', '978-0-06-623851-4', 19.99, 18, 'A series of seven fantasy novels set in the magical land of Narnia.', 'Fantasy', 'Geoffrey Bles', '1950-10-16', 767, 'English', 'https://example.com/images/narnia.jpg', TRUE),
('Fahrenheit 451', 'Ray Bradbury', '978-1-4516-7331-9', 13.99, 32, 'A dystopian novel about a future society where books are banned and burned.', 'Science Fiction', 'Ballantine Books', '1953-10-19', 249, 'English', 'https://example.com/images/fahrenheit-451.jpg', TRUE),
('Romeo and Juliet', 'William Shakespeare', '978-0-14-062430-5', 9.99, 40, 'A tragic play about young star-crossed lovers whose deaths unite their feuding families.', 'Drama', 'John Danter', '1597-01-01', 112, 'English', 'https://example.com/images/romeo-juliet.jpg', TRUE);

-- Insert customers linked to users
INSERT INTO Customers (user_id, name, email, address, phone, date_of_birth, preferred_payment_method) VALUES
(2, 'John Doe', 'john.doe@email.com', '456 Oak Avenue, Springfield, IL 62701', '+1-555-0101', '1985-03-15', 'Credit Card'),
(3, 'Jane Smith', 'jane.smith@email.com', '789 Pine Street, Chicago, IL 60601', '+1-555-0102', '1990-07-22', 'PayPal'),
(4, 'Bob Wilson', 'bob.wilson@email.com', '321 Elm Drive, Austin, TX 73301', '+1-555-0103', '1988-11-08', 'Credit Card'),
(5, 'Alice Brown', 'alice.brown@email.com', '654 Maple Lane, Seattle, WA 98101', '+1-555-0104', '1992-05-30', 'Debit Card');

-- Insert sample orders
INSERT INTO Orders (customer_id, user_id, total_amount, status, shipping_address, billing_address, payment_method, payment_status, estimated_delivery_date, notes) VALUES
(1, 2, 28.98, 'DELIVERED', '456 Oak Avenue, Springfield, IL 62701', '456 Oak Avenue, Springfield, IL 62701', 'Credit Card', 'PAID', '2024-01-15', 'First order - delivered successfully'),
(2, 3, 42.97, 'SHIPPED', '789 Pine Street, Chicago, IL 60601', '789 Pine Street, Chicago, IL 60601', 'PayPal', 'PAID', '2024-01-20', 'Express shipping requested'),
(3, 4, 15.99, 'PROCESSING', '321 Elm Drive, Austin, TX 73301', '321 Elm Drive, Austin, TX 73301', 'Credit Card', 'PAID', '2024-01-25', 'Standard shipping'),
(4, 5, 67.96, 'PENDING', '654 Maple Lane, Seattle, WA 98101', '654 Maple Lane, Seattle, WA 98101', 'Debit Card', 'PENDING', '2024-01-30', 'Large order - awaiting payment confirmation'),
(1, 2, 25.99, 'SHIPPED', '456 Oak Avenue, Springfield, IL 62701', '456 Oak Avenue, Springfield, IL 62701', 'Credit Card', 'PAID', '2024-01-22', 'Second order from repeat customer'),
(2, 3, 13.99, 'DELIVERED', '789 Pine Street, Chicago, IL 60601', '789 Pine Street, Chicago, IL 60601', 'PayPal', 'PAID', '2024-01-18', 'Quick delivery'),
(3, 4, 31.98, 'PROCESSING', '321 Elm Drive, Austin, TX 73301', '321 Elm Drive, Austin, TX 73301', 'Credit Card', 'PAID', '2024-01-28', 'Gift order');

-- Insert order items
INSERT INTO OrderItems (order_id, book_id, quantity, unit_price) VALUES
-- Order 1: John Doe's first order
(1, 1, 1, 12.99), -- The Great Gatsby
(1, 2, 1, 14.99), -- To Kill a Mockingbird
-- Order 2: Jane Smith's order
(2, 3, 1, 13.99), -- 1984
(2, 4, 1, 11.99), -- Pride and Prejudice
(2, 5, 1, 13.50), -- The Catcher in the Rye
-- Order 3: Bob Wilson's order
(3, 6, 1, 15.99), -- Harry Potter
-- Order 4: Alice Brown's large order
(4, 7, 1, 25.99), -- The Lord of the Rings
(4, 8, 1, 16.99), -- Dune
(4, 9, 1, 12.50), -- Hitchhiker's Guide
(4, 10, 1, 12.99), -- Jane Eyre
-- Order 5: John Doe's second order
(5, 7, 1, 25.99), -- The Lord of the Rings
-- Order 6: Jane Smith's second order
(6, 12, 1, 13.99), -- Brave New World
-- Order 7: Bob Wilson's second order
(7, 11, 1, 14.99), -- The Hobbit
(7, 8, 1, 16.99); -- Dune

-- Insert default order queues
INSERT INTO OrderQueues (queue_name, queue_type, description, max_capacity, processing_order, created_by, is_active) VALUES
('Default User Queue', 'USER', 'Default queue for individual user orders', 1000, 'FIFO', 1, TRUE),
('Admin Master Queue', 'ADMIN', 'Master queue for admin to monitor all orders', 5000, 'FIFO', 1, TRUE),
('Priority Orders Queue', 'PRIORITY', 'High priority orders requiring immediate attention', 100, 'PRIORITY', 1, TRUE),
('Processing Queue', 'PROCESSING', 'Currently being processed orders', 50, 'FIFO', 1, TRUE);

-- Insert queue orders (assign orders to appropriate queues)
INSERT INTO QueueOrders (queue_id, order_id, user_id, priority_level, position_in_queue, status, assigned_to) VALUES
-- User queue assignments
(1, 1, 2, 1, 1, 'COMPLETED', 1),
(1, 2, 3, 1, 2, 'COMPLETED', 1),
(1, 3, 4, 1, 3, 'PROCESSING', 1),
(1, 4, 5, 2, 4, 'QUEUED', NULL),
(1, 5, 2, 1, 5, 'COMPLETED', 1),
(1, 6, 3, 1, 6, 'COMPLETED', 1),
(1, 7, 4, 1, 7, 'PROCESSING', 1),
-- Admin queue (all orders)
(2, 1, 2, 1, 1, 'COMPLETED', 1),
(2, 2, 3, 1, 2, 'COMPLETED', 1),
(2, 3, 4, 1, 3, 'PROCESSING', 1),
(2, 4, 5, 2, 4, 'QUEUED', NULL),
(2, 5, 2, 1, 5, 'COMPLETED', 1),
(2, 6, 3, 1, 6, 'COMPLETED', 1),
(2, 7, 4, 1, 7, 'PROCESSING', 1),
-- Priority queue (high-value orders)
(3, 4, 5, 8, 1, 'QUEUED', NULL), -- Large order
(3, 7, 4, 6, 2, 'PROCESSING', 1); -- Gift order

-- Update tracking numbers for existing orders (trigger should handle new ones)
UPDATE Orders SET tracking_number = CONCAT('TRK', DATE_FORMAT(order_date, '%Y%m%d'), '-', LPAD(order_id, 6, '0')) WHERE tracking_number IS NULL;

-- Insert some additional order status history (beyond what triggers create)
INSERT INTO OrderStatusHistory (order_id, old_status, new_status, changed_by, notes, ip_address) VALUES
(1, 'PENDING', 'PROCESSING', 1, 'Order picked up by fulfillment team', '192.168.1.100'),
(1, 'PROCESSING', 'SHIPPED', 1, 'Order shipped via UPS', '192.168.1.100'),
(1, 'SHIPPED', 'DELIVERED', 1, 'Package delivered successfully', '192.168.1.100'),
(2, 'PENDING', 'PROCESSING', 1, 'Express order prioritized', '192.168.1.101'),
(2, 'PROCESSING', 'SHIPPED', 1, 'Express shipping - overnight delivery', '192.168.1.101'),
(5, 'PENDING', 'PROCESSING', 1, 'Repeat customer order processed', '192.168.1.102'),
(5, 'PROCESSING', 'SHIPPED', 1, 'Standard shipping', '192.168.1.102'),
(6, 'PENDING', 'PROCESSING', 1, 'Quick processing for small order', '192.168.1.103'),
(6, 'PROCESSING', 'SHIPPED', 1, 'Same day shipping', '192.168.1.103'),
(6, 'SHIPPED', 'DELIVERED', 1, 'Delivered within 24 hours', '192.168.1.103');

-- Update some orders with shipping and delivery dates
UPDATE Orders SET 
    shipped_date = DATE_ADD(order_date, INTERVAL 1 DAY),
    delivered_date = DATE_ADD(order_date, INTERVAL 3 DAY)
WHERE status = 'DELIVERED';

UPDATE Orders SET 
    shipped_date = DATE_ADD(order_date, INTERVAL 1 DAY)
WHERE status = 'SHIPPED';

-- Update last login times for users
UPDATE Users SET last_login = DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY) WHERE user_id > 1;

-- Update some book stock quantities to reflect sales
UPDATE Books SET stock_quantity = stock_quantity - 1 WHERE book_id IN (1, 2, 3, 4, 5, 6, 8, 10, 11, 12);
UPDATE Books SET stock_quantity = stock_quantity - 2 WHERE book_id IN (7, 8); -- Popular books sold more

-- Add some books with low stock for testing inventory alerts
UPDATE Books SET stock_quantity = 2 WHERE book_id = 13; -- Chronicles of Narnia - low stock
UPDATE Books SET stock_quantity = 1 WHERE book_id = 8;  -- Dune - very low stock
UPDATE Books SET stock_quantity = 0 WHERE book_id = 15; -- Romeo and Juliet - out of stock