package com.bookstore.web.api;

import com.bookstore.dao.BookDAO;
import com.bookstore.model.Book;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API Controller for Book operations
 * This layer imports and uses existing BookDAO logic without modifying it
 */
@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*") // Allow frontend to call these APIs
public class BookApiController {
    
    // Import existing logic - no changes to existing files needed
    private final BookDAO bookDAO;
    
    public BookApiController() {
        this.bookDAO = new BookDAO();
    }
    
    /**
     * GET /api/books - Get all books
     */
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        try {
            List<Book> books = bookDAO.getAllBooks();
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * GET /api/books/{id} - Get book by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable int id) {
        try {
            Book book = bookDAO.getBookById(id);
            if (book != null) {
                return ResponseEntity.ok(book);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * POST /api/books - Add new book
     */
    @PostMapping
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        try {
            int bookId = bookDAO.addBook(book);
            if (bookId != -1) {
                book.setBookId(bookId);
                return ResponseEntity.ok(book);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * PUT /api/books/{id} - Update existing book
     */
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable int id, @RequestBody Book book) {
        try {
            book.setBookId(id);
            boolean updated = bookDAO.updateBook(book);
            if (updated) {
                return ResponseEntity.ok(book);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * DELETE /api/books/{id} - Delete book
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable int id) {
        try {
            boolean deleted = bookDAO.deleteBook(id);
            if (deleted) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * PUT /api/books/{id}/stock - Update book stock
     */
    @PutMapping("/{id}/stock")
    public ResponseEntity<Book> updateBookStock(@PathVariable int id, @RequestParam int quantityChange) {
        try {
            boolean updated = bookDAO.updateBookStock(id, quantityChange);
            if (updated) {
                Book updatedBook = bookDAO.getBookById(id);
                return ResponseEntity.ok(updatedBook);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}