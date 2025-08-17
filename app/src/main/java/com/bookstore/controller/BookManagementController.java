package com.bookstore.controller;

import com.bookstore.dao.BookDAO;
import com.bookstore.model.Book;
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

    public BookManagementController(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
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
            System.out.println("5. Update Book");
            System.out.println("6. Delete Book");
            System.out.println("7. Update Stock");
            System.out.println("8. Refresh Book List");
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
                    updateBook();
                    break;
                case 6:
                    deleteBook();
                    break;
                case 7:
                    updateBookStock();
                    break;
                case 8:
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

        Book book = new Book(0, title, author, isbn, price, stock);
        int bookId = bookDAO.addBook(book);

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
                List<Book> allBooks = bookDAO.getAllBooks();
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
        Book book = bookDAO.getBookById(bookId);

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
        Book book = bookDAO.getBookById(bookId);

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
        Book book = bookDAO.getBookById(bookId);

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
        List<Book> books = bookDAO.getAllBooks();

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
}
