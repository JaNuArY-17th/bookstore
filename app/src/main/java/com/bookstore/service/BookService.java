package com.bookstore.service;

import com.bookstore.dao.BookDAO;
import com.bookstore.model.Book;
import com.bookstore.model.Role;
import com.bookstore.model.User;
import com.bookstore.util.algorithms.SearchingAlgorithms;
import com.bookstore.util.algorithms.SortingAlgorithms;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BookService {
    
    private SessionDataManager sessionManager;
    private BookDAO bookDAO;
    
    /**
     * Constructor with session manager dependency
     */
    public BookService(SessionDataManager sessionManager) {
        this.sessionManager = sessionManager;
        this.bookDAO = new BookDAO();
    }
    
    /**
     * Sort books by specified field and order
     * @param field The field to sort by (book_id, title, author, price, stock_quantity)
     * @param ascending True for ascending order, false for descending
     * @return Sorted list of books
     */
    public List<Book> sort(String field, boolean ascending) {
        List<Book> books = getCachedBooksForCurrentUser();
        if (books == null || books.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Create a copy to avoid modifying the original cached list
        List<Book> sortedBooks = new ArrayList<>(books);
        Comparator<Book> comparator = getBookComparator(field);
        
        if (!ascending) {
            comparator = comparator.reversed();
        }
        
        SortingAlgorithms.quickSort(sortedBooks, comparator);
        return sortedBooks;
    }
    
    /**
     * Search books by search term
     * @param searchTerm The term to search for (book_id, title, author, isbn)
     * @return List of books matching the search term
     */
    public List<Book> search(String searchTerm) {
        List<Book> books = getCachedBooksForCurrentUser();
        if (books == null || books.isEmpty()) {
            return new ArrayList<>();
        }
        
        return SearchingAlgorithms.searchBooks(books, searchTerm);
    }
    
    /**
     * Filter books by category
     * @param category The category to filter by
     * @param ascending True for ascending order, false for descending
     * @return Filtered list of books
     */
    public List<Book> filter(String category, boolean ascending) {
        List<Book> books = getCachedBooksForCurrentUser();
        if (books == null || books.isEmpty()) {
            return new ArrayList<>();
        }

        // Filter by category
        List<Book> filteredBooks = books.stream()
                .filter(book -> category.equals(book.getCategory()))
                .collect(Collectors.toList());

        // Sort by title
        Comparator<Book> comparator = Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER);
        if (!ascending) {
            comparator = comparator.reversed();
        }

        SortingAlgorithms.quickSort(filteredBooks, comparator);
        return filteredBooks;
    }

    /**
     * Get all available categories
     * @return List of unique categories
     */
    public List<String> getAllCategories() {
        return bookDAO.getAllCategories();
    }
    
    /**
     * Add new book (admin only)
     * @param book The book to add
     * @param user The user performing the operation
     * @return The ID of the added book, or -1 if failed
     */
    public int addBook(Book book, User user) {
        if (!isAdminUser(user)) {
            System.err.println("Access denied: Admin permission required to add books");
            return -1;
        }
        
        // Add to database
        int bookId = bookDAO.addBook(book);
        
        if (bookId != -1) {
            // Add to cache if successful
            book.setBookId(bookId);
            addBookToCache(book);
            System.out.println("Book added successfully with ID: " + bookId);
        }
        
        return bookId;
    }
    
    /**
     * Update book information (admin only)
     * @param book The book with updated information
     * @param user The user performing the operation
     * @return True if update successful, false otherwise
     */
    public boolean updateBook(Book book, User user) {
        if (!isAdminUser(user)) {
            System.err.println("Access denied: Admin permission required to update books");
            return false;
        }
        
        // Update in database
        boolean updated = bookDAO.updateBook(book);
        
        if (updated) {
            // Update in cache
            updateBookInCache(book);
            System.out.println("Book updated successfully: " + book.getTitle());
        }
        
        return updated;
    }
    
    /**
     * Delete book (admin only)
     * @param bookId The ID of the book to delete
     * @param user The user performing the operation
     * @return True if deletion successful, false otherwise
     */
    public boolean deleteBook(int bookId, User user) {
        if (!isAdminUser(user)) {
            System.err.println("Access denied: Admin permission required to delete books");
            return false;
        }
        
        // Delete from database
        boolean deleted = bookDAO.deleteBook(bookId);
        
        if (deleted) {
            // Remove from cache
            removeBookFromCache(bookId);
            System.out.println("Book deleted successfully with ID: " + bookId);
        }
        
        return deleted;
    }
    
    /**
     * Get book by ID
     * @param bookId The book ID
     * @return Book if found, null otherwise
     */
    public Book getBookById(int bookId) {
        List<Book> books = getCachedBooksForCurrentUser();
        if (books != null) {
            return books.stream()
                    .filter(book -> book.getBookId() == bookId)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }
    
    /**
     * Get all books for current user
     * @return List of books accessible to current user
     */
    public List<Book> getAllBooks() {
        return getCachedBooksForCurrentUser();
    }
    
    /**
     * Update book stock
     * @param bookId The book ID
     * @param quantityChange The change in quantity (positive or negative)
     * @param user The user performing the operation
     * @return True if update successful, false otherwise
     */
    public boolean updateBookStock(int bookId, int quantityChange, User user) {
        if (!isAdminUser(user)) {
            System.err.println("Access denied: Admin permission required to update book stock");
            return false;
        }
        
        // Update in database
        boolean updated = bookDAO.updateBookStock(bookId, quantityChange);
        
        if (updated) {
            // Update in cache
            Book cachedBook = getBookById(bookId);
            if (cachedBook != null) {
                cachedBook.setStockQuantity(cachedBook.getStockQuantity() + quantityChange);
                updateBookInCache(cachedBook);
            }
            System.out.println("Book stock updated successfully for ID: " + bookId);
        }
        
        return updated;
    }
    
    // Helper methods
    
    /**
     * Get cached books for current user
     */
    private List<Book> getCachedBooksForCurrentUser() {
        if (sessionManager != null) {
            return sessionManager.getCachedBooks();
        }
        return new ArrayList<>();
    }
    
    /**
     * Check if user is admin
     */
    private boolean isAdminUser(User user) {
        return user != null && user.getRole() == Role.ADMIN;
    }
    
    /**
     * Get comparator for book sorting
     */
    private Comparator<Book> getBookComparator(String field) {
        switch (field.toLowerCase()) {
            case "book_id":
                return Comparator.comparing(Book::getBookId);
            case "title":
                return Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER);
            case "author":
                return Comparator.comparing(Book::getAuthor, String.CASE_INSENSITIVE_ORDER);
            case "price":
                return Comparator.comparing(Book::getPrice);
            case "stock_quantity":
                return Comparator.comparing(Book::getStockQuantity);
            default:
                throw new IllegalArgumentException("Invalid sort field: " + field);
        }
    }
    
    /**
     * Add book to cache
     */
    private void addBookToCache(Book book) {
        List<Book> books = sessionManager.getCachedBooks();
        if (books != null) {
            books.add(book);
        }
    }
    
    /**
     * Update book in cache
     */
    private void updateBookInCache(Book updatedBook) {
        List<Book> books = sessionManager.getCachedBooks();
        if (books != null) {
            for (int i = 0; i < books.size(); i++) {
                if (books.get(i).getBookId() == updatedBook.getBookId()) {
                    books.set(i, updatedBook);
                    break;
                }
            }
        }
    }
    
    /**
     * Remove book from cache
     */
    private void removeBookFromCache(int bookId) {
        List<Book> books = sessionManager.getCachedBooks();
        if (books != null) {
            books.removeIf(book -> book.getBookId() == bookId);
        }
    }
}
