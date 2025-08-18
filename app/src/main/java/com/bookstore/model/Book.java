package com.bookstore.model;

public class Book {
    private int bookId;
    private String title;
    private String author;
    private String isbn;
    private double price;
    private int stockQuantity;
    private String category;

    // Constructor
    public Book() {
    }

    // Constructor for creating new books (without ID - for AUTO_INCREMENT)
    public Book(String title, String author, String isbn, double price, int stockQuantity) {
        this.bookId = 0; // Will be set by database AUTO_INCREMENT
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = null; // Default to null
    }

    // Constructor for creating new books with category (without ID - for AUTO_INCREMENT)
    public Book(String title, String author, String isbn, double price, int stockQuantity, String category) {
        this.bookId = 0; // Will be set by database AUTO_INCREMENT
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
    }

    // Constructor for existing books (with ID - from database)
    public Book(int bookId, String title, String author, String isbn, double price, int stockQuantity) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = null; // Default to null
    }

    // Constructor for existing books with category (with ID - from database)
    public Book(int bookId, String title, String author, String isbn, double price, int stockQuantity, String category) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
    }

    // Getters and Setters
    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = Math.max(0, stockQuantity); // Ensure non-negative stock
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    // Utility methods
    public boolean isInStock() {
        return stockQuantity > 0;
    }

    public boolean hasStock(int requestedQuantity) {
        return stockQuantity >= requestedQuantity;
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookId=" + bookId +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", price=" + price +
                ", stockQuantity=" + stockQuantity +
                ", category='" + category + '\'' +
                '}';
    }
}
