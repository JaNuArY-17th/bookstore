package com.bookstore.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Customer user class extending User
 * Represents customer users with limited system access
 */
public class Customer extends User {
    private int customerId;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String phoneNumber;
    private List<Integer> orderHistory;
    private String preferredPaymentMethod;
    private boolean emailNotifications;

    // Constructors
    public Customer() {
        super();
        this.role = Role.CUSTOMER;
        this.orderHistory = new ArrayList<>();
        this.emailNotifications = true;
    }

    public Customer(String username, String password, String email, String firstName, String lastName) {
        super(username, password, email, firstName, lastName, Role.CUSTOMER);
        this.orderHistory = new ArrayList<>();
        this.emailNotifications = true;
    }

    public Customer(int customerId, String name, String email, String address) {
        this();
        this.customerId = customerId;
        this.firstName = name.split(" ")[0];
        this.lastName = name.length() > this.firstName.length() ?
                      name.substring(this.firstName.length() + 1) : "";
        this.email = email;
        this.address = address;
    }

    public Customer(String username, String password, String email, String firstName, String lastName,
                   String address, String phoneNumber) {
        super(username, password, email, firstName, lastName, Role.CUSTOMER);
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.orderHistory = new ArrayList<>();
        this.emailNotifications = true;
    }

    // Customer-specific getters and setters
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return getFullName();
    }

    public void setName(String name) {
        String[] parts = name.split(" ", 2);
        this.firstName = parts[0];
        this.lastName = parts.length > 1 ? parts[1] : "";
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Integer> getOrderHistory() {
        return orderHistory;
    }

    public void setOrderHistory(List<Integer> orderHistory) {
        this.orderHistory = orderHistory;
    }

    public String getPreferredPaymentMethod() {
        return preferredPaymentMethod;
    }

    public void setPreferredPaymentMethod(String preferredPaymentMethod) {
        this.preferredPaymentMethod = preferredPaymentMethod;
    }

    public boolean isEmailNotifications() {
        return emailNotifications;
    }

    public void setEmailNotifications(boolean emailNotifications) {
        this.emailNotifications = emailNotifications;
    }

    // Abstract method implementations
    @Override
    public String getUserType() {
        return "Customer";
    }

    @Override
    public boolean hasPermission(String permission) {
        // Customers have limited permissions
        switch (permission.toUpperCase()) {
            case "VIEW_BOOKS":
            case "SEARCH_BOOKS":
            case "PLACE_ORDER":
            case "VIEW_OWN_ORDERS":
            case "UPDATE_PROFILE":
                return true;
            default:
                return false;
        }
    }

    // Customer-specific methods
    public boolean canPlaceOrder() {
        return isActive() && hasPermission("PLACE_ORDER");
    }

    public boolean canViewBooks() {
        return hasPermission("VIEW_BOOKS");
    }

    public void addOrderToHistory(int orderId) {
        if (!orderHistory.contains(orderId)) {
            orderHistory.add(orderId);
        }
    }

    public boolean hasOrderHistory() {
        return !orderHistory.isEmpty();
    }

    public int getTotalOrders() {
        return orderHistory.size();
    }

    @Override
    public String toString() {
        return "Customer{" +
                "userId=" + userId +
                ", customerId=" + customerId +
                ", username='" + username + '\'' +
                ", name='" + getFullName() + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
