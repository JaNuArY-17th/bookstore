package com.bookstore.model;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

public class Order {
    private int orderId;
    private int customerId;
    private int userId;
    private Date orderDate;
    private double totalAmount;
    private OrderStatus status;
    private String trackingNumber;
    private Timestamp shippedDate;
    private Timestamp deliveredDate;
    private Date estimatedDeliveryDate;
    private String notes;
    private List<OrderItem> orderItems;
    // Default constructor
    public Order() {
    }

    // Constructor for creating new orders (without ID - for AUTO_INCREMENT)
    public Order(int customerId, Date orderDate, double totalAmount, OrderStatus status, List<OrderItem> orderItems) {
        this.orderId = 0; // Will be set by database AUTO_INCREMENT
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.orderItems = orderItems;
    }

    // Constructor for creating new orders with user ID (without order ID - for AUTO_INCREMENT)
    public Order(int customerId, int userId, Date orderDate, double totalAmount, OrderStatus status,
                String trackingNumber, List<OrderItem> orderItems) {
        this.orderId = 0; // Will be set by database AUTO_INCREMENT
        this.customerId = customerId;
        this.userId = userId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.trackingNumber = trackingNumber;
        this.orderItems = orderItems;
    }

    // Constructor for existing orders (with ID - from database)
    public Order(int orderId, int customerId, Date orderDate, double totalAmount, OrderStatus status, List<OrderItem> orderItems) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.orderItems = orderItems;
    }

    // Constructor for existing orders with user ID (with ID - from database)
    public Order(int orderId, int customerId, int userId, Date orderDate, double totalAmount, OrderStatus status,
                String trackingNumber, List<OrderItem> orderItems) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.userId = userId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.trackingNumber = trackingNumber;
        this.orderItems = orderItems;
    }

    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderStatus getStatus() { return status; }

    public void setStatus(OrderStatus status) { this.status = status; }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public Timestamp getShippedDate() {
        return shippedDate;
    }

    public void setShippedDate(Timestamp shippedDate) {
        this.shippedDate = shippedDate;
    }

    public Timestamp getDeliveredDate() {
        return deliveredDate;
    }

    public void setDeliveredDate(Timestamp deliveredDate) {
        this.deliveredDate = deliveredDate;
    }

    public Date getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }

    public void setEstimatedDeliveryDate(Date estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Utility methods
    public boolean isPending() {
        return status == OrderStatus.PENDING;
    }

    public boolean isProcessing() {
        return status == OrderStatus.PROCESSING;
    }

    public boolean isShipped() {
        return status == OrderStatus.SHIPPED;
    }

    public boolean isDelivered() {
        return status == OrderStatus.DELIVERED;
    }

    public boolean isCancelled() {
        return status == OrderStatus.CANCELLED;
    }

    public int getTotalItems() {
        return orderItems != null ? orderItems.stream().mapToInt(OrderItem::getQuantity).sum() : 0;
    }

    @Override
    public String toString() {
        return "Order {" +
                "orderId=" + orderId +
                ", customerId=" + customerId +
                ", userId=" + userId +
                ", orderDate=" + orderDate +
                ", totalAmount=" + totalAmount +
                ", status=" + status +
                ", trackingNumber='" + trackingNumber + '\'' +
                ", itemCount=" + (orderItems != null ? orderItems.size() : 0) +
                '}';
    }
}
