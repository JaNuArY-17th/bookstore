package com.bookstore.controller;

import com.bookstore.dao.BookDAO;
import com.bookstore.model.Book;
import com.bookstore.service.BookService;
import com.bookstore.util.ui.DisplayFormatter;
import com.bookstore.util.ui.InputValidator;
import com.bookstore.util.ui.PaginationUtil;
import com.bookstore.util.algorithms.SortingAlgorithms;

import java.util.List;

/**
 * Controller for book management operations
 * Extracted from Main.java to improve code organization
 */
public class BookManagementController {
    private BookDAO bookDAO;
    private BookService bookService;

    public BookManagementController(BookDAO bookDAO, BookService bookService) {
        this.bookDAO = bookDAO;
        this.bookService = bookService;
    }

    /**
     * Display and handle book management menu
     */
    public void showBookManagementMenu() {
        while (true) {
            System.out.println("\n=== BOOK MANAGEMENT ===");
            System.out.println("1. Add New Book");
            System.out.println("2. View All Books");
            System.out.println("3. Search Book by ID");
            System.out.println("4. Search Book by ISBN");
            System.out.println("5. Advanced Search Books");
            System.out.println("6. Sort Books");
            System.out.println("7. Filter Books by Category");
            System.out.println("8. Update Book");
            System.out.println("9. Delete Book");
            System.out.println("10. Update Stock");
            System.out.println("11. Refresh Book List");
            System.out.println("0. Back to Main Menu");

            int choice = InputValidator.getIntInput("Enter your choice: ");

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
                    advancedSearchBooks();
                    break;
                case 6:
                    sortBooks();
                    break;
                case 7:
                    filterBooksByCategory();
                    break;
                case 8:
                    updateBook();
                    break;
                case 9:
                    deleteBook();
                    break;
                case 10:
                    updateBookStock();
                    break;
                case 11:
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
     * Add a new book to the system
     */
    public void addNewBook() {
        System.out.println("=== ADD NEW BOOK ===");
        String title = InputValidator.getTrimmedStringInput("Title: ");
        String author = InputValidator.getTrimmedStringInput("Author: ");
        String isbn = InputValidator.getTrimmedStringInput("ISBN: ");
        double price = InputValidator.getDoubleInput("Price: $");
        int stock = InputValidator.getIntInput("Stock Quantity: ");
        String category = InputValidator.getTrimmedStringInput("Category (optional): ");

        // Use constructor without ID (AUTO_INCREMENT will handle it)
        Book book;
        if (category.isEmpty()) {
            book = new Book(title, author, isbn, price, stock);
        } else {
            book = new Book(title, author, isbn, price, stock, category);
        }
        // Note: Need to get current user for admin check
        // For now, assume admin access since this is admin menu
        int bookId = bookDAO.addBook(book); // Keep using DAO for now until we have user context

        if (bookId != -1) {
            System.out.println("Book added successfully with ID: " + bookId);
        } else {
            System.out.println("Failed to add book.");
        }
    }

    /**
     * View all books in the system with pagination
     */
    public void viewAllBooks() {
        int currentPage = 1;
        final int PAGE_SIZE = 10;

        while (true) {
            System.out.println("\n=== ALL BOOKS ===");
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
                System.out.printf("%-5s %-30s %-20s %-15s %-10s %-8s%n",
                        "ID", "Title", "Author", "ISBN", "Price", "Stock");
                System.out.println("=".repeat(90));

                for (Book book : pageBooks) {
                    System.out.printf("%-5d %-30s %-20s %-15s $%-9.2f %-8d%n",
                            book.getBookId(),
                            InputValidator.truncate(book.getTitle(), 30),
                            InputValidator.truncate(book.getAuthor(), 20),
                            book.getIsbn(),
                            book.getPrice(),
                            book.getStockQuantity());
                }

                // Display navigation options
                PaginationUtil.displayPaginationNavigation(currentPage, totalPages);

                System.out.println("\n=== OPTIONS ===");
                System.out.println("0. Back to Book Management Menu");

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
                    if (choice == 0) {
                        return;
                    } else {
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
     * Search for a book by ID
     */
    public void searchBookById() {
        int bookId = InputValidator.getIntInput("Enter Book ID: ");
        Book book = bookService.getBookById(bookId);

        if (book != null) {
            System.out.println("Book found:");
            System.out.println(book);
        } else {
            System.out.println("Book not found with ID: " + bookId);
        }
    }

    /**
     * Search for a book by ISBN
     */
    public void searchBookByIsbn() {
        String isbn = InputValidator.getTrimmedStringInput("Enter ISBN: ");
        Book book = bookDAO.getBookByIsbn(isbn);

        if (book != null) {
            System.out.println("Book found:");
            System.out.println(book);
        } else {
            System.out.println("Book not found with ISBN: " + isbn);
        }
    }

    /**
     * Update book information
     */
    public void updateBook() {
        int bookId = InputValidator.getIntInput("Enter Book ID to update: ");
        Book book = bookService.getBookById(bookId);

        if (book == null) {
            System.out.println("Book not found with ID: " + bookId);
            return;
        }

        System.out.println("Current book details:");
        System.out.println(book);
        System.out.println();

        String title = InputValidator.getStringInput("New Title (current: " + book.getTitle() + "): ");
        if (!title.trim().isEmpty()) {
            book.setTitle(title);
        }

        String author = InputValidator.getStringInput("New Author (current: " + book.getAuthor() + "): ");
        if (!author.trim().isEmpty()) {
            book.setAuthor(author);
        }

        String isbn = InputValidator.getStringInput("New ISBN (current: " + book.getIsbn() + "): ");
        if (!isbn.trim().isEmpty()) {
            book.setIsbn(isbn);
        }

        String priceStr = InputValidator.getStringInput("New Price (current: $" + book.getPrice() + "): ");
        if (!priceStr.trim().isEmpty()) {
            try {
                book.setPrice(Double.parseDouble(priceStr));
            } catch (NumberFormatException e) {
                System.out.println("Invalid price format. Keeping current price.");
            }
        }

        String stockStr = InputValidator.getStringInput("New Stock (current: " + book.getStockQuantity() + "): ");
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

    /**
     * Delete a book from the system
     */
    public void deleteBook() {
        int bookId = InputValidator.getIntInput("Enter Book ID to delete: ");
        Book book = bookService.getBookById(bookId);

        if (book == null) {
            System.out.println("Book not found with ID: " + bookId);
            return;
        }

        System.out.println("Book to delete:");
        System.out.println(book);
        
        if (InputValidator.getConfirmation("Are you sure you want to delete this book? (y/N): ")) {
            if (bookDAO.deleteBook(bookId)) {
                System.out.println("Book deleted successfully.");
            } else {
                System.out.println("Failed to delete book. It may have associated orders.");
            }
        } else {
            System.out.println("Delete operation cancelled.");
        }
    }

    /**
     * Update book stock quantity
     */
    public void updateBookStock() {
        int bookId = InputValidator.getIntInput("Enter Book ID: ");
        int quantityChange = InputValidator.getIntInput("Enter quantity change (positive to add, negative to reduce): ");

        if (bookDAO.updateBookStock(bookId, quantityChange)) {
            System.out.println("Stock updated successfully.");
        } else {
            System.out.println("Failed to update stock.");
        }
    }

    /**
     * Demonstrate sorting algorithms with books
     */
    public void demonstrateSorting() {
        System.out.println("=== SORTING DEMONSTRATION ===");
        List<Book> books = bookService.getAllBooks();

        if (books.isEmpty()) {
            System.out.println("No books available for sorting demonstration.");
            return;
        }

        System.out.println("Original order:");
        DisplayFormatter.displayBookList(books);

        System.out.println("\nSorting by title (Quick Sort):");
        SortingAlgorithms.quickSortBooks(books, SortingAlgorithms.BOOK_TITLE_COMPARATOR_BOOK);
        DisplayFormatter.displayBookList(books);

        System.out.println("\nSorting by price (Quick Sort):");
        SortingAlgorithms.quickSortBooks(books, SortingAlgorithms.BOOK_PRICE_COMPARATOR);
        DisplayFormatter.displayBookList(books);
    }

    /**
     * Advanced search books using BookService
     */
    public void advancedSearchBooks() {
        System.out.println("=== ADVANCED BOOK SEARCH ===");
        String searchTerm = InputValidator.getTrimmedStringInput("Enter search term (title, author, ISBN, or book ID): ");

        if (searchTerm.isEmpty()) {
            System.out.println("Search term cannot be empty.");
            return;
        }

        try {
            List<Book> searchResults = bookService.search(searchTerm);

            if (searchResults.isEmpty()) {
                System.out.println("No books found matching: " + searchTerm);
            } else {
                System.out.println("\n=== SEARCH RESULTS ===");
                System.out.println("Found " + searchResults.size() + " book(s) matching: " + searchTerm);
                DisplayFormatter.displayBookList(searchResults);
            }
        } catch (Exception e) {
            System.out.println("Error performing search: " + e.getMessage());
        }

        System.out.println("\nPress Enter to continue...");
        InputValidator.getStringInput("");
    }

    /**
     * Sort books using BookService
     */
    public void sortBooks() {
        System.out.println("=== SORT BOOKS ===");
        System.out.println("Sort by:");
        System.out.println("1. Book ID");
        System.out.println("2. Title");
        System.out.println("3. Author");
        System.out.println("4. Price");
        System.out.println("5. Stock Quantity");
        System.out.println("0. Cancel");

        int choice = InputValidator.getIntInput("Enter your choice: ");

        String field;
        switch (choice) {
            case 1:
                field = "book_id";
                break;
            case 2:
                field = "title";
                break;
            case 3:
                field = "author";
                break;
            case 4:
                field = "price";
                break;
            case 5:
                field = "stock_quantity";
                break;
            case 0:
                return;
            default:
                System.out.println("Invalid choice.");
                return;
        }

        boolean ascending = InputValidator.getConfirmation("Sort in ascending order? (y/n): ");

        try {
            List<Book> sortedBooks = bookService.sort(field, ascending);

            if (sortedBooks.isEmpty()) {
                System.out.println("No books available to sort.");
            } else {
                System.out.println("\n=== SORTED BOOKS ===");
                System.out.println("Sorted by " + field + " (" + (ascending ? "ascending" : "descending") + "):");
                DisplayFormatter.displayBookList(sortedBooks);
            }
        } catch (Exception e) {
            System.out.println("Error sorting books: " + e.getMessage());
        }

        System.out.println("\nPress Enter to continue...");
        InputValidator.getStringInput("");
    }

    /**
     * Filter books by category using BookService
     */
    public void filterBooksByCategory() {
        System.out.println("=== FILTER BOOKS BY CATEGORY ===");

        try {
            // Get all available categories
            List<String> categories = bookService.getAllCategories();

            if (categories.isEmpty()) {
                System.out.println("No categories available.");
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
                DisplayFormatter.displayBookList(filteredBooks);
            }
        } catch (Exception e) {
            System.out.println("Error filtering books: " + e.getMessage());
        }

        System.out.println("\nPress Enter to continue...");
        InputValidator.getStringInput("");
    }
}
