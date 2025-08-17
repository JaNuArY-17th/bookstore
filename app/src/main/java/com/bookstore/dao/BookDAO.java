package com.bookstore.dao;

import com.bookstore.model.Book;
import com.bookstore.util.database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    public int addBook(Book book) {
        String sql = "INSERT INTO Books (title, author, isbn, price, stock_quantity) VALUES (?, ?, ?, ?, ?)";
        int bookId = -1;
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getIsbn());
            pstmt.setDouble(4, book.getPrice());
            pstmt.setInt(5, book.getStockQuantity());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        bookId = rs.getInt(1);
                        book.setBookId(bookId); // Update ID for Book object
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding book: " + e.getMessage());
            if (e.getErrorCode() == 1062) { // MySQL error code for duplicate entry for key 'isbn'
                System.err.println("Book with this ISBN already exists.");
            }
        }
        return bookId;
    }

    public Book getBookById(int bookId) {
        String sql = "SELECT * FROM Books WHERE book_id = ?";
        Book book = null;
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    book = new Book();
                    book.setBookId(rs.getInt("book_id"));
                    book.setTitle(rs.getString("title"));
                    book.setAuthor(rs.getString("author"));
                    book.setIsbn(rs.getString("isbn"));
                    book.setPrice(rs.getDouble("price"));
                    book.setStockQuantity(rs.getInt("stock_quantity"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting book by ID: " + e.getMessage());
        }
        return book;
    }

    public Book getBookByIsbn(String isbn) {
        String sql = "SELECT * FROM Books WHERE isbn = ?";
        Book book = null;
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, isbn);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    book = new Book();
                    book.setBookId(rs.getInt("book_id"));
                    book.setTitle(rs.getString("title"));
                    book.setAuthor(rs.getString("author"));
                    book.setIsbn(rs.getString("isbn"));
                    book.setPrice(rs.getDouble("price"));
                    book.setStockQuantity(rs.getInt("stock_quantity"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting book by ISBN: " + e.getMessage());
        }
        return book;
    }

    public boolean updateBook(Book book) {
        String sql = "UPDATE Books SET title = ?, author = ?, isbn = ?, price = ?, stock_quantity = ? WHERE book_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getIsbn());
            pstmt.setDouble(4, book.getPrice());
            pstmt.setInt(5, book.getStockQuantity());
            pstmt.setInt(6, book.getBookId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating book: " + e.getMessage());
            if (e.getErrorCode() == 1062) { // MySQL error code for duplicate entry for key 'isbn'
                System.err.println("Cannot update: Book with this ISBN already exists.");
            }
            return false;
        }
    }

    public boolean updateBookStock(int bookId, int quantityChange) {
        // Get current stock quantity before checking
        Book book = getBookById(bookId);
        if (book == null) {
            System.err.println("Book with ID " + bookId + " not found for stock update.");
            return false;
        }

        int newQuantity = book.getStockQuantity() + quantityChange;
        if (newQuantity < 0) {
            System.err.println("Cannot reduce stock below zero for book ID " + bookId + ". Current stock: "
                    + book.getStockQuantity() + ", requested change: " + quantityChange);
            return false;
        }

        String sql = "UPDATE Books SET stock_quantity = ? WHERE book_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, newQuantity);
            pstmt.setInt(2, bookId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating book stock: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteBook(int bookId) {
        // Note: Need to handle foreign key constraints
        // If there are OrderItems associated with this book, you can:
        // 1. Configure ON DELETE CASCADE on database (OrderItems also deleted)
        // 2. Delete all OrderItems associated before deleting book
        // 3. Don't allow deletion if there are OrderItems
        String sql = "DELETE FROM Books WHERE book_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting book: " + e.getMessage());
            if (e.getErrorCode() == 1451) { // MySQL error code for foreign key constraint fail
                System.err.println("Cannot delete book: There are existing order items associated with this book.");
            }
            return false;
        }
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM Books";
        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Book book = new Book();
                book.setBookId(rs.getInt("book_id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setIsbn(rs.getString("isbn"));
                book.setPrice(rs.getDouble("price"));
                book.setStockQuantity(rs.getInt("stock_quantity"));
                books.add(book);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all books: " + e.getMessage());
        }
        return books;
    }

    public List<Book> searchBooks(String searchTerm) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM Books WHERE title LIKE ? OR author LIKE ? OR isbn LIKE ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Book book = new Book();
                    book.setBookId(rs.getInt("book_id"));
                    book.setTitle(rs.getString("title"));
                    book.setAuthor(rs.getString("author"));
                    book.setIsbn(rs.getString("isbn"));
                    book.setPrice(rs.getDouble("price"));
                    book.setStockQuantity(rs.getInt("stock_quantity"));
                    books.add(book);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching books: " + e.getMessage());
        }
        return books;
    }
}