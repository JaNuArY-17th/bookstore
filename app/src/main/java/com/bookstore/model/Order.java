package com.bookstore.model;

import java.sql.Date;

public class Order {
    private int orderId;
    private int customerId;
    private Date orderDate;
    private double totalAmount;
    private Enum<OrderStatus> status;

    // Constructor
    public Order() {
    }

    public Order(int orderId, int customerId, Date orderDate, double totalAmount, Enum<OrderStatus> status) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
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

    public Enum<OrderStatus> getStatus() { return status; }

    public void setStatus(Enum<OrderStatus> status) { this.status = status; }

    @Override
    public String toString() {
        return "Order {" +
                "orderId=" + orderId +
                ", customerId=" + customerId +
                ", orderDate=" + orderDate +
                ", totalAmount=" + totalAmount +
                ", status=" + status +
                '}';
    }
}
