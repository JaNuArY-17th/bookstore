-- =====================================================
-- Database Maintenance and Utility Queries
-- =====================================================

USE online_bookstore_db;

-- =====================================================
-- PERFORMANCE MONITORING QUERIES
-- =====================================================

-- Check table sizes
SELECT 
    TABLE_NAME,
    ROUND(((DATA_LENGTH + INDEX_LENGTH) / 1024 / 1024), 2) AS 'Size (MB)',
    TABLE_ROWS
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = 'online_bookstore_db'
ORDER BY (DATA_LENGTH + INDEX_LENGTH) DESC;

-- Check index usage
SELECT 
    TABLE_NAME,
    INDEX_NAME,
    CARDINALITY,
    NULLABLE
FROM information_schema.STATISTICS 
WHERE TABLE_SCHEMA = 'online_bookstore_db'
ORDER BY TABLE_NAME, INDEX_NAME;

-- =====================================================
-- DATA INTEGRITY CHECKS
-- =====================================================

-- Check for orphaned order items
SELECT 'Orphaned OrderItems' AS Check_Type, COUNT(*) AS Count
FROM OrderItems oi
LEFT JOIN Orders o ON oi.order_id = o.order_id
WHERE o.order_id IS NULL;

-- Check for orders without items
SELECT 'Orders without items' AS Check_Type, COUNT(*) AS Count
FROM Orders o
LEFT JOIN OrderItems oi ON o.order_id = oi.order_id
WHERE oi.order_id IS NULL;

-- Check for negative stock
SELECT 'Books with negative stock' AS Check_Type, COUNT(*) AS Count
FROM Books 
WHERE stock_quantity < 0;

-- Check for invalid order totals
SELECT 'Orders with incorrect totals' AS Check_Type, COUNT(*) AS Count
FROM Orders o
JOIN (
    SELECT 
        order_id,
        SUM(quantity * unit_price) AS calculated_total
    FROM OrderItems
    GROUP BY order_id
) calc ON o.order_id = calc.order_id
WHERE ABS(o.total_amount - calc.calculated_total) > 0.01;

-- =====================================================
-- BUSINESS INTELLIGENCE QUERIES
-- =====================================================

-- Top selling books
SELECT 
    b.title,
    b.author,
    SUM(oi.quantity) AS total_sold,
    SUM(oi.quantity * oi.unit_price) AS total_revenue
FROM Books b
JOIN OrderItems oi ON b.book_id = oi.book_id
JOIN Orders o ON oi.order_id = o.order_id
WHERE o.status IN ('DELIVERED', 'SHIPPED')
GROUP BY b.book_id, b.title, b.author
ORDER BY total_sold DESC
LIMIT 10;

-- Customer purchase summary
SELECT 
    c.name,
    c.email,
    COUNT(o.order_id) AS total_orders,
    SUM(o.total_amount) AS total_spent,
    AVG(o.total_amount) AS avg_order_value,
    MAX(o.order_date) AS last_order_date
FROM Customers c
JOIN Orders o ON c.customer_id = o.customer_id
WHERE o.status IN ('DELIVERED', 'SHIPPED', 'PROCESSING')
GROUP BY c.customer_id, c.name, c.email
ORDER BY total_spent DESC;

-- Monthly sales report
SELECT 
    YEAR(order_date) AS year,
    MONTH(order_date) AS month,
    COUNT(order_id) AS orders_count,
    SUM(total_amount) AS total_sales,
    AVG(total_amount) AS avg_order_value
FROM Orders
WHERE status IN ('DELIVERED', 'SHIPPED')
GROUP BY YEAR(order_date), MONTH(order_date)
ORDER BY year DESC, month DESC;

-- Inventory value report
SELECT 
    category,
    COUNT(*) AS book_count,
    SUM(stock_quantity) AS total_stock,
    SUM(stock_quantity * price) AS inventory_value,
    AVG(price) AS avg_price
FROM Books
GROUP BY category
ORDER BY inventory_value DESC;

-- =====================================================
-- MAINTENANCE PROCEDURES
-- =====================================================

-- Clean up old sessions (run periodically)
DELETE FROM UserSessions 
WHERE expires_at < DATE_SUB(NOW(), INTERVAL 7 DAY);

-- Archive old order status history (keep last 2 years)
-- CREATE TABLE IF NOT EXISTS OrderStatusHistory_Archive LIKE OrderStatusHistory;
-- INSERT INTO OrderStatusHistory_Archive 
-- SELECT * FROM OrderStatusHistory 
-- WHERE changed_at < DATE_SUB(NOW(), INTERVAL 2 YEAR);
-- DELETE FROM OrderStatusHistory 
-- WHERE changed_at < DATE_SUB(NOW(), INTERVAL 2 YEAR);

-- Update book search index (if using full-text search)
-- ALTER TABLE Books DROP INDEX idx_search;
-- ALTER TABLE Books ADD FULLTEXT idx_search (title, author, description);

-- =====================================================
-- BACKUP VERIFICATION
-- =====================================================

-- Check last backup date (you would need to implement this based on your backup strategy)
SELECT 
    'Database Size' AS metric,
    ROUND(SUM(DATA_LENGTH + INDEX_LENGTH) / 1024 / 1024, 2) AS 'Value (MB)'
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = 'online_bookstore_db'

UNION ALL

SELECT 
    'Total Records' AS metric,
    SUM(TABLE_ROWS) AS 'Value (MB)'
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = 'online_bookstore_db';

-- =====================================================
-- SECURITY AUDIT
-- =====================================================

-- Check user accounts
SELECT 
    user_id,
    username,
    email,
    role,
    is_active,
    created_at,
    last_login,
    DATEDIFF(NOW(), last_login) AS days_since_last_login
FROM Users
ORDER BY last_login DESC;

-- Check for inactive users (no login in 90 days)
SELECT 
    username,
    email,
    role,
    last_login,
    DATEDIFF(NOW(), last_login) AS days_inactive
FROM Users
WHERE last_login < DATE_SUB(NOW(), INTERVAL 90 DAY)
   OR last_login IS NULL
ORDER BY days_inactive DESC;

-- =====================================================
-- OPTIMIZATION SUGGESTIONS
-- =====================================================

-- Find tables that might benefit from optimization
SELECT 
    TABLE_NAME,
    TABLE_ROWS,
    AVG_ROW_LENGTH,
    DATA_FREE,
    ROUND(DATA_FREE / 1024 / 1024, 2) AS 'Fragmentation (MB)'
FROM information_schema.TABLES
WHERE TABLE_SCHEMA = 'online_bookstore_db'
  AND DATA_FREE > 0
ORDER BY DATA_FREE DESC;

-- Check for unused indexes (this would require query log analysis)
-- You would need to enable slow query log and analyze it

-- =====================================================
-- EMERGENCY PROCEDURES
-- =====================================================

-- Reset user password (admin use only)
-- UPDATE Users SET password = 'new_hashed_password' WHERE username = 'target_user';

-- Disable user account
-- UPDATE Users SET is_active = FALSE WHERE username = 'target_user';

-- Cancel order (emergency)
-- UPDATE Orders SET status = 'CANCELLED' WHERE order_id = 'target_order_id';

-- Restore stock (if order was cancelled)
-- UPDATE Books b 
-- JOIN OrderItems oi ON b.book_id = oi.book_id 
-- SET b.stock_quantity = b.stock_quantity + oi.quantity 
-- WHERE oi.order_id = 'cancelled_order_id';

-- =====================================================
-- REPORTING VIEWS
-- =====================================================

-- Daily sales dashboard
CREATE OR REPLACE VIEW DailySalesDashboard AS
SELECT 
    DATE(order_date) AS sale_date,
    COUNT(DISTINCT order_id) AS orders_count,
    COUNT(DISTINCT customer_id) AS unique_customers,
    SUM(total_amount) AS total_sales,
    AVG(total_amount) AS avg_order_value,
    SUM(CASE WHEN status = 'DELIVERED' THEN total_amount ELSE 0 END) AS delivered_sales,
    SUM(CASE WHEN status = 'CANCELLED' THEN total_amount ELSE 0 END) AS cancelled_sales
FROM Orders
WHERE order_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
GROUP BY DATE(order_date)
ORDER BY sale_date DESC;

-- Inventory alerts
CREATE OR REPLACE VIEW InventoryAlerts AS
SELECT 
    book_id,
    title,
    author,
    category,
    stock_quantity,
    price,
    (stock_quantity * price) AS stock_value,
    CASE 
        WHEN stock_quantity = 0 THEN 'OUT_OF_STOCK'
        WHEN stock_quantity <= 5 THEN 'CRITICAL'
        WHEN stock_quantity <= 10 THEN 'LOW'
        ELSE 'OK'
    END AS alert_level
FROM Books
WHERE stock_quantity <= 10
ORDER BY stock_quantity ASC, title;

-- Customer insights
CREATE OR REPLACE VIEW CustomerInsights AS
SELECT 
    c.customer_id,
    c.name,
    c.email,
    COUNT(o.order_id) AS total_orders,
    SUM(o.total_amount) AS lifetime_value,
    AVG(o.total_amount) AS avg_order_value,
    MIN(o.order_date) AS first_order_date,
    MAX(o.order_date) AS last_order_date,
    DATEDIFF(NOW(), MAX(o.order_date)) AS days_since_last_order,
    CASE 
        WHEN DATEDIFF(NOW(), MAX(o.order_date)) <= 30 THEN 'ACTIVE'
        WHEN DATEDIFF(NOW(), MAX(o.order_date)) <= 90 THEN 'RECENT'
        WHEN DATEDIFF(NOW(), MAX(o.order_date)) <= 365 THEN 'DORMANT'
        ELSE 'INACTIVE'
    END AS customer_status
FROM Customers c
LEFT JOIN Orders o ON c.customer_id = o.customer_id
GROUP BY c.customer_id, c.name, c.email
ORDER BY lifetime_value DESC;

-- =====================================================
-- COMPLETION MESSAGE
-- =====================================================
SELECT 'Maintenance queries and views created successfully!' AS Status;