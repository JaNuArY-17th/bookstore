package com.bookstore.model;

/**
 * User roles for authorization
 */
public enum Role {
    ADMIN("Administrator", "Full system access"),
    CUSTOMER("Customer", "Customer access to place orders and view books");

    private final String displayName;
    private final String description;

    Role(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return displayName;
    }
}