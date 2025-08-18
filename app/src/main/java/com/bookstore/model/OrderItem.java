package com.bookstore.model; 

public class OrderItem {
    private int orderItemId;
    private int orderId;
    private int bookId;
    private int quantity;
    private double unitPrice;
    // Note: Removed redundant 'book' field as bookId is sufficient for lookups
    // Book details should be retrieved via BookDAO.getBookById(bookId) when needed

    // Default constructor
    public OrderItem() {
    }

    // Constructor for creating new order items (without IDs - for AUTO_INCREMENT)
    public OrderItem(int bookId, int quantity, double unitPrice) {
        this.orderItemId = 0; // Will be set by database AUTO_INCREMENT
        this.orderId = 0; // Will be set when order is created
        this.bookId = bookId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    // Constructor for existing order items (with IDs - from database)
    public OrderItem(int orderItemId, int orderId, int bookId, int quantity, double unitPrice) {
        this.orderItemId = orderItemId;
        this.orderId = orderId;
        this.bookId = bookId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    // Getters and Setters
    public int getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(int orderItemId) {
        this.orderItemId = orderItemId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    // Book getter/setter removed - use BookDAO.getBookById(bookId) to retrieve book details

    @Override
    public String toString() {
        return "OrderItem {" +
                "orderItemId=" + orderItemId +
                ", orderId=" + orderId +
                ", bookId=" + bookId +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                '}';
    }

    // Utility methods
    public double getTotalPrice() {
        return quantity * unitPrice;
    }
}