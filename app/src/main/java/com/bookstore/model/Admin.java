package com.bookstore.model;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

/**
 * Admin user class extending User
 * Represents administrative users with full system access
 */
public class Admin extends User {
    private String department;
    private String adminLevel; // e.g., "SUPER_ADMIN", "MANAGER", "STAFF"
    private List<String> permissions;

    // Constructors
    public Admin() {
        super();
        this.role = Role.ADMIN;
        this.adminLevel = "STAFF";
        this.permissions = getDefaultAdminPermissions();
    }

    public Admin(String username, String password, String email, String firstName, String lastName) {
        super(username, password, email, firstName, lastName, Role.ADMIN);
        this.adminLevel = "STAFF";
        this.permissions = getDefaultAdminPermissions();
    }

    public Admin(String username, String password, String email, String firstName, String lastName, 
                 String department, String adminLevel) {
        super(username, password, email, firstName, lastName, Role.ADMIN);
        this.department = department;
        this.adminLevel = adminLevel;
        this.permissions = getPermissionsByLevel(adminLevel);
    }

    // Admin-specific getters and setters
    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(String adminLevel) {
        this.adminLevel = adminLevel;
        this.permissions = getPermissionsByLevel(adminLevel);
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    // Abstract method implementations
    @Override
    public String getUserType() {
        return "Administrator (" + adminLevel + ")";
    }

    @Override
    public boolean hasPermission(String permission) {
        return permissions.contains(permission) || permissions.contains("ALL");
    }

    // Admin-specific methods
    public boolean canManageUsers() {
        return hasPermission("USER_MANAGEMENT") || adminLevel.equals("SUPER_ADMIN");
    }

    public boolean canManageBooks() {
        return hasPermission("BOOK_MANAGEMENT");
    }

    public boolean canManageOrders() {
        return hasPermission("ORDER_MANAGEMENT");
    }

    public boolean canViewReports() {
        return hasPermission("REPORTS");
    }

    public boolean canManageSystem() {
        return hasPermission("SYSTEM_MANAGEMENT") || adminLevel.equals("SUPER_ADMIN");
    }

    public void addPermission(String permission) {
        if (!permissions.contains(permission)) {
            permissions.add(permission);
        }
    }

    public void removePermission(String permission) {
        permissions.remove(permission);
    }

    // Helper methods
    private List<String> getDefaultAdminPermissions() {
        return Arrays.asList(
            "BOOK_MANAGEMENT",
            "ORDER_MANAGEMENT",
            "CUSTOMER_MANAGEMENT",
            "REPORTS"
        );
    }

    private List<String> getPermissionsByLevel(String level) {
        switch (level.toUpperCase()) {
            case "SUPER_ADMIN":
                return Arrays.asList("ALL");
            case "MANAGER":
                return Arrays.asList(
                    "BOOK_MANAGEMENT",
                    "ORDER_MANAGEMENT",
                    "CUSTOMER_MANAGEMENT",
                    "USER_MANAGEMENT",
                    "REPORTS",
                    "QUEUE_MANAGEMENT"
                );
            case "STAFF":
            default:
                return getDefaultAdminPermissions();
        }
    }

    @Override
    public String toString() {
        return "Admin{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", department='" + department + '\'' +
                ", adminLevel='" + adminLevel + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
