package com.bookstore.main;

import com.bookstore.dao.BookDAO;
import com.bookstore.dao.CustomerDAO;
import com.bookstore.dao.OrderDAO;
import com.bookstore.dao.UserDAO;
import com.bookstore.model.Book;
import com.bookstore.model.Customer;
import com.bookstore.model.Order;
import com.bookstore.model.OrderItem;
import com.bookstore.model.User;
import com.bookstore.model.Customer;
import com.bookstore.model.Role;
import com.bookstore.service.OrderService;
import com.bookstore.service.AuthService;
import com.bookstore.util.algorithms.SortingAlgorithms;
import com.bookstore.util.DatabaseInitializer;
import com.bookstore.util.DatabaseTestUtil;
import com.bookstore.util.PasswordUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Console-based Bookstore Management System
 * Demonstrates core functionality including:
 * - Book management (CRUD operations)
 * - Customer management
 * - Order processing with queue
 * - Search and sorting algorithms
 */
public class Main {
    private static BookDAO bookDAO = new BookDAO();
    private static CustomerDAO customerDAO = new CustomerDAO();
    private static OrderDAO orderDAO = new OrderDAO();
    private static UserDAO userDAO = new UserDAO();
    private static OrderService orderService = new OrderService();
    private static AuthService authService = new AuthService();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== Welcome to Bookstore Management System ===");
        System.out.println("Console Application - Java 21 with MySQL Database");
        System.out.println();

        // Initialize database and create default admin
        initializeSystem();

        // Authentication loop
        boolean running = true;
        while (running) {
            if (!authService.isLoggedIn()) {
                if (!loginMenu()) {
                    running = false;
                    continue;
                }
            }

            // Direct to appropriate menu based on role
            User currentUser = authService.getCurrentUser();
            if (currentUser.getRole() == Role.ADMIN) {
                if (!adminMainMenu()) {
                    running = false;
                }
            } else {
                if (!customerMainMenu()) {
                    running = false;
                }
            }
            System.out.println();
        }

        scanner.close();
    }


    private static void bookManagementMenu() {
        System.out.println("=== BOOK MANAGEMENT ===");
        System.out.println("1. Add New Book");
        System.out.println("2. View All Books");
        System.out.println("3. Search Book by ID");
        System.out.println("4. Search Book by ISBN");
        System.out.println("5. Update Book");
        System.out.println("6. Delete Book");
        System.out.println("7. Update Stock");
        System.out.println("0. Back to Main Menu");

        int choice = getIntInput("Enter your choice: ");

        switch (choice) {
            case 1:
                addNewBook();
                break;
            case 2:
                viewAllBooks();
                break;
            case 3:
                searchBookById();
                break;
            case 4:
                searchBookByIsbn();
                break;
            case 5:
                updateBook();
                break;
            case 6:
                deleteBook();
                break;
            case 7:
                updateBookStock();
                break;
            case 0:
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private static void customerManagementMenu() {
        System.out.println("=== CUSTOMER MANAGEMENT ===");
        System.out.println("1. Add New Customer");
        System.out.println("2. View All Customers");
        System.out.println("3. Search Customer by ID");
        System.out.println("0. Back to Main Menu");

        int choice = getIntInput("Enter your choice: ");

        switch (choice) {
            case 1:
                addNewCustomer();
                break;
            case 2:
                viewAllCustomers();
                break;
            case 3:
                searchCustomerById();
                break;
            case 0:
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private static void orderManagementMenu() {
        System.out.println("=== ORDER MANAGEMENT ===");
        System.out.println("1. Create New Order");
        System.out.println("2. View All Orders");
        System.out.println("3. Search Order by ID");
        System.out.println("4. View Order Queue Status");
        System.out.println("0. Back to Main Menu");

        int choice = getIntInput("Enter your choice: ");

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
                viewOrderQueueStatus();
                break;
            case 0:
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private static void addNewBook() {
        System.out.println("=== ADD NEW BOOK ===");
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Author: ");
        String author = scanner.nextLine();
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine();
        double price = getDoubleInput("Price: $");
        int stock = getIntInput("Stock Quantity: ");

        Book book = new Book(0, title, author, isbn, price, stock);
        int bookId = bookDAO.addBook(book);

        if (bookId != -1) {
            System.out.println("Book added successfully with ID: " + bookId);
        } else {
            System.out.println("Failed to add book.");
        }
    }

    private static void viewAllBooks() {
        System.out.println("=== ALL BOOKS ===");
        List<Book> books = bookDAO.getAllBooks();

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
                    truncate(book.getTitle(), 30),
                    truncate(book.getAuthor(), 20),
                    book.getIsbn(),
                    book.getPrice(),
                    book.getStockQuantity());
        }
    }

    private static void searchBookById() {
        int bookId = getIntInput("Enter Book ID: ");
        Book book = bookDAO.getBookById(bookId);

        if (book != null) {
            System.out.println("Book found:");
            System.out.println(book);
        } else {
            System.out.println("Book not found with ID: " + bookId);
        }
    }

    private static void searchBookByIsbn() {
        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine();
        Book book = bookDAO.getBookByIsbn(isbn);

        if (book != null) {
            System.out.println("Book found:");
            System.out.println(book);
        } else {
            System.out.println("Book not found with ISBN: " + isbn);
        }
    }

    private static void updateBook() {
        int bookId = getIntInput("Enter Book ID to update: ");
        Book book = bookDAO.getBookById(bookId);

        if (book == null) {
            System.out.println("Book not found with ID: " + bookId);
            return;
        }

        System.out.println("Current book details:");
        System.out.println(book);
        System.out.println();

        System.out.print("New Title (current: " + book.getTitle() + "): ");
        String title = scanner.nextLine();
        if (!title.trim().isEmpty()) {
            book.setTitle(title);
        }

        System.out.print("New Author (current: " + book.getAuthor() + "): ");
        String author = scanner.nextLine();
        if (!author.trim().isEmpty()) {
            book.setAuthor(author);
        }

        System.out.print("New ISBN (current: " + book.getIsbn() + "): ");
        String isbn = scanner.nextLine();
        if (!isbn.trim().isEmpty()) {
            book.setIsbn(isbn);
        }

        System.out.print("New Price (current: $" + book.getPrice() + "): ");
        String priceStr = scanner.nextLine();
        if (!priceStr.trim().isEmpty()) {
            try {
                book.setPrice(Double.parseDouble(priceStr));
            } catch (NumberFormatException e) {
                System.out.println("Invalid price format. Keeping current price.");
            }
        }

        System.out.print("New Stock (current: " + book.getStockQuantity() + "): ");
        String stockStr = scanner.nextLine();
        if (!stockStr.trim().isEmpty()) {
            try {
                book.setStockQuantity(Integer.parseInt(stockStr));
            } catch (NumberFormatException e) {
                System.out.println("Invalid stock format. Keeping current stock.");
            }
        }

        if (bookDAO.updateBook(book)) {
            System.out.println("Book updated successfully.");
        } else {
            System.out.println("Failed to update book.");
        }
    }

    private static void deleteBook() {
        int bookId = getIntInput("Enter Book ID to delete: ");
        Book book = bookDAO.getBookById(bookId);

        if (book == null) {
            System.out.println("Book not found with ID: " + bookId);
            return;
        }

        System.out.println("Book to delete:");
        System.out.println(book);
        System.out.print("Are you sure you want to delete this book? (y/N): ");
        String confirm = scanner.nextLine();

        if (confirm.toLowerCase().startsWith("y")) {
            if (bookDAO.deleteBook(bookId)) {
                System.out.println("Book deleted successfully.");
            } else {
                System.out.println("Failed to delete book. It may have associated orders.");
            }
        } else {
            System.out.println("Delete operation cancelled.");
        }
    }

    private static void updateBookStock() {
        int bookId = getIntInput("Enter Book ID: ");
        int quantityChange = getIntInput("Enter quantity change (positive to add, negative to reduce): ");

        if (bookDAO.updateBookStock(bookId, quantityChange)) {
            System.out.println("Stock updated successfully.");
        } else {
            System.out.println("Failed to update stock.");
        }
    }

    private static void addNewCustomer() {
        System.out.println("=== ADD NEW CUSTOMER ===");
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Address: ");
        String address = scanner.nextLine();

        Customer customer = new Customer(0, name, email, address);
        int customerId = customerDAO.addCustomer(customer);

        if (customerId != -1) {
            System.out.println("Customer added successfully with ID: " + customerId);
        } else {
            System.out.println("Failed to add customer.");
        }
    }

    private static void viewAllCustomers() {
        System.out.println("=== ALL CUSTOMERS ===");
        List<Customer> customers = customerDAO.getAllCustomers();

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
                    truncate(customer.getName(), 25),
                    truncate(customer.getEmail(), 30),
                    truncate(customer.getAddress(), 30));
        }
    }

    private static void searchCustomerById() {
        int customerId = getIntInput("Enter Customer ID: ");
        Customer customer = customerDAO.getCustomerById(customerId);

        if (customer != null) {
            System.out.println("Customer found:");
            System.out.println(customer);
        } else {
            System.out.println("Customer not found with ID: " + customerId);
        }
    }

    private static void createNewOrder() {
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

        int customerId = getIntInput("Enter Customer ID: ");
        Customer customer = customerDAO.getCustomerById(customerId);
        if (customer == null) {
            System.out.println("Invalid customer ID.");
            return;
        }

        // Show available books
        List<Book> books = bookDAO.getAllBooks();
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
            int bookId = getIntInput("Enter Book ID to add to order (0 to finish): ");
            if (bookId == 0) {
                addingItems = false;
                continue;
            }

            Book book = bookDAO.getBookById(bookId);
            if (book == null) {
                System.out.println("Invalid book ID.");
                continue;
            }

            int quantity = getIntInput("Enter quantity: ");
            if (quantity <= 0) {
                System.out.println("Invalid quantity.");
                continue;
            }

            OrderItem item = new OrderItem(0, 0, bookId, quantity, book.getPrice());
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

    private static void viewAllOrders() {
        System.out.println("=== ALL ORDERS ===");
        List<Order> orders = orderDAO.getAllOrders();

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

    private static void searchOrderById() {
        int orderId = getIntInput("Enter Order ID: ");
        Order order = orderService.findOrderById(orderId);

        if (order != null) {
            System.out.println("Order found:");
            System.out.println(order);

            // Show order items
            List<OrderItem> items = orderDAO.getOrderItemsByOrderId(orderId);
            if (!items.isEmpty()) {
                System.out.println("Order Items:");
                for (OrderItem item : items) {
                    Book book = bookDAO.getBookById(item.getBookId());
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

    private static void viewOrderQueueStatus() {
        System.out.println("=== ORDER QUEUE STATUS ===");
        // Note: This would need to be implemented in OrderProcessingQueue
        System.out.println("Queue functionality available through order processing.");
    }

    private static void processOrderQueue() {
        System.out.println("=== PROCESS ORDER QUEUE ===");
        User currentUser = authService.getCurrentUser();

        if (!orderService.hasOrdersInQueue(currentUser)) {
            System.out.println("No orders in your queue to process.");
            return;
        }

        // Show queue status first
        showQueueStatus();

        System.out.println("\n1. Process Next Order");
        System.out.println("2. View Queue Orders");
        System.out.println("3. Complete Order");
        System.out.println("4. Cancel Order");
        if (authService.isCurrentUserAdmin()) {
            System.out.println("5. Admin Queue Management");
        }
        System.out.println("0. Back to Main Menu");

        int choice = getIntInput("Enter your choice: ");

        switch (choice) {
            case 1:
                processNextOrder();
                break;
            case 2:
                viewQueueOrders();
                break;
            case 3:
                completeOrderFromQueue();
                break;
            case 4:
                cancelOrderFromQueue();
                break;
            case 5:
                if (authService.isCurrentUserAdmin()) {
                    adminQueueManagement();
                }
                break;
            case 0:
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private static void demonstrateAlgorithms() {
        System.out.println("=== ALGORITHM DEMONSTRATION ===");
        System.out.println("1. Sorting Algorithms");
        System.out.println("2. Search Algorithms");
        System.out.println("0. Back to Main Menu");

        int choice = getIntInput("Enter your choice: ");

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

    private static void demonstrateSorting() {
        System.out.println("=== SORTING DEMONSTRATION ===");
        List<Book> books = bookDAO.getAllBooks();

        if (books.isEmpty()) {
            System.out.println("No books available for sorting demonstration.");
            return;
        }

        System.out.println("Original order:");
        displayBookList(books);

        System.out.println("\nSorting by title (Quick Sort):");
        SortingAlgorithms.quickSortBooks(books, SortingAlgorithms.BOOK_TITLE_COMPARATOR_BOOK);
        displayBookList(books);

        System.out.println("\nSorting by price (Quick Sort):");
        SortingAlgorithms.quickSortBooks(books, SortingAlgorithms.BOOK_PRICE_COMPARATOR);
        displayBookList(books);
    }

    private static void demonstrateSearching() {
        System.out.println("=== SEARCH DEMONSTRATION ===");
        int orderId = getIntInput("Enter Order ID to search for: ");

        System.out.println("Searching using binary search algorithm...");
        Order order = orderService.findOrderById(orderId);

        if (order != null) {
            System.out.println("Order found using binary search:");
            System.out.println(order);
        } else {
            System.out.println("Order not found with ID: " + orderId);
        }
    }

    private static void displayBookList(List<Book> books) {
        for (Book book : books) {
            System.out.printf("%-30s by %-20s - $%.2f%n",
                    truncate(book.getTitle(), 30),
                    truncate(book.getAuthor(), 20),
                    book.getPrice());
        }
    }

    private static void initializeSystem() {
        System.out.println("Initializing system...");

        // Test database connection first
        if (!DatabaseTestUtil.testDatabaseConnection()) {
            System.err.println("\n❌ Database connection failed!");
            System.err.println("Please ensure:");
            System.err.println("1. XAMPP is running");
            System.err.println("2. MySQL service is started");
            // System.err.println("3. Database 'online_bookstore_db' exists");
            // System.err.println("\nYou can use the database setup scripts in the
            // 'database' folder.");
            System.exit(1);
        }

        // Initialize database tables
        DatabaseInitializer.initializeDatabase();

        // Create default admin user if needed
        authService.createDefaultAdminIfNeeded();

        // Initialize order queues with existing orders
        if (authService.isLoggedIn()) {
            orderService.initializeQueues(authService.getCurrentUser());
        }

        System.out.println("✓ System initialization completed successfully!");
        System.out.println();
    }

    private static boolean loginMenu() {
        System.out.println("=== WELCOME TO BOOKSTORE ===");
        System.out.println("1. Login");
        System.out.println("2. Sign Up (Customer)");
        System.out.println("0. Exit");

        int choice = getIntInput("Enter your choice: ");

        switch (choice) {
            case 1:
                return performLogin();
            case 2:
                return performSignup();
            case 0:
                return false;
            default:
                System.out.println("Invalid choice.");
                return loginMenu();
        }
    }

    private static boolean performLogin() {
        System.out.println("=== LOGIN ===");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        if (username.isEmpty()) {
            System.out.println("Username cannot be empty.");
            System.out.println("Please try again.\n");
            return loginMenu();
        }

        String password = getMaskedPassword("Password: ");

        if (authService.login(username, password)) {
            User user = authService.getCurrentUser();
            System.out.println("Login successful! Welcome, " + user.getFullName());

            // Check if this is a customer's first login and profile is incomplete
            if (user.isCustomer() && isFirstLoginOrIncompleteProfile((Customer) user)) {
                System.out.println("\n=== COMPLETE YOUR PROFILE ===");
                System.out.println("To provide you with the best service, please complete your profile information.");

                if (completeCustomerProfile((Customer) user)) {
                    System.out.println("Profile completed successfully!");
                } else {
                    System.out.println("Profile completion was skipped. You can update it later in Profile Settings.");
                }
                System.out.println();
            }

            return true;
        } else {
            System.out.println("Invalid username or password.");
            System.out.println("Please try again.\n");
            return loginMenu();
        }
    }

    private static boolean performSignup() {
        System.out.println("=== CUSTOMER SIGN UP ===");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        if (username.isEmpty()) {
            System.out.println("Username cannot be empty.");
            System.out.println("Please try again.\n");
            return loginMenu();
        }

        // Check if username already exists
        if (userDAO.getUserByUsername(username) != null) {
            System.out.println("Username already exists. Please choose a different username.");
            System.out.println("Please try again.\n");
            return loginMenu();
        }

        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        if (email.isEmpty()) {
            System.out.println("Email cannot be empty.");
            System.out.println("Please try again.\n");
            return loginMenu();
        }

        System.out.print("First Name: ");
        String firstName = scanner.nextLine().trim();

        if (firstName.isEmpty()) {
            System.out.println("First name cannot be empty.");
            System.out.println("Please try again.\n");
            return loginMenu();
        }

        System.out.print("Last Name: ");
        String lastName = scanner.nextLine().trim();

        if (lastName.isEmpty()) {
            System.out.println("Last name cannot be empty.");
            System.out.println("Please try again.\n");
            return loginMenu();
        }

        String password = getMaskedPassword("Password: ");
        String confirmPassword = getMaskedPassword("Confirm Password: ");

        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match.");
            System.out.println("Please try again.\n");
            return loginMenu();
        }

        // Create customer account
        String hashedPassword = com.bookstore.util.PasswordUtil.hashPassword(password);
        Customer newCustomer = new Customer(username, hashedPassword, email, firstName, lastName);

        int userId = userDAO.addUser(newCustomer);
        if (userId != -1) {
            System.out.println("Account created successfully! You can now login.");
            System.out.println();
            return loginMenu();
        } else {
            System.out.println("Failed to create account. Please try again.");
            System.out.println();
            return loginMenu();
        }
    }

    private static String getMaskedPassword(String prompt) {
        System.out.print(prompt);

        // Try to use console for password masking
        java.io.Console console = System.console();
        if (console != null) {
            char[] passwordChars = console.readPassword();
            return new String(passwordChars);
        } else {
            // Fallback to regular input if console is not available (IDE environments)
            return scanner.nextLine();
        }
    }

    private static boolean adminMainMenu() {
        System.out.println("=== ADMIN DASHBOARD ===");
        System.out.println("1. Book Management");
        System.out.println("2. Customer Management");
        System.out.println("3. Order Management");
        System.out.println("4. Account Management");
        System.out.println("5. Queue Processing");
        System.out.println("6. Algorithms Demo");
        System.out.println("7. Profile Settings");
        System.out.println("8. Logout");
        System.out.println("0. Exit");

        int choice = getIntInput("Enter your choice: ");

        switch (choice) {
            case 1:
                bookManagementMenu();
                break;
            case 2:
                customerManagementMenu();
                break;
            case 3:
                orderManagementMenu();
                break;
            case 4:
                accountManagementMenu();
                break;
            case 5:
                processOrderQueue();
                break;
            case 6:
                demonstrateAlgorithms();
                break;
            case 7:
                userProfileMenu();
                break;
            case 8:
                authService.logout();
                System.out.println("Logged out successfully.");
                return true; // Continue to login menu
            case 0:
                authService.logout();
                System.out.println("Thank you for using Bookstore Management System!");
                return false; // Exit application
            default:
                System.out.println("Invalid choice. Please try again.");
        }
        return true;
    }

    private static boolean customerMainMenu() {
        System.out.println("=== CUSTOMER DASHBOARD ===");
        System.out.println("1. Browse Books");
        System.out.println("2. Search Books");
        System.out.println("3. My Orders");
        System.out.println("4. Place New Order");
        System.out.println("5. Profile Settings");
        System.out.println("6. Logout");
        System.out.println("0. Exit");

        int choice = getIntInput("Enter your choice: ");

        switch (choice) {
            case 1:
                customerBrowseBooks();
                break;
            case 2:
                customerSearchBooks();
                break;
            case 3:
                customerViewOrders();
                break;
            case 4:
                customerPlaceOrder();
                break;
            case 5:
                userProfileMenu();
                break;
            case 6:
                authService.logout();
                System.out.println("Logged out successfully.");
                return true; // Continue to login menu
            case 0:
                authService.logout();
                System.out.println("Thank you for using Bookstore Management System!");
                return false; // Exit application
            default:
                System.out.println("Invalid choice. Please try again.");
        }
        return true;
    }

    private static void accountManagementMenu() {
        System.out.println("=== ACCOUNT MANAGEMENT ===");
        System.out.println("1. Create New Admin Account");
        System.out.println("2. Create New Customer Account");
        System.out.println("3. View All Users");
        System.out.println("0. Back to Main Menu");

        int choice = getIntInput("Enter your choice: ");

        switch (choice) {
            case 1:
                createAdminAccount();
                break;
            case 2:
                createCustomerAccount();
                break;
            case 3:
                viewAllUsers();
                break;
            case 0:
                return;
            default:
                System.out.println("Invalid choice. Please try again.");
                accountManagementMenu();
        }
    }

    private static void createAdminAccount() {
        System.out.println("=== CREATE ADMIN ACCOUNT ===");

        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        if (username.isEmpty()) {
            System.out.println("Username cannot be empty.");
            return;
        }

        // Check if username already exists
        if (userDAO.getUserByUsername(username) != null) {
            System.out.println("Username already exists. Please choose a different username.");
            return;
        }

        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        if (email.isEmpty()) {
            System.out.println("Email cannot be empty.");
            return;
        }

        System.out.print("First Name: ");
        String firstName = scanner.nextLine().trim();

        if (firstName.isEmpty()) {
            System.out.println("First name cannot be empty.");
            return;
        }

        System.out.print("Last Name: ");
        String lastName = scanner.nextLine().trim();

        if (lastName.isEmpty()) {
            System.out.println("Last name cannot be empty.");
            return;
        }

        String password = getMaskedPassword("Password: ");
        String confirmPassword = getMaskedPassword("Confirm Password: ");

        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match.");
            return;
        }

        if (authService.registerAdmin(username, password, email, firstName, lastName)) {
            System.out.println("Admin account created successfully!");
        } else {
            System.out.println("Failed to create admin account.");
        }
    }

    private static void createCustomerAccount() {
        System.out.println("=== CREATE CUSTOMER ACCOUNT ===");

        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        if (username.isEmpty()) {
            System.out.println("Username cannot be empty.");
            return;
        }

        // Check if username already exists
        if (userDAO.getUserByUsername(username) != null) {
            System.out.println("Username already exists. Please choose a different username.");
            return;
        }

        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        if (email.isEmpty()) {
            System.out.println("Email cannot be empty.");
            return;
        }

        System.out.print("First Name: ");
        String firstName = scanner.nextLine().trim();

        if (firstName.isEmpty()) {
            System.out.println("First name cannot be empty.");
            return;
        }

        System.out.print("Last Name: ");
        String lastName = scanner.nextLine().trim();

        if (lastName.isEmpty()) {
            System.out.println("Last name cannot be empty.");
            return;
        }

        String password = getMaskedPassword("Password: ");
        String confirmPassword = getMaskedPassword("Confirm Password: ");

        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match.");
            return;
        }

        if (authService.registerCustomer(username, password, email, firstName, lastName)) {
            System.out.println("Customer account created successfully!");
        } else {
            System.out.println("Failed to create customer account.");
        }
    }



    private static void customerBrowseBooks() {
        System.out.println("=== BROWSE BOOKS ===");
        try {
            List<Book> books = bookDAO.getAllBooks();
            if (books.isEmpty()) {
                System.out.println("No books available.");
                return;
            }

            System.out.printf("%-5s %-30s %-20s %-10s %-8s%n", "ID", "Title", "Author", "Price", "Stock");
            System.out.println("=".repeat(80));

            for (Book book : books) {
                System.out.printf("%-5d %-30s %-20s $%-9.2f %-8d%n",
                        book.getBookId(),
                        book.getTitle().length() > 30 ? book.getTitle().substring(0, 27) + "..." : book.getTitle(),
                        book.getAuthor().length() > 20 ? book.getAuthor().substring(0, 17) + "..." : book.getAuthor(),
                        book.getPrice(),
                        book.getStockQuantity());
            }
        } catch (Exception e) {
            System.out.println("Error retrieving books: " + e.getMessage());
        }
    }

    private static void customerSearchBooks() {
        System.out.println("=== SEARCH BOOKS ===");
        System.out.print("Enter search term (title or author): ");
        String searchTerm = scanner.nextLine().trim();

        if (searchTerm.isEmpty()) {
            System.out.println("Search term cannot be empty.");
            return;
        }

        try {
            List<Book> books = bookDAO.searchBooks(searchTerm);
            if (books.isEmpty()) {
                System.out.println("No books found matching: " + searchTerm);
                return;
            }

            System.out.printf("%-5s %-30s %-20s %-10s %-8s%n", "ID", "Title", "Author", "Price", "Stock");
            System.out.println("=".repeat(80));

            for (Book book : books) {
                System.out.printf("%-5d %-30s %-20s $%-9.2f %-8d%n",
                        book.getBookId(),
                        book.getTitle().length() > 30 ? book.getTitle().substring(0, 27) + "..." : book.getTitle(),
                        book.getAuthor().length() > 20 ? book.getAuthor().substring(0, 17) + "..." : book.getAuthor(),
                        book.getPrice(),
                        book.getStockQuantity());
            }
        } catch (Exception e) {
            System.out.println("Error searching books: " + e.getMessage());
        }
    }

    private static void customerViewOrders() {
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
                    order.getTotalAmount()
                );
            }

            System.out.println("\nWould you like to view details of a specific order? (y/n): ");
            String choice = scanner.nextLine().trim().toLowerCase();

            if (choice.equals("y") || choice.equals("yes")) {
                System.out.print("Enter Order ID: ");
                try {
                    int orderId = Integer.parseInt(scanner.nextLine().trim());

                    // Check if the order belongs to this user
                    boolean orderFound = false;
                    for (Order order : userOrders) {
                        if (order.getOrderId() == orderId) {
                            orderFound = true;
                            displayOrderDetails(order);
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

    private static void displayOrderDetails(Order order) {
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

    private static void customerPlaceOrder() {
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
            System.out.print("Would you like to complete your profile now? (y/n): ");
            String choice = scanner.nextLine().trim().toLowerCase();

            if (choice.equals("y") || choice.equals("yes")) {
                if (completeCustomerProfile((Customer) currentUser)) {
                    System.out.println("Profile completed! You can now place your order.\n");
                } else {
                    System.out.println("Profile completion failed. Please try again later.");
                    return;
                }
            } else {
                System.out.println("Profile completion is required to place orders.");
                return;
            }
        }

        // Show available books
        List<Book> books = bookDAO.getAllBooks();
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
            int bookId = getIntInput("Enter Book ID to add to cart (0 to finish): ");
            if (bookId == 0) {
                addingItems = false;
                continue;
            }

            Book book = bookDAO.getBookById(bookId);
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

            int quantity = getIntInput("Enter quantity: ");
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
                        System.out.println("Total quantity would exceed available stock (" + book.getStockQuantity() + ").");
                        continue;
                    }
                    existingItem.setQuantity(newQuantity);
                    bookExists = true;
                    System.out.println("Updated quantity for " + book.getTitle() + " to " + newQuantity);
                    break;
                }
            }

            if (!bookExists) {
                OrderItem item = new OrderItem(0, 0, bookId, quantity, book.getPrice());
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
        showOrderSummary(orderItems, totalAmount);

        // Confirm order
        System.out.print("Confirm order? (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (!confirm.equals("y") && !confirm.equals("yes")) {
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

    private static void showOrderSummary(List<OrderItem> orderItems, double totalAmount) {
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

    private static void userProfileMenu() {
        System.out.println("=== MY PROFILE ===");
        User currentUser = authService.getCurrentUser();
        System.out.println("Username: " + currentUser.getUsername());
        System.out.println("Name: " + currentUser.getFullName());
        System.out.println("Email: " + currentUser.getEmail());
        System.out.println("Role: " + currentUser.getRole());
        System.out
                .println("Last Login: " + (currentUser.getLastLogin() != null ? currentUser.getLastLogin() : "Never"));

        // Show customer-specific information
        if (currentUser.isCustomer()) {
            Customer customer = (Customer) currentUser;
            System.out.println("\n--- Customer Information ---");
            System.out.println("Address: " + (customer.getAddress() != null ? customer.getAddress() : "Not set"));
            System.out.println("City: " + (customer.getCity() != null ? customer.getCity() : "Not set"));
            System.out.println("State: " + (customer.getState() != null ? customer.getState() : "Not set"));
            System.out.println("Postal Code: " + (customer.getPostalCode() != null ? customer.getPostalCode() : "Not set"));
            System.out.println("Country: " + (customer.getCountry() != null ? customer.getCountry() : "Not set"));
            System.out.println("Phone: " + (customer.getPhoneNumber() != null ? customer.getPhoneNumber() : "Not set"));
            System.out.println("Preferred Payment: " + (customer.getPreferredPaymentMethod() != null ? customer.getPreferredPaymentMethod() : "Not set"));
            System.out.println("Email Notifications: " + (customer.isEmailNotifications() ? "Enabled" : "Disabled"));
        }

        System.out.println();

        System.out.println("1. Change Password");
        if (currentUser.isCustomer()) {
            System.out.println("2. Update Profile Information");
        }
        System.out.println("0. Back to Main Menu");

        int choice = getIntInput("Enter your choice: ");

        switch (choice) {
            case 1:
                changePassword();
                break;
            case 2:
                if (currentUser.isCustomer()) {
                    updateCustomerProfileMenu((Customer) currentUser);
                } else {
                    System.out.println("Invalid choice.");
                }
                break;
            case 0:
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private static void updateCustomerProfileMenu(Customer customer) {
        System.out.println("=== UPDATE PROFILE INFORMATION ===");
        System.out.println("Current Information:");
        System.out.println("Address: " + (customer.getAddress() != null ? customer.getAddress() : "Not set"));
        System.out.println("City: " + (customer.getCity() != null ? customer.getCity() : "Not set"));
        System.out.println("State: " + (customer.getState() != null ? customer.getState() : "Not set"));
        System.out.println("Postal Code: " + (customer.getPostalCode() != null ? customer.getPostalCode() : "Not set"));
        System.out.println("Country: " + (customer.getCountry() != null ? customer.getCountry() : "Not set"));
        System.out.println("Phone: " + (customer.getPhoneNumber() != null ? customer.getPhoneNumber() : "Not set"));
        System.out.println("Preferred Payment: " + (customer.getPreferredPaymentMethod() != null ? customer.getPreferredPaymentMethod() : "Not set"));
        System.out.println("Email Notifications: " + (customer.isEmailNotifications() ? "Enabled" : "Disabled"));
        System.out.println();

        System.out.println("What would you like to update?");
        System.out.println("1. Street Address");
        System.out.println("2. City/State/Postal Code");
        System.out.println("3. Phone Number");
        System.out.println("4. Preferred Payment Method");
        System.out.println("5. Email Notifications");
        System.out.println("6. Update All Information");
        System.out.println("0. Back to Profile Menu");

        int choice = getIntInput("Enter your choice: ");

        switch (choice) {
            case 1:
                updateAddress(customer);
                break;
            case 2:
                updateCityStatePostal(customer);
                break;
            case 3:
                updatePhoneNumber(customer);
                break;
            case 4:
                updatePaymentMethod(customer);
                break;
            case 5:
                updateEmailNotifications(customer);
                break;
            case 6:
                completeCustomerProfile(customer);
                break;
            case 0:
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private static void updateAddress(Customer customer) {
        System.out.print("Enter new street address (current: " +
                        (customer.getAddress() != null ? customer.getAddress() : "Not set") + "): ");
        String newAddress = scanner.nextLine().trim();
        if (!newAddress.isEmpty()) {
            customer.setAddress(newAddress);
            if (updateCustomerProfile(customer)) {
                System.out.println("Street address updated successfully!");
            }
        }
    }

    private static void updateCityStatePostal(Customer customer) {
        System.out.println("=== UPDATE CITY/STATE/POSTAL CODE ===");

        System.out.print("Enter new city (current: " +
                        (customer.getCity() != null ? customer.getCity() : "Not set") + "): ");
        String newCity = scanner.nextLine().trim();
        if (!newCity.isEmpty()) {
            customer.setCity(newCity);
        }

        System.out.print("Enter new state/province (current: " +
                        (customer.getState() != null ? customer.getState() : "Not set") + "): ");
        String newState = scanner.nextLine().trim();
        if (!newState.isEmpty()) {
            customer.setState(newState);
        }

        System.out.print("Enter new postal/ZIP code (current: " +
                        (customer.getPostalCode() != null ? customer.getPostalCode() : "Not set") + "): ");
        String newPostalCode = scanner.nextLine().trim();
        if (!newPostalCode.isEmpty()) {
            customer.setPostalCode(newPostalCode);
        }

        System.out.print("Enter new country (current: " +
                        (customer.getCountry() != null ? customer.getCountry() : "Not set") + "): ");
        String newCountry = scanner.nextLine().trim();
        if (!newCountry.isEmpty()) {
            customer.setCountry(newCountry);
        }

        if (updateCustomerProfile(customer)) {
            System.out.println("Location information updated successfully!");
        }
    }

    private static void updatePhoneNumber(Customer customer) {
        System.out.print("Enter new phone number (current: " +
                        (customer.getPhoneNumber() != null ? customer.getPhoneNumber() : "Not set") + "): ");
        String newPhone = scanner.nextLine().trim();
        if (!newPhone.isEmpty()) {
            customer.setPhoneNumber(newPhone);
            if (updateCustomerProfile(customer)) {
                System.out.println("Phone number updated successfully!");
            }
        }
    }

    private static void updatePaymentMethod(Customer customer) {
        System.out.println("Current payment method: " +
                          (customer.getPreferredPaymentMethod() != null ? customer.getPreferredPaymentMethod() : "Not set"));
        System.out.println("\nSelect new payment method:");
        System.out.println("1. Credit Card");
        System.out.println("2. Debit Card");
        System.out.println("3. PayPal");
        System.out.println("4. Bank Transfer");
        System.out.println("5. Cash on Delivery");

        int choice = getIntInput("Select payment method (1-5): ");
        String paymentMethod = switch (choice) {
            case 1 -> "Credit Card";
            case 2 -> "Debit Card";
            case 3 -> "PayPal";
            case 4 -> "Bank Transfer";
            case 5 -> "Cash on Delivery";
            default -> customer.getPreferredPaymentMethod(); // Keep current
        };

        if (paymentMethod != null && !paymentMethod.equals(customer.getPreferredPaymentMethod())) {
            customer.setPreferredPaymentMethod(paymentMethod);
            if (updateCustomerProfile(customer)) {
                System.out.println("Payment method updated successfully!");
            }
        }
    }

    private static void updateEmailNotifications(Customer customer) {
        System.out.println("Current email notifications: " + (customer.isEmailNotifications() ? "Enabled" : "Disabled"));
        System.out.print("Enable email notifications? (y/n): ");
        String choice = scanner.nextLine().trim().toLowerCase();
        boolean newSetting = choice.equals("y") || choice.equals("yes");

        if (newSetting != customer.isEmailNotifications()) {
            customer.setEmailNotifications(newSetting);
            if (updateCustomerProfile(customer)) {
                System.out.println("Email notification preference updated successfully!");
            }
        }
    }

    private static int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static double getDoubleInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static void viewAllUsers() {
        System.out.println("=== ALL USERS ===");
        try {
            List<User> users = authService.getUserDAO().getAllUsers();

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
                        truncate(user.getUsername(), 15),
                        truncate(user.getFullName(), 25),
                        truncate(user.getEmail(), 20),
                        user.getRole(),
                        user.isActive() ? "Yes" : "No",
                        user.getLastLogin() != null ? user.getLastLogin().toString().substring(0, 19) : "Never");
            }
        } catch (SecurityException e) {
            System.out.println("Access denied: " + e.getMessage());
        }
    }



    private static void changePassword() {
        System.out.println("=== CHANGE PASSWORD ===");

        System.out.print("Current Password: ");
        String currentPassword = scanner.nextLine();

        System.out.print("New Password: ");
        String newPassword = scanner.nextLine();

        if (!PasswordUtil.isPasswordStrong(newPassword)) {
            System.out.println("Password is not strong enough.");
            System.out.println(PasswordUtil.getPasswordRequirements());
            return;
        }

        System.out.print("Confirm New Password: ");
        String confirmPassword = scanner.nextLine();

        if (!newPassword.equals(confirmPassword)) {
            System.out.println("Passwords do not match.");
            return;
        }

        if (authService.changePassword(currentPassword, newPassword)) {
            System.out.println("Password changed successfully.");
        } else {
            System.out.println("Failed to change password. Please check your current password.");
        }
    }

    private static void showQueueStatus() {
        User currentUser = authService.getCurrentUser();

        try {
            if (currentUser.isAdmin()) {
                // Admin sees overall statistics
                var overallStats = orderService.getOverallQueueStats(currentUser);
                System.out.println("=== ADMIN QUEUE OVERVIEW ===");
                System.out.println("Admin Queue: " + overallStats.getAdminQueueSize() + " orders");
                System.out.println("Pending Queue: " + overallStats.getPendingQueueSize() + " orders");
                System.out.println("Completed Queue: " + overallStats.getCompletedQueueSize() + " orders");
                System.out.println("Active User Queues: " + overallStats.getActiveUserQueues());
                System.out.println("Total User Queue Items: " + overallStats.getTotalUserQueueItems());
            }

            // Show user-specific statistics
            var userStats = orderService.getUserQueueStats(currentUser);
            System.out.println("=== YOUR QUEUE STATUS ===");
            System.out.println("Total Orders: " + userStats.getTotalOrders());
            System.out.println("Pending Orders: " + userStats.getPendingOrders());
            System.out.println("Processing Orders: " + userStats.getProcessingOrders());

        } catch (Exception e) {
            System.out.println("Could not retrieve queue statistics: " + e.getMessage());
        }
    }

    private static void processNextOrder() {
        System.out.println("=== PROCESS NEXT ORDER ===");
        User currentUser = authService.getCurrentUser();

        Order nextOrder = orderService.peekNextOrder(currentUser);
        if (nextOrder == null) {
            System.out.println("No orders in queue to process.");
            return;
        }

        System.out.println("Next order to process:");
        displayOrderSummary(nextOrder);

        System.out.print("Do you want to process this order? (y/N): ");
        String confirm = scanner.nextLine();

        if (confirm.toLowerCase().startsWith("y")) {
            orderService.processNextOrderInQueue(currentUser);
        } else {
            System.out.println("Order processing cancelled.");
        }
    }

    private static void viewQueueOrders() {
        System.out.println("=== QUEUE ORDERS ===");
        User currentUser = authService.getCurrentUser();

        List<Order> queueOrders = orderService.getUserQueueOrders(currentUser);

        if (queueOrders.isEmpty()) {
            System.out.println("No orders in your queue.");
            return;
        }

        System.out.printf("%-8s %-12s %-15s %-12s %-15s%n",
                "Order ID", "Customer ID", "Status", "Total", "Date");
        System.out.println("-".repeat(70));

        for (Order order : queueOrders) {
            System.out.printf("%-8d %-12d %-15s $%-11.2f %-15s%n",
                    order.getOrderId(),
                    order.getCustomerId(),
                    order.getStatus(),
                    order.getTotalAmount(),
                    order.getOrderDate() != null ? order.getOrderDate().toString().substring(0, 10) : "N/A");
        }
    }

    private static void completeOrderFromQueue() {
        System.out.println("=== COMPLETE ORDER ===");
        User currentUser = authService.getCurrentUser();

        List<Order> queueOrders = orderService.getUserQueueOrders(currentUser);
        if (queueOrders.isEmpty()) {
            System.out.println("No orders in queue to complete.");
            return;
        }

        // Show orders that can be completed (PROCESSING status)
        List<Order> processingOrders = queueOrders.stream()
                .filter(o -> "PROCESSING".equals(o.getStatus().name()))
                .toList();

        if (processingOrders.isEmpty()) {
            System.out.println("No orders in PROCESSING status to complete.");
            return;
        }

        System.out.println("Orders available for completion:");
        for (int i = 0; i < processingOrders.size(); i++) {
            Order order = processingOrders.get(i);
            System.out.printf("%d. Order #%d - $%.2f (%s)%n",
                    i + 1, order.getOrderId(), order.getTotalAmount(), order.getStatus());
        }

        int choice = getIntInput("Select order to complete (0 to cancel): ");
        if (choice > 0 && choice <= processingOrders.size()) {
            Order orderToComplete = processingOrders.get(choice - 1);

            if (orderService.completeOrder(orderToComplete, currentUser)) {
                System.out.println("Order #" + orderToComplete.getOrderId() + " completed successfully!");
            } else {
                System.out.println("Failed to complete order.");
            }
        }
    }

    private static void cancelOrderFromQueue() {
        System.out.println("=== CANCEL ORDER ===");
        User currentUser = authService.getCurrentUser();

        List<Order> queueOrders = orderService.getUserQueueOrders(currentUser);
        if (queueOrders.isEmpty()) {
            System.out.println("No orders in queue to cancel.");
            return;
        }

        // Show orders that can be cancelled (not DELIVERED or CANCELLED)
        List<Order> cancellableOrders = queueOrders.stream()
                .filter(o -> !"DELIVERED".equals(o.getStatus().name()) && !"CANCELLED".equals(o.getStatus().name()))
                .toList();

        if (cancellableOrders.isEmpty()) {
            System.out.println("No orders available for cancellation.");
            return;
        }

        System.out.println("Orders available for cancellation:");
        for (int i = 0; i < cancellableOrders.size(); i++) {
            Order order = cancellableOrders.get(i);
            System.out.printf("%d. Order #%d - $%.2f (%s)%n",
                    i + 1, order.getOrderId(), order.getTotalAmount(), order.getStatus());
        }

        int choice = getIntInput("Select order to cancel (0 to cancel): ");
        if (choice > 0 && choice <= cancellableOrders.size()) {
            Order orderToCancel = cancellableOrders.get(choice - 1);

            System.out.print("Enter cancellation reason: ");
            String reason = scanner.nextLine();

            if (orderService.cancelOrder(orderToCancel, currentUser, reason)) {
                System.out.println("Order #" + orderToCancel.getOrderId() + " cancelled successfully!");
            } else {
                System.out.println("Failed to cancel order.");
            }
        }
    }

    private static void adminQueueManagement() {
        System.out.println("=== ADMIN QUEUE MANAGEMENT ===");

        System.out.println("1. View Pending Orders");
        System.out.println("2. View Completed Orders");
        System.out.println("3. Clear User Queue");
        System.out.println("4. Clear All Queues");
        System.out.println("5. Overall Queue Statistics");
        System.out.println("0. Back");

        int choice = getIntInput("Enter your choice: ");

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

    private static void viewPendingOrders() {
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

    private static void viewCompletedOrders() {
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

    private static void clearUserQueue() {
        System.out.println("=== CLEAR USER QUEUE ===");

        int userId = getIntInput("Enter User ID to clear queue (0 to cancel): ");
        if (userId == 0) {
            return;
        }

        System.out.print("Are you sure you want to clear queue for user " + userId + "? (y/N): ");
        String confirm = scanner.nextLine();

        if (confirm.toLowerCase().startsWith("y")) {
            if (orderService.clearUserQueue(userId, authService.getCurrentUser())) {
                System.out.println("User queue cleared successfully.");
            } else {
                System.out.println("Failed to clear user queue.");
            }
        }
    }

    private static void clearAllQueues() {
        System.out.println("=== CLEAR ALL QUEUES ===");
        System.out.println("⚠️  WARNING: This will clear ALL order queues!");
        System.out.print("Are you absolutely sure? Type 'CLEAR ALL' to confirm: ");
        String confirm = scanner.nextLine();

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

    private static void showDetailedQueueStatistics() {
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

    private static void displayOrderSummary(Order order) {
        System.out.println("Order ID: " + order.getOrderId());
        System.out.println("Customer ID: " + order.getCustomerId());
        System.out.println("Status: " + order.getStatus());
        System.out.println("Total Amount: $" + String.format("%.2f", order.getTotalAmount()));
        System.out.println("Order Date: " + (order.getOrderDate() != null ? order.getOrderDate() : "N/A"));
        if (order.getNotes() != null && !order.getNotes().trim().isEmpty()) {
            System.out.println("Notes: " + order.getNotes());
        }
    }

    private static String truncate(String str, int length) {
        if (str == null)
            return "";
        return str.length() <= length ? str : str.substring(0, length - 3) + "...";
    }

    /**
     * Check if this is a customer's first login or if their profile is incomplete
     */
    private static boolean isFirstLoginOrIncompleteProfile(Customer customer) {
        // Check if essential customer information is missing
        return customer.getAddress() == null || customer.getAddress().trim().isEmpty() ||
               customer.getPhoneNumber() == null || customer.getPhoneNumber().trim().isEmpty() ||
               customer.getPreferredPaymentMethod() == null || customer.getPreferredPaymentMethod().trim().isEmpty();
    }

    /**
     * Guide the customer through completing their profile
     */
    private static boolean completeCustomerProfile(Customer customer) {
        System.out.println("\nLet's set up your profile information:");

        try {
            // Address information
            if (customer.getAddress() == null || customer.getAddress().trim().isEmpty()) {
                System.out.print("Street Address: ");
                String address = scanner.nextLine().trim();
                if (!address.isEmpty()) {
                    customer.setAddress(address);
                }
            }

            // City
            if (customer.getCity() == null || customer.getCity().trim().isEmpty()) {
                System.out.print("City: ");
                String city = scanner.nextLine().trim();
                if (!city.isEmpty()) {
                    customer.setCity(city);
                }
            }

            // State
            if (customer.getState() == null || customer.getState().trim().isEmpty()) {
                System.out.print("State/Province: ");
                String state = scanner.nextLine().trim();
                if (!state.isEmpty()) {
                    customer.setState(state);
                }
            }

            // Postal Code
            if (customer.getPostalCode() == null || customer.getPostalCode().trim().isEmpty()) {
                System.out.print("Postal/ZIP Code: ");
                String postalCode = scanner.nextLine().trim();
                if (!postalCode.isEmpty()) {
                    customer.setPostalCode(postalCode);
                }
            }

            // Country (optional, defaults to USA)
            System.out.print("Country (default: USA): ");
            String country = scanner.nextLine().trim();
            if (!country.isEmpty()) {
                customer.setCountry(country);
            } else {
                customer.setCountry("USA");
            }

            // Phone number
            if (customer.getPhoneNumber() == null || customer.getPhoneNumber().trim().isEmpty()) {
                System.out.print("Phone Number: ");
                String phone = scanner.nextLine().trim();
                if (!phone.isEmpty()) {
                    customer.setPhoneNumber(phone);
                }
            }

            // Preferred payment method
            if (customer.getPreferredPaymentMethod() == null || customer.getPreferredPaymentMethod().trim().isEmpty()) {
                System.out.println("\nPreferred Payment Method:");
                System.out.println("1. Credit Card");
                System.out.println("2. Debit Card");
                System.out.println("3. PayPal");
                System.out.println("4. Bank Transfer");
                System.out.println("5. Cash on Delivery");

                int paymentChoice = getIntInput("Select payment method (1-5): ");
                String paymentMethod = switch (paymentChoice) {
                    case 1 -> "Credit Card";
                    case 2 -> "Debit Card";
                    case 3 -> "PayPal";
                    case 4 -> "Bank Transfer";
                    case 5 -> "Cash on Delivery";
                    default -> "Credit Card"; // Default
                };
                customer.setPreferredPaymentMethod(paymentMethod);
            }

            // Email notifications preference
            System.out.print("Would you like to receive email notifications about your orders? (y/n): ");
            String emailChoice = scanner.nextLine().trim().toLowerCase();
            customer.setEmailNotifications(emailChoice.equals("y") || emailChoice.equals("yes"));

            // Update the customer in the database
            if (updateCustomerProfile(customer)) {
                return true;
            } else {
                System.out.println("Failed to save profile information. Please try again later.");
                return false;
            }

        } catch (Exception e) {
            System.out.println("Error completing profile: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update customer profile in the database
     */
    private static boolean updateCustomerProfile(Customer customer) {
        try {
            System.out.println("Saving profile information...");

            // Get the customer record from database using user ID
            Customer dbCustomer = customerDAO.getCustomerByUserId(customer.getUserId());
            if (dbCustomer == null) {
                System.err.println("Customer record not found in database.");
                return false;
            }

            // Update the database customer record with new information
            dbCustomer.setAddress(customer.getAddress());
            dbCustomer.setPhoneNumber(customer.getPhoneNumber());
            dbCustomer.setPreferredPaymentMethod(customer.getPreferredPaymentMethod());
            dbCustomer.setEmailNotifications(customer.isEmailNotifications());

            // Parse address for city, state, postal code if needed
            // For now, we'll set default values or parse from address
            if (customer.getCity() != null) {
                dbCustomer.setCity(customer.getCity());
            }
            if (customer.getState() != null) {
                dbCustomer.setState(customer.getState());
            }
            if (customer.getPostalCode() != null) {
                dbCustomer.setPostalCode(customer.getPostalCode());
            }
            if (customer.getCountry() != null) {
                dbCustomer.setCountry(customer.getCountry());
            } else {
                dbCustomer.setCountry("USA"); // Default country
            }

            // Update in database using CustomerDAO
            boolean success = customerDAO.updateCustomerByUserId(customer.getUserId(), dbCustomer);

            if (success) {
                System.out.println("Profile information saved successfully!");
            } else {
                System.err.println("Failed to save profile information to database.");
            }

            return success;

        } catch (Exception e) {
            System.err.println("Error updating customer profile: " + e.getMessage());
            return false;
        }
    }
}
