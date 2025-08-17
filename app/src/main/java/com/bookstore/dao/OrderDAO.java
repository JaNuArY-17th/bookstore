package com.bookstore.dao;

import com.bookstore.model.Order;
import com.bookstore.model.OrderItem;
import com.bookstore.model.Book;
import com.bookstore.model.OrderStatus;
import com.bookstore.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    public int addOrder(Order order) {
        String sqlOrder = "INSERT INTO Orders (customer_id, total_amount, status) VALUES (?, ?, ?)";
        String sqlOrderItem = "INSERT INTO OrderItems (order_id, book_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
        int orderId = -1;

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            // Insert Order
            try (PreparedStatement pstmtOrder = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS)) {
                pstmtOrder.setInt(1, order.getCustomerId());
                pstmtOrder.setDouble(2, order.getTotalAmount());
                pstmtOrder.setString(3, order.getStatus().name());
                pstmtOrder.executeUpdate();

                ResultSet rs = pstmtOrder.getGeneratedKeys();
                if (rs.next()) {
                    orderId = rs.getInt(1);
                    order.setOrderId(orderId);
                } else {
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            }

            // Insert Order Items
            try (PreparedStatement pstmtOrderItem = conn.prepareStatement(sqlOrderItem)) {
                for (OrderItem item : order.getOrderItems()) {
                    pstmtOrderItem.setInt(1, orderId);
                    pstmtOrderItem.setInt(2, item.getBookId());
                    pstmtOrderItem.setInt(3, item.getQuantity());
                    pstmtOrderItem.setDouble(4, item.getUnitPrice());
                    pstmtOrderItem.addBatch();
                }
                pstmtOrderItem.executeBatch();
            }
            conn.commit();
        } catch (SQLException e) {
            System.err.println("Error adding order: " + e.getMessage());
            // Rollback if error
            try (Connection conn = DBConnection.getConnection()) {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Error during rollback: " + rollbackEx.getMessage());
            }
        }
        return orderId;
    }

    // Get order by id
    public Order getOrderById(int orderId) {
        String sqlOrder = "SELECT * FROM Orders WHERE order_id = ?";
        String sqlOrderItems = "SELECT oi.*, b.title, b.author, b.isbn, b.price " +
                "FROM OrderItems oi JOIN Books b ON oi.book_id = b.book_id WHERE oi.order_id = ?";
        Order order = null;

        try (Connection conn = DBConnection.getConnection()) {
            // Get Order details
            try (PreparedStatement pstmt = conn.prepareStatement(sqlOrder)) {
                pstmt.setInt(1, orderId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    order = new Order();
                    order.setOrderId(rs.getInt("order_id"));
                    order.setCustomerId(rs.getInt("customer_id"));
                    order.setOrderDate(rs.getDate("order_date"));
                    order.setTotalAmount(rs.getDouble("total_amount"));
                    order.setStatus(OrderStatus.valueOf(rs.getString("status")));
                }
            }

            // Get OrderItems
            if (order != null) {
                List<OrderItem> orderItems = new ArrayList<>();
                try (PreparedStatement pstmtItems = conn.prepareStatement(sqlOrderItems)) {
                    pstmtItems.setInt(1, orderId);
                    ResultSet rsItems = pstmtItems.executeQuery();
                    while (rsItems.next()) {
                        OrderItem item = new OrderItem();
                        item.setOrderItemId(rsItems.getInt("order_item_id"));
                        item.setOrderId(rsItems.getInt("order_id"));
                        item.setBookId(rsItems.getInt("book_id"));
                        item.setQuantity(rsItems.getInt("quantity"));
                        item.setUnitPrice(rsItems.getDouble("unit_price"));

                        // Assign book information to OrderItem
                        Book book = new Book();
                        book.setBookId(rsItems.getInt("book_id"));
                        book.setTitle(rsItems.getString("title"));
                        book.setAuthor(rsItems.getString("author"));
                        book.setIsbn(rsItems.getString("isbn"));
                        book.setPrice(rsItems.getDouble("price"));
                        item.setBook(book); // Link OrderItem with Book object

                        orderItems.add(item);
                    }
                }
                order.setOrderItems(orderItems);
            }

        } catch (SQLException e) {
            System.err.println("Error getting order by ID: " + e.getMessage());
        }
        return order;
    }

    // Update order status
    public boolean updateOrderStatus(int orderId, String newStatus) {
        String sql = "UPDATE Orders SET status = ? WHERE order_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, orderId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating order status: " + e.getMessage());
            return false;
        }
    }

    // Get all orders
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT order_id FROM Orders ORDER BY order_id"; // Only get ID to load full details later
        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                // To optimize, only get ID and then call getOrderById if needed
                // Or join table directly in this query if needed full data
                orders.add(getOrderById(rs.getInt("order_id"))); // Get full details for each Order
            }
        } catch (SQLException e) {
            System.err.println("Error getting all orders: " + e.getMessage());
        }
        return orders;
    }

    /**
     * Get orders by customer ID
     * @param customerId The customer ID
     * @return List of orders for the customer
     */
    public List<Order> getOrdersByCustomerId(int customerId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT order_id FROM Orders WHERE customer_id = ? ORDER BY order_date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(getOrderById(rs.getInt("order_id")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting orders by customer ID: " + e.getMessage());
        }
        return orders;
    }

    /**
     * Get order items by order ID
     * @param orderId The order ID
     * @return List of order items for the order
     */
    public List<OrderItem> getOrderItemsByOrderId(int orderId) {
        List<OrderItem> orderItems = new ArrayList<>();
        String sql = "SELECT oi.*, b.title, b.author, b.isbn, b.price " +
                    "FROM OrderItems oi JOIN Books b ON oi.book_id = b.book_id WHERE oi.order_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, orderId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setOrderItemId(rs.getInt("order_item_id"));
                    item.setOrderId(rs.getInt("order_id"));
                    item.setBookId(rs.getInt("book_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setUnitPrice(rs.getDouble("unit_price"));

                    // Assign book information to OrderItem
                    Book book = new Book();
                    book.setBookId(rs.getInt("book_id"));
                    book.setTitle(rs.getString("title"));
                    book.setAuthor(rs.getString("author"));
                    book.setIsbn(rs.getString("isbn"));
                    book.setPrice(rs.getDouble("price"));
                    item.setBook(book);

                    orderItems.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting order items by order ID: " + e.getMessage());
        }
        return orderItems;
    }

    /**
     * Get orders by user ID (for registered customers)
     * @param userId The user ID from Users table
     * @return List of orders for the user
     */
    public List<Order> getOrdersByUserId(int userId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.order_id FROM Orders o " +
                    "JOIN Customers c ON o.customer_id = c.customer_id " +
                    "WHERE c.user_id = ? ORDER BY o.order_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Order order = getOrderById(rs.getInt("order_id"));
                    if (order != null) {
                        orders.add(order);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting orders by user ID: " + e.getMessage());
        }
        return orders;
    }
}
