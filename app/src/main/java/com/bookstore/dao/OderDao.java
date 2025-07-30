package com.bookstore.dao;

import com.bookstore.model.Order; 
import com.bookstore.model.OrderItem; 
import com.bookstore.model.Book; 
import com.bookstore.util.DBConnection; 

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OderDao {
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
                pstmtOrder.(3, order.getStatus());
                pstmtOrder.executeUpdate();

                ResultSet rs = pstmtOrder.getGeneratedKeys();
                if (rs.next()) {
                    orderId = rs.getInt(1);
                    order.setOrderId(orderId);
                } else {
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            }
        }
    }
}
