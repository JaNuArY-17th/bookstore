package com.bookstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot Application class for the Bookstore Web UI
 * This enables the web layer while keeping all existing logic intact
 */
@SpringBootApplication
public class BookstoreWebApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(BookstoreWebApplication.class, args);
        System.out.println("Bookstore Web Application started!");
        System.out.println("Access the web interface at: http://localhost:8080");
        System.out.println("API endpoints available at: http://localhost:8080/api/");
    }
}