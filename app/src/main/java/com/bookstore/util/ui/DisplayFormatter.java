package com.bookstore.util.ui;

import com.bookstore.dao.BookDAO;
import com.bookstore.model.Book;
import com.bookstore.model.Customer;
import com.bookstore.model.Order;
import com.bookstore.model.OrderItem;
import com.bookstore.model.User;

import java.util.List;

/**
 * Utility class for formatting and displaying data in console
 * Extracted from Main.java to improve code organization
 */
public class DisplayFormatter {
    private static BookDAO bookDAO = new BookDAO();

    /**
     * Display books in a formatted table
     * @param books List of books to display
     */
    public static void displayBooksTable(List<Book> books) {
        if (books.isEmpty()) {
            System.out.println("No books found.");
            return;
        }

        System.out.printf("%-5s %-30s %-20s %-15s %-10s %-8s%n",
                "ID", "Title", "Author", "ISBN", "Price", "Stock");
        System.out.println("-".repeat(90));

        for (Book book : books) {
            System.out.printf("%-5d %-30s %-20s %-15s $%-9.2f %-8d%n",
                    book.getBookId(),
                    InputValidator.truncate(book.getTitle(), 30),
                    InputValidator.truncate(book.getAuthor(), 20),
                    book.getIsbn(),
                    book.getPrice(),
                    book.getStockQuantity());
        }
    }

    /**
     * Display customers in a formatted table
     * @param customers List of customers to display
     */
    public static void displayCustomersTable(List<Customer> customers) {
        if (customers.isEmpty()) {
            System.out.println("No customers found.");
            return;
        }

        System.out.printf("%-5s %-25s %-30s %-30s%n",
                "ID", "Name", "Email", "Address");
        System.out.println("-".repeat(90));

        for (Customer customer : customers) {
            System.out.printf("%-5d %-25s %-30s %-30s%n",
                    customer.getCustomerId(),
                    InputValidator.truncate(customer.getName(), 25),
                    InputValidator.truncate(customer.getEmail(), 30),
                    InputValidator.truncate(customer.getAddress(), 30));
        }
    }

    /**
     * Display orders in a formatted table
     * @param orders List of orders to display
     */
    public static void displayOrdersTable(List<Order> orders) {
        if (orders.isEmpty()) {
            System.out.println("No orders found.");
            return;
        }

        System.out.printf("%-5s %-12s %-10s %-12s %-10s%n",
                "ID", "Customer ID", "Status", "Total", "Date");
        System.out.println("-".repeat(60));

        for (Order order : orders) {
            System.out.printf("%-5d %-12d %-10s $%-11.2f %-10s%n",
                    order.getOrderId(),
                    order.getCustomerId(),
                    order.getStatus(),
                    order.getTotalAmount(),
                    order.getOrderDate() != null ? order.getOrderDate().toString() : "N/A");
        }
    }

    /**
     * Display detailed order information
     * @param order Order to display
     */
    public static void displayOrderDetails(Order order) {
        System.out.println("\n=== ORDER DETAILS ===");
        System.out.println("Order ID: " + order.getOrderId());
        System.out.println("Order Date: " + order.getOrderDate());
        System.out.println("Status: " + order.getStatus());
        System.out.println("Total Amount: $" + String.format("%.2f", order.getTotalAmount()));

        if (order.getTrackingNumber() != null) {
            System.out.println("Tracking Number: " + order.getTrackingNumber());
        }

        if (order.getShippedDate() != null) {
            System.out.println("Shipped Date: " + order.getShippedDate());
        }

        if (order.getDeliveredDate() != null) {
            System.out.println("Delivered Date: " + order.getDeliveredDate());
        }

        if (order.getEstimatedDeliveryDate() != null) {
            System.out.println("Estimated Delivery: " + order.getEstimatedDeliveryDate());
        }

        if (order.getNotes() != null && !order.getNotes().trim().isEmpty()) {
            System.out.println("Notes: " + order.getNotes());
        }

        // Display order items
        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            System.out.println("\n--- ORDER ITEMS ---");
            System.out.printf("%-40s %-8s %-10s %-10s%n", "Book Title", "Qty", "Price", "Total");
            System.out.println("=".repeat(75));

            for (OrderItem item : order.getOrderItems()) {
                Book book = bookDAO.getBookById(item.getBookId());
                String title = book != null ? book.getTitle() : "Unknown Book";
                if (title.length() > 37) {
                    title = title.substring(0, 37) + "...";
                }

                double itemTotal = item.getQuantity() * item.getUnitPrice();
                System.out.printf("%-40s %-8d $%-9.2f $%-9.2f%n",
                    title,
                    item.getQuantity(),
                    item.getUnitPrice(),
                    itemTotal
                );
            }
        }
        System.out.println();
    }

    /**
     * Display order summary for confirmation
     * @param orderItems List of order items
     * @param totalAmount Total amount
     */
    public static void displayOrderSummary(List<OrderItem> orderItems, double totalAmount) {
        System.out.println("\n=== ORDER SUMMARY ===");
        System.out.printf("%-40s %-8s %-10s %-10s%n", "Book Title", "Qty", "Price", "Subtotal");
        System.out.println("=".repeat(75));

        for (OrderItem item : orderItems) {
            Book book = bookDAO.getBookById(item.getBookId());
            String title = book != null ? book.getTitle() : "Unknown Book";
            if (title.length() > 37) {
                title = title.substring(0, 37) + "...";
            }

            double subtotal = item.getQuantity() * item.getUnitPrice();
            System.out.printf("%-40s %-8d $%-9.2f $%-9.2f%n",
                title,
                item.getQuantity(),
                item.getUnitPrice(),
                subtotal
            );
        }

        System.out.println("=".repeat(75));
        System.out.printf("%-59s $%-9.2f%n", "TOTAL:", totalAmount);
        System.out.println();
    }

    /**
     * Display simple order summary
     * @param order Order to display
     */
    public static void displayOrderSummary(Order order) {
        System.out.println("Order ID: " + order.getOrderId());
        System.out.println("Customer ID: " + order.getCustomerId());
        System.out.println("Status: " + order.getStatus());
        System.out.println("Total Amount: $" + String.format("%.2f", order.getTotalAmount()));
        System.out.println("Order Date: " + (order.getOrderDate() != null ? order.getOrderDate() : "N/A"));
        if (order.getNotes() != null && !order.getNotes().trim().isEmpty()) {
            System.out.println("Notes: " + order.getNotes());
        }
    }

    /**
     * Display books in a simple list format with stock quantity
     * @param books List of books to display
     */
    public static void displayBookList(List<Book> books) {
        if (books == null || books.isEmpty()) {
            System.out.println("No books to display.");
            return;
        }

        // Print header
        System.out.printf("%-30s %-20s %-10s %-8s%n", "Title", "Author", "Price", "Stock");
        System.out.println("=".repeat(70));

        for (Book book : books) {
            System.out.printf("%-30s %-20s $%-9.2f %-8d%n",
                    InputValidator.truncate(book.getTitle(), 30),
                    InputValidator.truncate(book.getAuthor(), 20),
                    book.getPrice(),
                    book.getStockQuantity());
        }
    }

    /**
     * Display users in a formatted table
     * @param users List of users to display
     */
    public static void displayUsersTable(List<User> users) {
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        System.out.printf("%-5s %-15s %-25s %-20s %-10s %-8s %-20s%n",
                "ID", "Username", "Name", "Email", "Role", "Active", "Last Login");
        System.out.println("-".repeat(105));

        for (User user : users) {
            System.out.printf("%-5d %-15s %-25s %-20s %-10s %-8s %-20s%n",
                    user.getUserId(),
                    InputValidator.truncate(user.getUsername(), 15),
                    InputValidator.truncate(user.getFullName(), 25),
                    InputValidator.truncate(user.getEmail(), 20),
                    user.getRole(),
                    user.isActive() ? "Yes" : "No",
                    user.getLastLogin() != null ? user.getLastLogin().toString().substring(0, 19) : "Never");
        }
    }

    /**
     * Set BookDAO instance (useful for testing)
     * @param bookDAO BookDAO instance to use
     */
    public static void setBookDAO(BookDAO bookDAO) {
        DisplayFormatter.bookDAO = bookDAO;
    }
}
