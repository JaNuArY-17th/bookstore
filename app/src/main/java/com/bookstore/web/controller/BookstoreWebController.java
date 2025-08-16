package com.bookstore.web.controller;

import com.bookstore.dao.BookDAO;
import com.bookstore.dao.CustomerDAO;
import com.bookstore.dao.OrderDAO;
import com.bookstore.model.Book;
import com.bookstore.model.Customer;
import com.bookstore.model.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Web Controller for serving HTML pages
 * This layer imports and uses existing DAO logic to populate web pages
 */
@Controller
public class BookstoreWebController {
    
    // Import existing logic - no changes to existing files needed
    private final BookDAO bookDAO;
    private final CustomerDAO customerDAO;
    private final OrderDAO orderDAO;
    
    public BookstoreWebController() {
        this.bookDAO = new BookDAO();
        this.customerDAO = new CustomerDAO();
        this.orderDAO = new OrderDAO();
    }
    
    /**
     * Home page - Display all books
     */
    @GetMapping("/")
    public String home(Model model) {
        try {
            List<Book> books = bookDAO.getAllBooks();
            model.addAttribute("books", books);
            model.addAttribute("pageTitle", "Bookstore - Home");
            return "index";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading books: " + e.getMessage());
            return "error";
        }
    }
    
    /**
     * Books page - Manage books
     */
    @GetMapping("/books")
    public String books(Model model) {
        try {
            List<Book> books = bookDAO.getAllBooks();
            model.addAttribute("books", books);
            model.addAttribute("pageTitle", "Manage Books");
            return "books";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading books: " + e.getMessage());
            return "error";
        }
    }
    
    /**
     * Book details page
     */
    @GetMapping("/books/{id}")
    public String bookDetails(@PathVariable int id, Model model) {
        try {
            Book book = bookDAO.getBookById(id);
            if (book != null) {
                model.addAttribute("book", book);
                model.addAttribute("pageTitle", "Book Details - " + book.getTitle());
                return "book-details";
            } else {
                model.addAttribute("error", "Book not found");
                return "error";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error loading book: " + e.getMessage());
            return "error";
        }
    }
    
    /**
     * Orders page - Display all orders
     */
    @GetMapping("/orders")
    public String orders(Model model) {
        try {
            List<Order> orders = orderDAO.getAllOrders();
            model.addAttribute("orders", orders);
            model.addAttribute("pageTitle", "Manage Orders");
            model.addAttribute("queueSize", com.bookstore.service.OrderProcessingQueue.getQueueSize());
            return "orders";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading orders: " + e.getMessage());
            return "error";
        }
    }
    
    /**
     * Customers page - Display all customers
     */
    @GetMapping("/customers")
    public String customers(Model model) {
        try {
            List<Customer> customers = customerDAO.getAllCustomers();
            model.addAttribute("customers", customers);
            model.addAttribute("pageTitle", "Manage Customers");
            return "customers";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading customers: " + e.getMessage());
            return "error";
        }
    }
    
    /**
     * Add book form page
     */
    @GetMapping("/books/add")
    public String addBookForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("pageTitle", "Add New Book");
        return "add-book";
    }
    
    /**
     * Create order form page
     */
    @GetMapping("/orders/create")
    public String createOrderForm(Model model) {
        try {
            List<Book> books = bookDAO.getAllBooks();
            List<Customer> customers = customerDAO.getAllCustomers();
            model.addAttribute("books", books);
            model.addAttribute("customers", customers);
            model.addAttribute("pageTitle", "Create New Order");
            return "create-order";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading data: " + e.getMessage());
            return "error";
        }
    }
}