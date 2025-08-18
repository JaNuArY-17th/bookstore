package com.bookstore.util.algorithms;

import com.bookstore.model.Order;
import com.bookstore.model.Book;
import com.bookstore.model.Customer;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Utility class providing searching algorithms for various data types.
 * Includes both generic and specialized search implementations.
 */
public class SearchingAlgorithms {

    // Comparator for OrderId
    public static final Comparator<Order> ORDER_ID_COMPARATOR = (order1, order2) ->
        Integer.compare(order1.getOrderId(), order2.getOrderId());

    // Generic Binary Search Implementation
    public static <T> T binarySearch(List<T> sortedList, T target, Comparator<T> comparator) {
        if (sortedList == null || sortedList.isEmpty()) {
            return null;
        }

        int low = 0;
        int high = sortedList.size() - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            T midElement = sortedList.get(mid);

            int comparison = comparator.compare(midElement, target);

            if (comparison == 0) {
                return midElement;
            } else if (comparison < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return null;
    }

    // Convenience method for Order search by ID (delegates to generic binarySearch)
    public static Order binarySearchOrderById(List<Order> sortedOrders, int targetOrderId) {
        if (sortedOrders == null || sortedOrders.isEmpty()) {
            return null;
        }

        // Create a dummy order with the target ID for comparison
        Order targetOrder = new Order();
        targetOrder.setOrderId(targetOrderId);

        return binarySearch(sortedOrders, targetOrder, ORDER_ID_COMPARATOR);
    }

    /**
     * Search books by search term with weighted scoring
     * @param books List of books to search
     * @param searchTerm The search term
     * @return List of books matching the search term, sorted by relevance
     */
    public static List<Book> searchBooks(List<Book> books, String searchTerm) {
        if (books == null || books.isEmpty() || searchTerm == null || searchTerm.trim().isEmpty()) {
            return new ArrayList<>(books != null ? books : new ArrayList<>());
        }

        String term = searchTerm.toLowerCase().trim();
        List<BookSearchResult> results = new ArrayList<>();

        for (Book book : books) {
            int score = calculateBookSearchScore(book, term);
            if (score > 0) {
                results.add(new BookSearchResult(book, score));
            }
        }

        // Sort by relevance score (highest first)
        results.sort((a, b) -> Integer.compare(b.score, a.score));

        return results.stream()
                .map(result -> result.book)
                .collect(Collectors.toList());
    }

    /**
     * Search customers by search term
     * @param customers List of customers to search
     * @param searchTerm The search term
     * @return List of customers matching the search term
     */
    public static List<Customer> searchCustomers(List<Customer> customers, String searchTerm) {
        if (customers == null || customers.isEmpty() || searchTerm == null || searchTerm.trim().isEmpty()) {
            return new ArrayList<>(customers != null ? customers : new ArrayList<>());
        }

        String term = searchTerm.toLowerCase().trim();
        List<CustomerSearchResult> results = new ArrayList<>();

        for (Customer customer : customers) {
            int score = calculateCustomerSearchScore(customer, term);
            if (score > 0) {
                results.add(new CustomerSearchResult(customer, score));
            }
        }

        // Sort by relevance score (highest first)
        results.sort((a, b) -> Integer.compare(b.score, a.score));

        return results.stream()
                .map(result -> result.customer)
                .collect(Collectors.toList());
    }

    /**
     * Search orders by search term
     * @param orders List of orders to search
     * @param searchTerm The search term
     * @return List of orders matching the search term
     */
    public static List<Order> searchOrders(List<Order> orders, String searchTerm) {
        if (orders == null || orders.isEmpty() || searchTerm == null || searchTerm.trim().isEmpty()) {
            return new ArrayList<>(orders != null ? orders : new ArrayList<>());
        }

        String term = searchTerm.toLowerCase().trim();
        List<OrderSearchResult> results = new ArrayList<>();

        for (Order order : orders) {
            int score = calculateOrderSearchScore(order, term);
            if (score > 0) {
                results.add(new OrderSearchResult(order, score));
            }
        }

        // Sort by relevance score (highest first)
        results.sort((a, b) -> Integer.compare(b.score, a.score));

        return results.stream()
                .map(result -> result.order)
                .collect(Collectors.toList());
    }

    // Helper methods for calculating search scores

    private static int calculateBookSearchScore(Book book, String searchTerm) {
        int score = 0;

        // Exact ID match (highest priority)
        try {
            int searchId = Integer.parseInt(searchTerm);
            if (book.getBookId() == searchId) {
                return 1000;
            }
        } catch (NumberFormatException ignored) {}

        // Title matching
        if (book.getTitle() != null) {
            String title = book.getTitle().toLowerCase();
            if (title.equals(searchTerm)) {
                score += 100; // Exact title match
            } else if (title.contains(searchTerm)) {
                score += 50; // Partial title match
            }
        }

        // Author matching
        if (book.getAuthor() != null) {
            String author = book.getAuthor().toLowerCase();
            if (author.equals(searchTerm)) {
                score += 80; // Exact author match
            } else if (author.contains(searchTerm)) {
                score += 30; // Partial author match
            }
        }

        // ISBN matching
        if (book.getIsbn() != null && book.getIsbn().toLowerCase().contains(searchTerm)) {
            score += 60;
        }

        return score;
    }

    private static int calculateCustomerSearchScore(Customer customer, String searchTerm) {
        int score = 0;

        // Exact ID match (highest priority)
        try {
            int searchId = Integer.parseInt(searchTerm);
            if (customer.getCustomerId() == searchId) {
                return 1000;
            }
        } catch (NumberFormatException ignored) {}

        // Name matching
        if (customer.getName() != null) {
            String name = customer.getName().toLowerCase();
            if (name.equals(searchTerm)) {
                score += 100; // Exact name match
            } else if (name.contains(searchTerm)) {
                score += 50; // Partial name match
            }
        }

        // Email matching
        if (customer.getEmail() != null) {
            String email = customer.getEmail().toLowerCase();
            if (email.equals(searchTerm)) {
                score += 80; // Exact email match
            } else if (email.contains(searchTerm)) {
                score += 30; // Partial email match
            }
        }

        return score;
    }

    private static int calculateOrderSearchScore(Order order, String searchTerm) {
        int score = 0;

        // Exact order ID match (highest priority)
        try {
            int searchId = Integer.parseInt(searchTerm);
            if (order.getOrderId() == searchId) {
                return 1000;
            }
            // Customer ID match
            if (order.getCustomerId() == searchId) {
                return 500;
            }
        } catch (NumberFormatException ignored) {}

        // Tracking number matching
        if (order.getTrackingNumber() != null) {
            String trackingNumber = order.getTrackingNumber().toLowerCase();
            if (trackingNumber.equals(searchTerm)) {
                score += 80;
            } else if (trackingNumber.contains(searchTerm)) {
                score += 40;
            }
        }

        return score;
    }

    // Inner classes for search results with scoring

    private static class BookSearchResult {
        final Book book;
        final int score;

        BookSearchResult(Book book, int score) {
            this.book = book;
            this.score = score;
        }
    }

    private static class CustomerSearchResult {
        final Customer customer;
        final int score;

        CustomerSearchResult(Customer customer, int score) {
            this.customer = customer;
            this.score = score;
        }
    }

    private static class OrderSearchResult {
        final Order order;
        final int score;

        OrderSearchResult(Order order, int score) {
            this.order = order;
            this.score = score;
        }
    }
}