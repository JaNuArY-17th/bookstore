package com.bookstore.controller;

import com.bookstore.dao.BookDAO;
import com.bookstore.dao.CustomerDAO;
import com.bookstore.dao.OrderDAO;
import com.bookstore.model.Book;
import com.bookstore.model.Customer;
import com.bookstore.model.Order;
import com.bookstore.model.OrderItem;
import com.bookstore.model.OrderStatus;
import com.bookstore.model.User;
import com.bookstore.service.AuthService;
import com.bookstore.service.OrderService;
import com.bookstore.service.BookService;
import com.bookstore.util.ui.DisplayFormatter;
import com.bookstore.util.ui.InputValidator;
import com.bookstore.util.ui.PaginationUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for order management operations
 * Extracted from Main.java to improve code organization
 */
public class OrderManagementController {
    private OrderDAO orderDAO;
    private BookDAO bookDAO;
    private CustomerDAO customerDAO;
    private OrderService orderService;
    private AuthService authService;
    private BookService bookService;

    public OrderManagementController(OrderDAO orderDAO, BookDAO bookDAO, CustomerDAO customerDAO,
            OrderService orderService, AuthService authService, BookService bookService) {
        this.orderDAO = orderDAO;
        this.bookDAO = bookDAO;
        this.customerDAO = customerDAO;
        this.orderService = orderService;
        this.authService = authService;
        this.bookService = bookService;
    }

    /**
     * Display and handle order management menu
     */
    public void showOrderManagementMenu() {
        while (true) {
            System.out.println("\n=== ORDER MANAGEMENT ===");
            System.out.println("1. Create New Order");
            System.out.println("2. View All Orders");
            System.out.println("3. Search Order by ID");
            System.out.println("4. Advanced Search Orders");
            System.out.println("5. Sort Orders");
            System.out.println("6. Filter Orders by Status");
            // System.out.println("7. View Order Queue Status");
            System.out.println("0. Back to Main Menu");

            int choice = InputValidator.getIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    createNewOrder();
                    break;
                case 2:
                    viewAllOrders();
                    break;
                case 3:
                    searchOrderById();
                    break;
                case 4:
                    advancedSearchOrders();
                    break;
                case 5:
                    sortOrders();
                    break;
                case 6:
                    filterOrdersByStatus();
                    break;
                // case 7:
                //     viewOrderQueueStatus();
                //     break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    /**
     * Create a new order
     */
    public void createNewOrder() {
        System.out.println("=== CREATE NEW ORDER ===");

        // Show available customers
        List<Customer> customers = customerDAO.getAllCustomers();
        if (customers.isEmpty()) {
            System.out.println("No customers available. Please add customers first.");
            return;
        }

        System.out.println("Available customers:");
        for (Customer customer : customers) {
            System.out.printf("%d. %s (%s)%n",
                    customer.getCustomerId(),
                    customer.getName(),
                    customer.getEmail());
        }

        int customerId = InputValidator.getIntInput("Enter Customer ID: ");
        Customer customer = customerDAO.getCustomerById(customerId);
        if (customer == null) {
            System.out.println("Invalid customer ID.");
            return;
        }

        // Show available books
        List<Book> books = bookService.getAllBooks();
        if (books.isEmpty()) {
            System.out.println("No books available.");
            return;
        }

        System.out.println("Available books:");
        for (Book book : books) {
            System.out.printf("%d. %s by %s - $%.2f (Stock: %d)%n",
                    book.getBookId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPrice(),
                    book.getStockQuantity());
        }

        // Create order items
        List<OrderItem> orderItems = new ArrayList<>();
        boolean addingItems = true;

        while (addingItems) {
            int bookId = InputValidator.getIntInput("Enter Book ID to add to order (0 to finish): ");
            if (bookId == 0) {
                addingItems = false;
                continue;
            }

            Book book = bookService.getBookById(bookId);
            if (book == null) {
                System.out.println("Invalid book ID.");
                continue;
            }

            int quantity = InputValidator.getIntInput("Enter quantity: ");
            if (quantity <= 0) {
                System.out.println("Invalid quantity.");
                continue;
            }

            OrderItem item = new OrderItem(bookId, quantity, book.getPrice());
            orderItems.add(item);
            System.out.println("Added: " + quantity + " x " + book.getTitle());
        }

        if (orderItems.isEmpty()) {
            System.out.println("No items added to order.");
            return;
        }

        // Create order
        Order order = new Order();
        order.setCustomerId(customerId);
        order.setOrderItems(orderItems);

        int orderId = orderService.createNewOrder(order, authService.getCurrentUser());
        if (orderId != -1) {
            System.out.println("Order created successfully with ID: " + orderId);
            System.out.println("Order added to processing queue.");
        } else {
            System.out.println("Failed to create order.");
        }
    }

    /**
     * View all orders in the system
     */
    public void viewAllOrders() {
        System.out.println("=== ALL ORDERS ===");
        List<Order> orders = orderDAO.getAllOrders();
        DisplayFormatter.displayOrdersTable(orders);
    }

    /**
     * Search for an order by ID
     */
    public void searchOrderById() {
        int orderId = InputValidator.getIntInput("Enter Order ID: ");
        Order order = orderService.findOrderById(orderId);

        if (order != null) {
            System.out.println("Order found:");
            System.out.println(order);

            // Show order items
            List<OrderItem> items = orderDAO.getOrderItemsByOrderId(orderId);
            if (!items.isEmpty()) {
                System.out.println("Order Items:");
                for (OrderItem item : items) {
                    Book book = bookService.getBookById(item.getBookId());
                    System.out.printf("- %s x %d @ $%.2f each%n",
                            book != null ? book.getTitle() : "Unknown Book",
                            item.getQuantity(),
                            item.getUnitPrice());
                }
            }
        } else {
            System.out.println("Order not found with ID: " + orderId);
        }
    }

    /**
     * View order queue status
     */
    public void viewOrderQueueStatus() {
        System.out.println("=== ORDER QUEUE STATUS ===");
        // Note: This would need to be implemented in OrderProcessingQueue
        System.out.println("Queue functionality available through order processing.");
    }

    /**
     * Show customer browse books interface with pagination
     */
    public void showCustomerBrowseBooks() {
        int currentPage = 1;
        final int PAGE_SIZE = 10;

        while (true) {
            System.out.println("\n=== BROWSE BOOKS ===");
            try {
                List<Book> allBooks = bookService.getAllBooks();
                if (allBooks.isEmpty()) {
                    System.out.println("No books available.");
                    System.out.println("\nPress Enter to continue...");
                    InputValidator.getStringInput("");
                    return;
                }

                int totalPages = PaginationUtil.getTotalPages(allBooks.size(), PAGE_SIZE);
                currentPage = PaginationUtil.validatePageNumber(currentPage, totalPages);
                List<Book> pageBooks = PaginationUtil.getPage(allBooks, currentPage, PAGE_SIZE);

                // Display pagination info
                PaginationUtil.displayPaginationInfo(currentPage, totalPages, allBooks.size(), PAGE_SIZE);

                // Display books table
                System.out.printf("%-5s %-30s %-20s %-10s %-8s%n", "ID", "Title", "Author", "Price", "Stock");
                System.out.println("=".repeat(80));

                for (Book book : pageBooks) {
                    System.out.printf("%-5d %-30s %-20s $%-9.2f %-8d%n",
                            book.getBookId(),
                            book.getTitle().length() > 30 ? book.getTitle().substring(0, 27) + "..."
                                    : book.getTitle(),
                            book.getAuthor().length() > 20 ? book.getAuthor().substring(0, 17) + "..."
                                    : book.getAuthor(),
                            book.getPrice(),
                            book.getStockQuantity());
                }

                // Display navigation options
                PaginationUtil.displayPaginationNavigation(currentPage, totalPages);

                System.out.println("\n=== BROWSE OPTIONS ===");
                System.out.println("1. View Book Details");
                System.out.println("2. Add Book to Cart");
                System.out.println("3. Search Books");
                System.out.println("4. Filter Books by Category");
                System.out.println("5. View Cart (" + authService.getCartService().getItemCount() + " items)");
                System.out.println("6. Refresh Book List");
                System.out.println("0. Back to Customer Menu");

                String input = InputValidator.getStringInput("Enter your choice (or navigation command): ");

                // Handle pagination navigation first
                int newPage = PaginationUtil.handleNavigationInput(input, currentPage, totalPages);
                if (newPage > 0) {
                    currentPage = newPage;
                    continue;
                } else if (newPage == -2) { // Go to page
                    int targetPage = InputValidator.getIntInput("Enter page number (1-" + totalPages + "): ");
                    currentPage = PaginationUtil.validatePageNumber(targetPage, totalPages);
                    continue;
                }

                // Handle menu options
                try {
                    int choice = Integer.parseInt(input);
                    switch (choice) {
                        case 1:
                            viewBookDetailsCustomer();
                            break;
                        case 2:
                            addBookToCartFromBrowse();
                            break;
                        case 3:
                            showCustomerSearchBooks();
                            break;
                        case 4:
                            showCustomerFilterBooks();
                            break;
                        case 5:
                            showCartManagement();
                            break;
                        case 6:
                            currentPage = 1; // Reset to first page on refresh
                            break;
                        case 0:
                            return;
                        default:
                            System.out.println("Invalid choice.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number or navigation command.");
                }
            } catch (Exception e) {
                System.out.println("Error retrieving books: " + e.getMessage());
            }
        }
    }

    /**
     * Show customer filter books interface
     */
    public void showCustomerFilterBooks() {
        System.out.println("=== FILTER BOOKS BY CATEGORY ===");

        try {
            // Get all available categories
            List<String> categories = bookService.getAllCategories();

            if (categories.isEmpty()) {
                System.out.println("No categories available.");
                System.out.println("\nPress Enter to continue...");
                InputValidator.getStringInput("");
                return;
            }

            // Display available categories
            System.out.println("Available categories:");
            for (int i = 0; i < categories.size(); i++) {
                System.out.println((i + 1) + ". " + categories.get(i));
            }
            System.out.println("0. Cancel");

            int choice = InputValidator.getIntInput("Enter your choice: ");

            if (choice == 0) {
                return;
            }

            if (choice < 1 || choice > categories.size()) {
                System.out.println("Invalid choice.");
                return;
            }

            String selectedCategory = categories.get(choice - 1);
            boolean ascending = InputValidator.getConfirmation("Sort results in ascending order? (y/n): ");

            List<Book> filteredBooks = bookService.filter(selectedCategory, ascending);

            if (filteredBooks.isEmpty()) {
                System.out.println("No books found in category: " + selectedCategory);
            } else {
                System.out.println("\n=== FILTERED BOOKS ===");
                System.out.println("Books in category '" + selectedCategory + "' (" + filteredBooks.size() + " books):");

                // Show filtered results with pagination and customer options
                showPaginatedFilteredResults(filteredBooks, selectedCategory);
            }
        } catch (Exception e) {
            System.out.println("Error filtering books: " + e.getMessage());
        }

        System.out.println("\nPress Enter to continue...");
        InputValidator.getStringInput("");
    }

    /**
     * Show paginated filtered results for customers
     */
    private void showPaginatedFilteredResults(List<Book> filteredBooks, String category) {
        int currentPage = 1;
        final int PAGE_SIZE = 10;

        while (true) {
            System.out.println("\n=== BOOKS IN CATEGORY: \"" + category.toUpperCase() + "\" ===");

            // Calculate pagination
            int totalPages = (int) Math.ceil((double) filteredBooks.size() / PAGE_SIZE);
            int startIndex = (currentPage - 1) * PAGE_SIZE;
            int endIndex = Math.min(startIndex + PAGE_SIZE, filteredBooks.size());

            // Display current page of books
            List<Book> currentPageBooks = filteredBooks.subList(startIndex, endIndex);

            // Display pagination info
            PaginationUtil.displayPaginationInfo(currentPage, totalPages, filteredBooks.size(), PAGE_SIZE);

            // Display books table
            System.out.printf("%-5s %-30s %-20s %-10s %-8s%n", "ID", "Title", "Author", "Price", "Stock");
            System.out.println("=".repeat(80));

            for (Book book : currentPageBooks) {
                System.out.printf("%-5d %-30s %-20s $%-9.2f %-8d%n",
                        book.getBookId(),
                        InputValidator.truncate(book.getTitle(), 30),
                        InputValidator.truncate(book.getAuthor(), 20),
                        book.getPrice(),
                        book.getStockQuantity());
            }

            // Display navigation and options
            if (totalPages > 1) {
                PaginationUtil.displayPaginationNavigation(currentPage, totalPages);
            }

            System.out.println("\n=== FILTER OPTIONS ===");
            System.out.println("1. View Book Details");
            System.out.println("2. Add Book to Cart");
            System.out.println("3. View Cart (" + authService.getCartService().getItemCount() + " items)");
            System.out.println("4. New Filter");
            System.out.println("0. Back to Browse Menu");

            String input = InputValidator.getStringInput("Enter your choice (or navigation command): ");

            // Handle menu options first
            try {
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1:
                        viewBookDetailsCustomer();
                        break;
                    case 2:
                        addBookToCartFromBrowse();
                        break;
                    case 3:
                        showCartManagement();
                        break;
                    case 4:
                        showCustomerFilterBooks(); // Start new filter
                        return;
                    case 0:
                        return; // Return to browse menu
                    default:
                        System.out.println("Invalid choice.");
                        continue;
                }
                continue; // Continue the loop after handling menu option
            } catch (NumberFormatException e) {
                // Not a number, try navigation commands
            }

            // Handle pagination navigation if not a menu option
            int newPage = PaginationUtil.handleNavigationInput(input, currentPage, totalPages);
            if (newPage > 0) {
                currentPage = newPage;
                continue;
            } else if (newPage == -2) { // Go to page
                int targetPage = InputValidator.getIntInput("Enter page number (1-" + totalPages + "): ");
                currentPage = PaginationUtil.validatePageNumber(targetPage, totalPages);
                continue;
            } else {
                System.out.println("Invalid input. Please enter a number or navigation command.");
                continue;
            }
        }
    }

    /**
     * View book details for customers
     */
    private void viewBookDetailsCustomer() {
        int bookId = InputValidator.getIntInput("Enter Book ID to view details: ");
        Book book = bookService.getBookById(bookId);

        if (book == null) {
            System.out.println("Book not found with ID: " + bookId);
            return;
        }

        System.out.println("\n=== BOOK DETAILS ===");
        System.out.println("ID: " + book.getBookId());
        System.out.println("Title: " + book.getTitle());
        System.out.println("Author: " + book.getAuthor());
        System.out.println("ISBN: " + book.getIsbn());
        System.out.println("Price: $" + String.format("%.2f", book.getPrice()));
        System.out.println("Stock: " + book.getStockQuantity());

        if (book.getStockQuantity() > 0) {
            if (InputValidator.getConfirmation("\nWould you like to add this book to your cart? (y/n): ")) {
                int quantity = InputValidator.getIntInput("Enter quantity: ");
                if (quantity > 0 && quantity <= book.getStockQuantity()) {
                    authService.getCartService().addToCart(book.getBookId(), quantity);
                } else {
                    System.out.println("Invalid quantity or insufficient stock.");
                }
            }
        } else {
            System.out.println("\nThis book is currently out of stock.");
        }
    }

    /**
     * Show cart management interface
     */
    public void showCartManagement() {
        while (true) {
            authService.getCartService().viewCart();

            if (authService.getCartService().isEmpty()) {
                System.out.println("\nPress Enter to continue...");
                InputValidator.getStringInput("");
                return;
            }

            System.out.println("\n=== CART OPTIONS ===");
            System.out.println("1. Update Item Quantity");
            System.out.println("2. Remove Item");
            System.out.println("3. Clear Cart");
            System.out.println("4. Proceed to Checkout");
            System.out.println("0. Back to Browse");

            int choice = InputValidator.getIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    updateCartItemQuantity();
                    break;
                case 2:
                    removeCartItem();
                    break;
                case 3:
                    if (InputValidator.getConfirmation("Are you sure you want to clear your cart? (y/n): ")) {
                        authService.getCartService().clearCart();
                    }
                    break;
                case 4:
                    proceedToCheckoutFromCart();
                    return;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    /**
     * Update cart item quantity
     */
    private void updateCartItemQuantity() {
        int bookId = InputValidator.getIntInput("Enter Book ID to update: ");
        int newQuantity = InputValidator.getIntInput("Enter new quantity (0 to remove): ");
        authService.getCartService().updateQuantity(bookId, newQuantity);
    }

    /**
     * Remove item from cart
     */
    private void removeCartItem() {
        int bookId = InputValidator.getIntInput("Enter Book ID to remove: ");
        authService.getCartService().removeFromCart(bookId);
    }

    /**
     * Proceed to checkout from cart
     */
    private void proceedToCheckoutFromCart() {
        if (authService.getCartService().isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }

        System.out.println("\n=== CHECKOUT ===");
        authService.getCartService().viewCart();

        if (InputValidator.getConfirmation("\nProceed to checkout? (y/n): ")) {
            // Transfer cart items to order and process
            showCustomerPlaceOrderFromCart();
        }
    }

    /**
     * Place order using items from cart
     */
    private void showCustomerPlaceOrderFromCart() {
        if (!authService.isLoggedIn()) {
            System.out.println("Please login to place an order.");
            return;
        }

        User currentUser = authService.getCurrentUser();
        if (!currentUser.isCustomer()) {
            System.out.println("This feature is only available for customers.");
            return;
        }

        // Get customer record for the logged-in user
        Customer customer = customerDAO.getCustomerByUserId(currentUser.getUserId());
        if (customer == null) {
            System.out.println("Customer profile not found. Please contact support.");
            return;
        }

        // Get cart items
        List<OrderItem> cartItems = authService.getCartService().getCartItems();
        if (cartItems.isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }

        // Validate stock availability
        boolean allItemsAvailable = true;
        for (OrderItem item : cartItems) {
            Book book = bookService.getBookById(item.getBookId());
            if (book == null || book.getStockQuantity() < item.getQuantity()) {
                System.out.println("Warning: " + (book != null ? book.getTitle() : "Unknown book") +
                                 " has insufficient stock.");
                allItemsAvailable = false;
            }
        }

        if (!allItemsAvailable) {
            System.out.println("Please update your cart and try again.");
            return;
        }

        // Show order summary
        double totalAmount = authService.getCartService().getCartTotal();
        DisplayFormatter.displayOrderSummary(cartItems, totalAmount);

        // Confirm order
        if (!InputValidator.getConfirmation("Confirm order? (y/n): ")) {
            System.out.println("Order cancelled.");
            return;
        }

        // Create and place order
        Order order = new Order();
        order.setCustomerId(customer.getCustomerId());
        order.setOrderItems(authService.getCartService().transferToOrder()); // This clears the cart

        int orderId = orderService.createNewOrder(order, currentUser);
        if (orderId != -1) {
            System.out.println("\n🎉 Order placed successfully!");
            System.out.println("Order ID: " + orderId);
            System.out.println("Total Amount: $" + String.format("%.2f", totalAmount));
            System.out.println("Order has been added to the processing queue.");
            System.out.println("\nYou can track your order status in 'My Orders' section.");
        } else {
            System.out.println("Failed to place order. Please try again or contact support.");
        }
    }

    /**
     * Add book to cart from browse view
     */
    private void addBookToCartFromBrowse() {
        int bookId = InputValidator.getIntInput("Enter Book ID to add to cart: ");
        Book book = bookService.getBookById(bookId);

        if (book == null) {
            System.out.println("Book not found with ID: " + bookId);
            return;
        }

        if (book.getStockQuantity() <= 0) {
            System.out.println("Sorry, this book is out of stock.");
            return;
        }

        System.out.println("Book: " + book.getTitle() + " by " + book.getAuthor());
        System.out.println("Price: $" + String.format("%.2f", book.getPrice()));
        System.out.println("Available stock: " + book.getStockQuantity());

        int quantity = InputValidator.getIntInput("Enter quantity to add: ");
        if (quantity > 0 && quantity <= book.getStockQuantity()) {
            authService.getCartService().addToCart(book.getBookId(), quantity);
        } else {
            System.out.println("Invalid quantity or insufficient stock.");
        }
    }

    /**
     * Show customer search books interface with pagination
     */
    public void showCustomerSearchBooks() {
        while (true) {
            System.out.println("\n=== SEARCH BOOKS ===");
            String searchTerm = InputValidator
                    .getTrimmedStringInput("Enter search term (title or author) or 'exit' to return: ");

            if (searchTerm.equalsIgnoreCase("exit")) {
                return;
            }

            if (searchTerm.isEmpty()) {
                System.out.println("Search term cannot be empty.");
                continue;
            }

            try {
                // Debug: Print the search term being used
                System.out.println("DEBUG: Searching for: '" + searchTerm + "'");

                List<Book> searchResults = bookService.search(searchTerm);
                if (searchResults.isEmpty()) {
                    System.out.println("No books found matching: " + searchTerm);
                    continue;
                }

                // Debug: Print number of results found
                System.out.println("DEBUG: Found " + searchResults.size() + " results for: '" + searchTerm + "'");

                // Handle pagination for search results
                showPaginatedSearchResults(searchResults, searchTerm);

            } catch (Exception e) {
                System.out.println("Error searching books: " + e.getMessage());
            }
        }
    }

    /**
     * Display paginated search results
     */
    private void showPaginatedSearchResults(List<Book> searchResults, String searchTerm) {
        int currentPage = 1;
        final int PAGE_SIZE = 10;

        // Debug: Print the search term received in pagination
        System.out.println("DEBUG: showPaginatedSearchResults called with searchTerm: '" + searchTerm + "'");
        System.out.println("DEBUG: Number of search results: " + searchResults.size());

        while (true) {
            System.out.println("\n=== SEARCH RESULTS FOR: \"" + searchTerm + "\" ===");

            int totalPages = PaginationUtil.getTotalPages(searchResults.size(), PAGE_SIZE);
            currentPage = PaginationUtil.validatePageNumber(currentPage, totalPages);
            List<Book> pageBooks = PaginationUtil.getPage(searchResults, currentPage, PAGE_SIZE);

            // Display pagination info
            PaginationUtil.displayPaginationInfo(currentPage, totalPages, searchResults.size(), PAGE_SIZE);

            // Display books table
            System.out.printf("%-5s %-30s %-20s %-10s %-8s%n", "ID", "Title", "Author", "Price", "Stock");
            System.out.println("=".repeat(80));

            for (Book book : pageBooks) {
                System.out.printf("%-5d %-30s %-20s $%-9.2f %-8d%n",
                        book.getBookId(),
                        book.getTitle().length() > 30 ? book.getTitle().substring(0, 27) + "..."
                                : book.getTitle(),
                        book.getAuthor().length() > 20 ? book.getAuthor().substring(0, 17) + "..."
                                : book.getAuthor(),
                        book.getPrice(),
                        book.getStockQuantity());
            }

            // Display navigation options
            PaginationUtil.displayPaginationNavigation(currentPage, totalPages);

            System.out.println("\n=== SEARCH OPTIONS ===");
            System.out.println("1. View Book Details");
            System.out.println("2. Add Book to Cart");
            System.out.println("3. View Cart (" + authService.getCartService().getItemCount() + " items)");
            System.out.println("4. New Search");
            System.out.println("0. Back to Customer Menu");

            String input = InputValidator.getStringInput("Enter your choice (or navigation command): ");

            // Handle pagination navigation first
            int newPage = PaginationUtil.handleNavigationInput(input, currentPage, totalPages);
            if (newPage > 0) {
                currentPage = newPage;
                continue;
            } else if (newPage == -2) { // Go to page
                int targetPage = InputValidator.getIntInput("Enter page number (1-" + totalPages + "): ");
                currentPage = PaginationUtil.validatePageNumber(targetPage, totalPages);
                continue;
            }

            // Handle menu options
            try {
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1:
                        viewBookDetailsCustomer();
                        break;
                    case 2:
                        addBookToCartFromBrowse();
                        break;
                    case 3:
                        showCartManagement();
                        break;
                    case 4:
                        return; // Return to search input
                    case 0:
                        return; // Return to customer menu
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number or navigation command.");
            }
        }
    }

    /**
     * Show customer view orders interface
     */
    public void showCustomerViewOrders() {
        System.out.println("=== MY ORDERS ===");

        if (!authService.isLoggedIn()) {
            System.out.println("Please login to view your orders.");
            return;
        }

        User currentUser = authService.getCurrentUser();
        if (!currentUser.isCustomer()) {
            System.out.println("This feature is only available for customers.");
            return;
        }

        try {
            List<Order> userOrders = orderDAO.getOrdersByUserId(currentUser.getUserId());

            if (userOrders.isEmpty()) {
                System.out.println("You have no orders yet.");
                System.out.println("Use 'Place New Order' to create your first order!");
                return;
            }

            System.out.printf("%-8s %-12s %-15s %-15s %-10s%n",
                    "Order ID", "Date", "Status", "Tracking", "Total");
            System.out.println("=".repeat(75));

            for (Order order : userOrders) {
                String tracking = order.getTrackingNumber() != null ? order.getTrackingNumber() : "N/A";
                System.out.printf("%-8d %-12s %-15s %-15s $%-9.2f%n",
                        order.getOrderId(),
                        order.getOrderDate().toString(), // Full date
                        order.getStatus(),
                        tracking,
                        order.getTotalAmount());
            }

            if (InputValidator.getConfirmation("\nWould you like to view details of a specific order? (y/n): ")) {
                try {
                    int orderId = InputValidator.getIntInput("Enter Order ID: ");

                    // Check if the order belongs to this user
                    boolean orderFound = false;
                    for (Order order : userOrders) {
                        if (order.getOrderId() == orderId) {
                            orderFound = true;
                            DisplayFormatter.displayOrderDetails(order);
                            break;
                        }
                    }

                    if (!orderFound) {
                        System.out.println("Order not found or doesn't belong to you.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid order ID format.");
                }
            }

        } catch (Exception e) {
            System.out.println("Error retrieving your orders: " + e.getMessage());
        }
    }

    /**
     * Show customer place order interface
     */
    public void showCustomerPlaceOrder() {
        System.out.println("=== PLACE NEW ORDER ===");

        if (!authService.isLoggedIn()) {
            System.out.println("Please login to place an order.");
            return;
        }

        User currentUser = authService.getCurrentUser();
        if (!currentUser.isCustomer()) {
            System.out.println("This feature is only available for customers.");
            return;
        }

        // Get customer record for the logged-in user
        Customer customer = customerDAO.getCustomerByUserId(currentUser.getUserId());
        if (customer == null) {
            System.out.println("Customer profile not found. Please contact support.");
            return;
        }

        // Check if customer profile is complete
        if (isFirstLoginOrIncompleteProfile((Customer) currentUser)) {
            System.out.println("Please complete your profile before placing an order.");
            if (InputValidator.getConfirmation("Would you like to complete your profile now? (y/n): ")) {
                // This would need to call the authentication controller's profile completion
                System.out.println("Please use Profile Settings to complete your profile first.");
                return;
            } else {
                System.out.println("Profile completion is required to place orders.");
                return;
            }
        }

        // Show available books
        List<Book> books = bookService.getAllBooks();
        if (books.isEmpty()) {
            System.out.println("No books available for purchase at the moment.");
            return;
        }

        System.out.println("Available books:");
        System.out.printf("%-5s %-30s %-20s %-10s %-8s%n", "ID", "Title", "Author", "Price", "Stock");
        System.out.println("=".repeat(80));

        for (Book book : books) {
            if (book.getStockQuantity() > 0) { // Only show books in stock
                System.out.printf("%-5d %-30s %-20s $%-9.2f %-8d%n",
                        book.getBookId(),
                        book.getTitle().length() > 30 ? book.getTitle().substring(0, 27) + "..." : book.getTitle(),
                        book.getAuthor().length() > 20 ? book.getAuthor().substring(0, 17) + "..." : book.getAuthor(),
                        book.getPrice(),
                        book.getStockQuantity());
            }
        }

        // Create shopping cart
        List<OrderItem> orderItems = new ArrayList<>();
        double totalAmount = 0.0;
        boolean addingItems = true;

        System.out.println("\nAdd books to your order:");

        while (addingItems) {
            int bookId = InputValidator.getIntInput("Enter Book ID to add to cart (0 to finish): ");
            if (bookId == 0) {
                addingItems = false;
                continue;
            }

            Book book = bookService.getBookById(bookId);
            if (book == null) {
                System.out.println("Invalid book ID. Please try again.");
                continue;
            }

            if (book.getStockQuantity() <= 0) {
                System.out.println("Sorry, this book is out of stock.");
                continue;
            }

            System.out.println("Selected: " + book.getTitle() + " by " + book.getAuthor());
            System.out.println("Price: $" + String.format("%.2f", book.getPrice()));
            System.out.println("Available stock: " + book.getStockQuantity());

            int quantity = InputValidator.getIntInput("Enter quantity: ");
            if (quantity <= 0) {
                System.out.println("Invalid quantity. Please enter a positive number.");
                continue;
            }

            if (quantity > book.getStockQuantity()) {
                System.out.println("Sorry, only " + book.getStockQuantity() + " copies available.");
                continue;
            }

            // Check if book is already in cart
            boolean bookExists = false;
            for (OrderItem existingItem : orderItems) {
                if (existingItem.getBookId() == bookId) {
                    int newQuantity = existingItem.getQuantity() + quantity;
                    if (newQuantity > book.getStockQuantity()) {
                        System.out.println(
                                "Total quantity would exceed available stock (" + book.getStockQuantity() + ").");
                        continue;
                    }
                    existingItem.setQuantity(newQuantity);
                    bookExists = true;
                    System.out.println("Updated quantity for " + book.getTitle() + " to " + newQuantity);
                    break;
                }
            }

            if (!bookExists) {
                OrderItem item = new OrderItem(bookId, quantity, book.getPrice());
                orderItems.add(item);
                System.out.println("Added to cart: " + quantity + " x " + book.getTitle());
            }

            // Calculate current total
            totalAmount = 0.0;
            for (OrderItem item : orderItems) {
                totalAmount += item.getQuantity() * item.getUnitPrice();
            }

            System.out.println("Current cart total: $" + String.format("%.2f", totalAmount));
            System.out.println();
        }

        if (orderItems.isEmpty()) {
            System.out.println("No items added to cart. Order cancelled.");
            return;
        }

        // Show order summary
        DisplayFormatter.displayOrderSummary(orderItems, totalAmount);

        // Confirm order
        if (!InputValidator.getConfirmation("Confirm order? (y/n): ")) {
            System.out.println("Order cancelled.");
            return;
        }

        // Create and place order
        Order order = new Order();
        order.setCustomerId(customer.getCustomerId());
        order.setOrderItems(orderItems);

        int orderId = orderService.createNewOrder(order, currentUser);
        if (orderId != -1) {
            System.out.println("\n🎉 Order placed successfully!");
            System.out.println("Order ID: " + orderId);
            System.out.println("Total Amount: $" + String.format("%.2f", totalAmount));
            System.out.println("Order has been added to the processing queue.");
            System.out.println("\nYou can track your order status in 'My Orders' section.");
        } else {
            System.out.println("Failed to place order. Please try again or contact support.");
        }
    }

    /**
     * Check if this is a customer's first login or if their profile is incomplete
     */
    private boolean isFirstLoginOrIncompleteProfile(Customer customer) {
        // Check if essential customer information is missing
        return customer.getAddress() == null || customer.getAddress().trim().isEmpty() ||
                customer.getPhoneNumber() == null || customer.getPhoneNumber().trim().isEmpty() ||
                customer.getPreferredPaymentMethod() == null || customer.getPreferredPaymentMethod().trim().isEmpty();
    }

    /**
     * Show order queue processing interface
     */
    public void showOrderQueueProcessing() {
        while (true) {
            System.out.println("\n=== ORDER QUEUE MANAGEMENT ===");
            User currentUser = authService.getCurrentUser();

            // Get all orders from database
            List<Order> allOrders = orderDAO.getAllOrders();

            if (allOrders.isEmpty()) {
                System.out.println("No orders in the system.");
                System.out.println("\n=== QUEUE MANAGEMENT OPTIONS ===");
                System.out.println("1. Refresh Queue");
                System.out.println("0. Back to Main Menu");

                int choice = InputValidator.getIntInput("Enter your choice: ");
                if (choice == 0) {
                    return;
                }
                continue;
            }

            // Show comprehensive queue status and orders table
            showComprehensiveQueueStatus(allOrders);

            System.out.println("\n=== QUEUE MANAGEMENT OPTIONS ===");
            System.out.println("1. Process Next Pending Order");
            System.out.println("2. View Orders by Status");
            System.out.println("3. Update Order Status");
            System.out.println("4. View Order Details");
            System.out.println("5. Search Orders");
            // if (authService.isCurrentUserAdmin()) {
            //     System.out.println("6. Admin Queue Management");
            // }
            System.out.println("7. Refresh Queue");
            System.out.println("0. Back to Main Menu");

            int choice = InputValidator.getIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    processNextPendingOrder(allOrders);
                    break;
                case 2:
                    viewOrdersByStatus();
                    break;
                case 3:
                    updateOrderStatus();
                    break;
                case 4:
                    viewOrderDetailsFromQueue();
                    break;
                case 5:
                    searchOrdersInQueue();
                    break;
                // case 6:
                //     if (authService.isCurrentUserAdmin()) {
                //         adminQueueManagement();
                //     }
                //     break;
                case 7:
                    // Refresh - just continue the loop
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    /**
     * Show comprehensive queue status with orders table
     */
    private void showComprehensiveQueueStatus(List<Order> allOrders) {
        // Calculate statistics
        long pendingCount = allOrders.stream().filter(o -> o.getStatus().toString().equals("PENDING")).count();
        long processingCount = allOrders.stream().filter(o -> o.getStatus().toString().equals("PROCESSING")).count();
        long shippedCount = allOrders.stream().filter(o -> o.getStatus().toString().equals("SHIPPED")).count();
        long deliveredCount = allOrders.stream().filter(o -> o.getStatus().toString().equals("DELIVERED")).count();
        long cancelledCount = allOrders.stream().filter(o -> o.getStatus().toString().equals("CANCELLED")).count();

        System.out.println("=== QUEUE STATUS OVERVIEW ===");
        System.out.println("Total Orders: " + allOrders.size());
        System.out.println("Pending: " + pendingCount + " | Processing: " + processingCount +
                " | Shipped: " + shippedCount + " | Delivered: " + deliveredCount +
                " | Cancelled: " + cancelledCount);

        // Show all orders in a table
        System.out.println("\n=== ALL ORDERS IN QUEUE ===");
        if (allOrders.isEmpty()) {
            System.out.println("No orders in the system.");
            return;
        }

        System.out.printf("%-8s %-12s %-15s %-12s %-15s %-20s%n",
                "Order ID", "Customer ID", "Status", "Total", "Date", "Items");
        System.out.println("=".repeat(90));

        // Sort orders by status priority (PENDING first, then by date)
        allOrders.sort((o1, o2) -> {
            String status1 = o1.getStatus().toString();
            String status2 = o2.getStatus().toString();

            // Priority order: PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
            int priority1 = getStatusPriority(status1);
            int priority2 = getStatusPriority(status2);

            if (priority1 != priority2) {
                return Integer.compare(priority1, priority2);
            }

            // If same status, sort by date (newest first)
            return o2.getOrderDate().compareTo(o1.getOrderDate());
        });

        for (Order order : allOrders) {
            int itemCount = order.getOrderItems() != null ? order.getOrderItems().size() : 0;
            String dateStr = order.getOrderDate() != null ? order.getOrderDate().toString().substring(0, 10) : "N/A";

            System.out.printf("%-8d %-12d %-15s $%-11.2f %-15s %-20d%n",
                    order.getOrderId(),
                    order.getCustomerId(),
                    order.getStatus(),
                    order.getTotalAmount(),
                    dateStr,
                    itemCount);
        }
    }

    /**
     * Get status priority for sorting (lower number = higher priority)
     */
    private int getStatusPriority(String status) {
        return switch (status) {
            case "PENDING" -> 1;
            case "PROCESSING" -> 2;
            case "SHIPPED" -> 3;
            case "DELIVERED" -> 4;
            case "CANCELLED" -> 5;
            default -> 6;
        };
    }

    /**
     * Process next pending order
     */
    private void processNextPendingOrder(List<Order> allOrders) {
        List<Order> pendingOrders = allOrders.stream()
                .filter(o -> o.getStatus().toString().equals("PENDING"))
                .sorted((o1, o2) -> o1.getOrderDate().compareTo(o2.getOrderDate())) // Oldest first
                .toList();

        if (pendingOrders.isEmpty()) {
            System.out.println("No pending orders to process.");
            return;
        }

        Order nextOrder = pendingOrders.get(0);
        System.out.println("=== PROCESSING NEXT PENDING ORDER #" + nextOrder.getOrderId() + " ===");

        // Show order details
        DisplayFormatter.displayOrderSummary(nextOrder);

        System.out.println("\nWhat would you like to do with this order?");
        System.out.println("1. Mark as Processing");
        System.out.println("2. Mark as Shipped");
        System.out.println("3. Cancel Order");
        System.out.println("0. Back to Queue Menu");

        int choice = InputValidator.getIntInput("Enter your choice: ");

        switch (choice) {
            case 1:
                updateOrderStatusInDatabase(nextOrder.getOrderId(), "PROCESSING");
                System.out.println("Order marked as PROCESSING.");
                break;
            case 2:
                updateOrderStatusInDatabase(nextOrder.getOrderId(), "SHIPPED");
                System.out.println("Order marked as SHIPPED.");
                break;
            case 3:
                updateOrderStatusInDatabase(nextOrder.getOrderId(), "CANCELLED");
                System.out.println("Order cancelled.");
                break;
            case 0:
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }

    /**
     * View orders by status
     */
    private void viewOrdersByStatus() {
        while (true) {
            System.out.println("\n=== VIEW ORDERS BY STATUS ===");
            System.out.println("1. Pending Orders");
            System.out.println("2. Processing Orders");
            System.out.println("3. Shipped Orders");
            System.out.println("4. Delivered Orders");
            System.out.println("5. Cancelled Orders");
            System.out.println("0. Back to Queue Menu");

            int choice = InputValidator.getIntInput("Enter your choice: ");
            String status = switch (choice) {
                case 1 -> "PENDING";
                case 2 -> "PROCESSING";
                case 3 -> "SHIPPED";
                case 4 -> "DELIVERED";
                case 5 -> "CANCELLED";
                case 0 -> null;
                default -> null;
            };

            if (status == null) {
                if (choice != 0) {
                    System.out.println("Invalid choice.");
                    continue;
                }
                return;
            }

            List<Order> allOrders = orderDAO.getAllOrders();
            List<Order> filteredOrders = allOrders.stream()
                    .filter(o -> o.getStatus().toString().equals(status))
                    .sorted((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate())) // Newest first
                    .toList();

            System.out.println("\n=== " + status + " ORDERS ===");
            if (filteredOrders.isEmpty()) {
                System.out.println("No " + status.toLowerCase() + " orders found.");
                continue;
            }

            System.out.printf("%-8s %-12s %-12s %-15s %-20s%n",
                    "Order ID", "Customer ID", "Total", "Date", "Items");
            System.out.println("=".repeat(75));

            for (Order order : filteredOrders) {
                int itemCount = order.getOrderItems() != null ? order.getOrderItems().size() : 0;
                String dateStr = order.getOrderDate() != null ? order.getOrderDate().toString().substring(0, 10)
                        : "N/A";

                System.out.printf("%-8d %-12d $%-11.2f %-15s %-20d%n",
                        order.getOrderId(),
                        order.getCustomerId(),
                        order.getTotalAmount(),
                        dateStr,
                        itemCount);
            }
        }
    }

    /**
     * Update order status
     */
    private void updateOrderStatus() {
        int orderId = InputValidator.getIntInput("Enter Order ID to update: ");
        Order order = orderDAO.getOrderById(orderId);

        if (order == null) {
            System.out.println("Order not found with ID: " + orderId);
            return;
        }

        System.out.println("Current order status: " + order.getStatus());
        System.out.println("\nSelect new status:");
        System.out.println("1. PENDING");
        System.out.println("2. PROCESSING");
        System.out.println("3. SHIPPED");
        System.out.println("4. DELIVERED");
        System.out.println("5. CANCELLED");

        int choice = InputValidator.getIntInput("Enter choice (1-5): ");
        String newStatus = switch (choice) {
            case 1 -> "PENDING";
            case 2 -> "PROCESSING";
            case 3 -> "SHIPPED";
            case 4 -> "DELIVERED";
            case 5 -> "CANCELLED";
            default -> null;
        };

        if (newStatus != null) {
            if (updateOrderStatusInDatabase(orderId, newStatus)) {
                System.out.println("Order status updated to: " + newStatus);
            } else {
                System.out.println("Failed to update order status.");
            }
        } else {
            System.out.println("Invalid choice.");
        }
    }

    /**
     * View order details from queue
     */
    private void viewOrderDetailsFromQueue() {
        int orderId = InputValidator.getIntInput("Enter Order ID to view: ");
        Order order = orderDAO.getOrderById(orderId);

        if (order == null) {
            System.out.println("Order not found with ID: " + orderId);
            return;
        }

        DisplayFormatter.displayOrderDetails(order);
    }

    /**
     * Update order status in database
     */
    private boolean updateOrderStatusInDatabase(int orderId, String newStatus) {
        try {
            System.out.println("Updating order " + orderId + " status to " + newStatus + "...");

            // Use OrderDAO to update the order status
            boolean success = orderDAO.updateOrderStatus(orderId, newStatus);

            if (success) {
                System.out.println("Order status updated successfully in database.");
            } else {
                System.out.println("Failed to update order status in database.");
            }

            return success;
        } catch (Exception e) {
            System.err.println("Error updating order status: " + e.getMessage());
            return false;
        }
    }

    /**
     * Search orders in queue
     */
    private void searchOrdersInQueue() {
        while (true) {
            System.out.println("\n=== SEARCH ORDERS ===");
            System.out.println("1. Search by Order ID");
            System.out.println("2. Search by Customer ID");
            System.out.println("3. Search by Date Range");
            System.out.println("0. Back to Queue Menu");

            int choice = InputValidator.getIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    searchByOrderId();
                    break;
                case 2:
                    searchByCustomerId();
                    break;
                case 3:
                    searchByDateRange();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    /**
     * Search orders by order ID
     */
    private void searchByOrderId() {
        int orderId = InputValidator.getIntInput("Enter Order ID: ");
        Order order = orderDAO.getOrderById(orderId);

        if (order == null) {
            System.out.println("Order not found with ID: " + orderId);
            return;
        }

        System.out.println("\n=== ORDER FOUND ===");
        DisplayFormatter.displayOrderDetails(order);
    }

    /**
     * Search orders by customer ID
     */
    private void searchByCustomerId() {
        int customerId = InputValidator.getIntInput("Enter Customer ID: ");
        List<Order> customerOrders = orderDAO.getOrdersByCustomerId(customerId);

        if (customerOrders.isEmpty()) {
            System.out.println("No orders found for Customer ID: " + customerId);
            return;
        }

        System.out.println("\n=== ORDERS FOR CUSTOMER " + customerId + " ===");
        System.out.printf("%-8s %-15s %-12s %-15s%n",
                "Order ID", "Status", "Total", "Date");
        System.out.println("=".repeat(60));

        for (Order order : customerOrders) {
            String dateStr = order.getOrderDate() != null ? order.getOrderDate().toString().substring(0, 10) : "N/A";

            System.out.printf("%-8d %-15s $%-11.2f %-15s%n",
                    order.getOrderId(),
                    order.getStatus(),
                    order.getTotalAmount(),
                    dateStr);
        }
    }

    /**
     * Search orders by date range
     */
    private void searchByDateRange() {
        System.out.println("Enter date range (YYYY-MM-DD format):");
        String startDate = InputValidator.getTrimmedStringInput("Start date: ");
        String endDate = InputValidator.getTrimmedStringInput("End date: ");

        List<Order> allOrders = orderDAO.getAllOrders();
        List<Order> dateFilteredOrders = allOrders.stream()
                .filter(o -> {
                    if (o.getOrderDate() == null)
                        return false;
                    String orderDateStr = o.getOrderDate().toString().substring(0, 10);
                    return orderDateStr.compareTo(startDate) >= 0 && orderDateStr.compareTo(endDate) <= 0;
                })
                .sorted((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()))
                .toList();

        if (dateFilteredOrders.isEmpty()) {
            System.out.println("No orders found in date range: " + startDate + " to " + endDate);
            return;
        }

        System.out.println("\n=== ORDERS FROM " + startDate + " TO " + endDate + " ===");
        System.out.printf("%-8s %-12s %-15s %-12s %-15s%n",
                "Order ID", "Customer ID", "Status", "Total", "Date");
        System.out.println("=".repeat(75));

        for (Order order : dateFilteredOrders) {
            String dateStr = order.getOrderDate() != null ? order.getOrderDate().toString().substring(0, 10) : "N/A";

            System.out.printf("%-8d %-12d %-15s $%-11.2f %-15s%n",
                    order.getOrderId(),
                    order.getCustomerId(),
                    order.getStatus(),
                    order.getTotalAmount(),
                    dateStr);
        }
    }

    /**
     * Admin queue management
     */
    private void adminQueueManagement() {
        System.out.println("=== ADMIN QUEUE MANAGEMENT ===");

        System.out.println("1. View Pending Orders");
        System.out.println("2. View Completed Orders");
        System.out.println("3. Clear User Queue");
        System.out.println("4. Clear All Queues");
        System.out.println("5. Overall Queue Statistics");
        System.out.println("0. Back");

        int choice = InputValidator.getIntInput("Enter your choice: ");

        switch (choice) {
            case 1:
                viewPendingOrders();
                break;
            case 2:
                viewCompletedOrders();
                break;
            case 3:
                clearUserQueue();
                break;
            case 4:
                clearAllQueues();
                break;
            case 5:
                showDetailedQueueStatistics();
                break;
            case 0:
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }

    /**
     * View pending orders
     */
    private void viewPendingOrders() {
        System.out.println("=== PENDING ORDERS ===");
        User currentUser = authService.getCurrentUser();

        try {
            List<Order> pendingOrders = orderService.getPendingOrders(currentUser);

            if (pendingOrders.isEmpty()) {
                System.out.println("No pending orders.");
                return;
            }

            System.out.printf("%-8s %-12s %-15s %-12s %-15s%n",
                    "Order ID", "Customer ID", "Status", "Total", "Date");
            System.out.println("-".repeat(70));

            for (Order order : pendingOrders) {
                System.out.printf("%-8d %-12d %-15s $%-11.2f %-15s%n",
                        order.getOrderId(),
                        order.getCustomerId(),
                        order.getStatus(),
                        order.getTotalAmount(),
                        order.getOrderDate() != null ? order.getOrderDate().toString().substring(0, 10) : "N/A");
            }
        } catch (SecurityException e) {
            System.out.println("Access denied: " + e.getMessage());
        }
    }

    /**
     * View completed orders
     */
    private void viewCompletedOrders() {
        System.out.println("=== COMPLETED ORDERS ===");
        User currentUser = authService.getCurrentUser();

        try {
            List<Order> completedOrders = orderService.getCompletedOrders(currentUser);

            if (completedOrders.isEmpty()) {
                System.out.println("No completed orders.");
                return;
            }

            System.out.printf("%-8s %-12s %-15s %-12s %-15s%n",
                    "Order ID", "Customer ID", "Status", "Total", "Date");
            System.out.println("-".repeat(70));

            for (Order order : completedOrders) {
                System.out.printf("%-8d %-12d %-15s $%-11.2f %-15s%n",
                        order.getOrderId(),
                        order.getCustomerId(),
                        order.getStatus(),
                        order.getTotalAmount(),
                        order.getOrderDate() != null ? order.getOrderDate().toString().substring(0, 10) : "N/A");
            }
        } catch (SecurityException e) {
            System.out.println("Access denied: " + e.getMessage());
        }
    }

    /**
     * Clear user queue
     */
    private void clearUserQueue() {
        System.out.println("=== CLEAR USER QUEUE ===");

        int userId = InputValidator.getIntInput("Enter User ID to clear queue (0 to cancel): ");
        if (userId == 0) {
            return;
        }

        if (InputValidator.getConfirmation("Are you sure you want to clear queue for user " + userId + "? (y/N): ")) {
            if (orderService.clearUserQueue(userId, authService.getCurrentUser())) {
                System.out.println("User queue cleared successfully.");
            } else {
                System.out.println("Failed to clear user queue.");
            }
        }
    }

    /**
     * Clear all queues
     */
    private void clearAllQueues() {
        System.out.println("=== CLEAR ALL QUEUES ===");
        System.out.println("⚠️  WARNING: This will clear ALL order queues!");
        String confirm = InputValidator.getTrimmedStringInput("Are you absolutely sure? Type 'CLEAR ALL' to confirm: ");

        if ("CLEAR ALL".equals(confirm)) {
            if (orderService.clearAllQueues(authService.getCurrentUser())) {
                System.out.println("All queues cleared successfully.");
            } else {
                System.out.println("Failed to clear queues.");
            }
        } else {
            System.out.println("Operation cancelled.");
        }
    }

    /**
     * Show detailed queue statistics
     */
    private void showDetailedQueueStatistics() {
        System.out.println("=== DETAILED QUEUE STATISTICS ===");
        User currentUser = authService.getCurrentUser();

        try {
            var stats = orderService.getOverallQueueStats(currentUser);

            System.out.println("Overall Queue Statistics:");
            System.out.println("  Admin Queue Size: " + stats.getAdminQueueSize());
            System.out.println("  Pending Queue Size: " + stats.getPendingQueueSize());
            System.out.println("  Completed Queue Size: " + stats.getCompletedQueueSize());
            System.out.println("  Active User Queues: " + stats.getActiveUserQueues());
            System.out.println("  Total User Queue Items: " + stats.getTotalUserQueueItems());

            var userStats = orderService.getUserQueueStats(currentUser);
            System.out.println("\nYour Queue Statistics:");
            System.out.println("  Total Orders: " + userStats.getTotalOrders());
            System.out.println("  Pending Orders: " + userStats.getPendingOrders());
            System.out.println("  Processing Orders: " + userStats.getProcessingOrders());

        } catch (SecurityException e) {
            System.out.println("Access denied: " + e.getMessage());
        }
    }

    /**
     * Demonstrate algorithms
     */
    public void demonstrateAlgorithms() {
        System.out.println("=== ALGORITHM DEMONSTRATION ===");
        System.out.println("1. Sorting Algorithms");
        System.out.println("2. Search Algorithms");
        System.out.println("0. Back to Main Menu");

        int choice = InputValidator.getIntInput("Enter your choice: ");

        switch (choice) {
            case 1:
                demonstrateSorting();
                break;
            case 2:
                demonstrateSearching();
                break;
            case 0:
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }

    /**
     * Demonstrate sorting algorithms
     */
    private void demonstrateSorting() {
        System.out.println("=== SORTING DEMONSTRATION ===");
        List<Book> books = bookService.getAllBooks();

        if (books.isEmpty()) {
            System.out.println("No books available for sorting demonstration.");
            return;
        }

        System.out.println("Original order:");
        DisplayFormatter.displayBookList(books);

        System.out.println("\nSorting by title (Quick Sort):");
        com.bookstore.util.algorithms.SortingAlgorithms.quickSortBooks(books,
                com.bookstore.util.algorithms.SortingAlgorithms.BOOK_TITLE_COMPARATOR_BOOK);
        DisplayFormatter.displayBookList(books);

        System.out.println("\nSorting by price (Quick Sort):");
        com.bookstore.util.algorithms.SortingAlgorithms.quickSortBooks(books,
                com.bookstore.util.algorithms.SortingAlgorithms.BOOK_PRICE_COMPARATOR);
        DisplayFormatter.displayBookList(books);
    }

    /**
     * Demonstrate search algorithms
     */
    private void demonstrateSearching() {
        System.out.println("=== SEARCH DEMONSTRATION ===");
        int orderId = InputValidator.getIntInput("Enter Order ID to search for: ");

        System.out.println("Searching using binary search algorithm...");
        Order order = orderService.findOrderById(orderId);

        if (order != null) {
            System.out.println("Order found using binary search:");
            System.out.println(order);
        } else {
            System.out.println("Order not found with ID: " + orderId);
        }
    }

    /**
     * Advanced search orders using OrderService
     */
    public void advancedSearchOrders() {
        System.out.println("=== ADVANCED ORDER SEARCH ===");
        String searchTerm = InputValidator.getTrimmedStringInput("Enter search term (order ID, customer ID, or tracking number): ");

        if (searchTerm.isEmpty()) {
            System.out.println("Search term cannot be empty.");
            return;
        }

        try {
            List<Order> searchResults = orderService.search(searchTerm);

            if (searchResults.isEmpty()) {
                System.out.println("No orders found matching: " + searchTerm);
            } else {
                System.out.println("\n=== SEARCH RESULTS ===");
                System.out.println("Found " + searchResults.size() + " order(s) matching: " + searchTerm);
                DisplayFormatter.displayOrdersTable(searchResults);
            }
        } catch (Exception e) {
            System.out.println("Error performing search: " + e.getMessage());
        }

        System.out.println("\nPress Enter to continue...");
        InputValidator.getStringInput("");
    }

    /**
     * Sort orders using OrderService
     */
    public void sortOrders() {
        System.out.println("=== SORT ORDERS ===");
        System.out.println("Sort by:");
        System.out.println("1. Order ID");
        System.out.println("2. Order Date");
        System.out.println("3. Total Amount");
        System.out.println("4. Customer ID");
        System.out.println("0. Cancel");

        int choice = InputValidator.getIntInput("Enter your choice: ");

        String field;
        switch (choice) {
            case 1:
                field = "order_id";
                break;
            case 2:
                field = "order_date";
                break;
            case 3:
                field = "total_amount";
                break;
            case 4:
                field = "customer_id";
                break;
            case 0:
                return;
            default:
                System.out.println("Invalid choice.");
                return;
        }

        boolean ascending = InputValidator.getConfirmation("Sort in ascending order? (y/n): ");

        try {
            List<Order> sortedOrders = orderService.sort(field, ascending);

            if (sortedOrders.isEmpty()) {
                System.out.println("No orders available to sort.");
            } else {
                System.out.println("\n=== SORTED ORDERS ===");
                System.out.println("Sorted by " + field + " (" + (ascending ? "ascending" : "descending") + "):");
                DisplayFormatter.displayOrdersTable(sortedOrders);
            }
        } catch (Exception e) {
            System.out.println("Error sorting orders: " + e.getMessage());
        }

        System.out.println("\nPress Enter to continue...");
        InputValidator.getStringInput("");
    }

    /**
     * Filter orders by status using OrderService
     */
    public void filterOrdersByStatus() {
        System.out.println("=== FILTER ORDERS BY STATUS ===");
        System.out.println("Filter by status:");
        System.out.println("1. PENDING");
        System.out.println("2. PROCESSING");
        System.out.println("3. SHIPPED");
        System.out.println("4. DELIVERED");
        System.out.println("5. CANCELLED");
        System.out.println("0. Cancel");

        int choice = InputValidator.getIntInput("Enter your choice: ");

        OrderStatus status;
        switch (choice) {
            case 1:
                status = OrderStatus.PENDING;
                break;
            case 2:
                status = OrderStatus.PROCESSING;
                break;
            case 3:
                status = OrderStatus.SHIPPED;
                break;
            case 4:
                status = OrderStatus.DELIVERED;
                break;
            case 5:
                status = OrderStatus.CANCELLED;
                break;
            case 0:
                return;
            default:
                System.out.println("Invalid choice.");
                return;
        }

        boolean ascending = InputValidator.getConfirmation("Sort results in ascending order by Order ID? (y/n): ");

        try {
            List<Order> filteredOrders = orderService.filter(status, ascending);

            if (filteredOrders.isEmpty()) {
                System.out.println("No orders found with status: " + status);
            } else {
                System.out.println("\n=== FILTERED ORDERS ===");
                System.out.println("Orders with status '" + status + "':");
                DisplayFormatter.displayOrdersTable(filteredOrders);
            }
        } catch (Exception e) {
            System.out.println("Error filtering orders: " + e.getMessage());
        }

        System.out.println("\nPress Enter to continue...");
        InputValidator.getStringInput("");
    }
}
