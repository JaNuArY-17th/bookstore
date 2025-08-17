package com.bookstore.util.ui;

import java.util.List;

/**
 * Utility class for handling pagination of lists
 */
public class PaginationUtil {
    
    /**
     * Get a page of items from a list
     * @param items The full list of items
     * @param pageNumber The page number (1-based)
     * @param pageSize The number of items per page
     * @return Sublist containing items for the specified page
     */
    public static <T> List<T> getPage(List<T> items, int pageNumber, int pageSize) {
        if (items == null || items.isEmpty()) {
            return items;
        }
        
        int totalItems = items.size();
        int startIndex = (pageNumber - 1) * pageSize;
        
        if (startIndex >= totalItems) {
            return items.subList(0, 0); // Return empty list if page is out of bounds
        }
        
        int endIndex = Math.min(startIndex + pageSize, totalItems);
        return items.subList(startIndex, endIndex);
    }
    
    /**
     * Calculate total number of pages
     * @param totalItems Total number of items
     * @param pageSize Number of items per page
     * @return Total number of pages
     */
    public static int getTotalPages(int totalItems, int pageSize) {
        if (totalItems <= 0 || pageSize <= 0) {
            return 0;
        }
        return (int) Math.ceil((double) totalItems / pageSize);
    }
    
    /**
     * Display pagination information
     * @param currentPage Current page number (1-based)
     * @param totalPages Total number of pages
     * @param totalItems Total number of items
     * @param pageSize Items per page
     */
    public static void displayPaginationInfo(int currentPage, int totalPages, int totalItems, int pageSize) {
        int startItem = (currentPage - 1) * pageSize + 1;
        int endItem = Math.min(currentPage * pageSize, totalItems);
        
        System.out.println("\n" + "=".repeat(80));
        System.out.printf("Showing %d-%d of %d items | Page %d of %d%n", 
                         startItem, endItem, totalItems, currentPage, totalPages);
        System.out.println("=".repeat(80));
    }
    
    /**
     * Display pagination navigation options
     * @param currentPage Current page number (1-based)
     * @param totalPages Total number of pages
     */
    public static void displayPaginationNavigation(int currentPage, int totalPages) {
        if (totalPages <= 1) {
            return; // No navigation needed for single page
        }
        
        System.out.println("\n=== NAVIGATION ===");
        
        if (currentPage > 1) {
            System.out.println("P. Previous Page");
        }
        if (currentPage < totalPages) {
            System.out.println("N. Next Page");
        }
        
        System.out.println("G. Go to Page");
        System.out.println("F. First Page");
        System.out.println("L. Last Page");
    }
    
    /**
     * Handle pagination navigation input
     * @param input User input for navigation
     * @param currentPage Current page number
     * @param totalPages Total number of pages
     * @return New page number, or -1 if invalid input
     */
    public static int handleNavigationInput(String input, int currentPage, int totalPages) {
        if (input == null || input.trim().isEmpty()) {
            return -1;
        }
        
        String command = input.trim().toUpperCase();
        
        switch (command) {
            case "P":
            case "PREV":
            case "PREVIOUS":
                return currentPage > 1 ? currentPage - 1 : -1;
                
            case "N":
            case "NEXT":
                return currentPage < totalPages ? currentPage + 1 : -1;
                
            case "F":
            case "FIRST":
                return 1;
                
            case "L":
            case "LAST":
                return totalPages;
                
            case "G":
            case "GO":
                return -2; // Special code to indicate "go to page" was selected
                
            default:
                // Try to parse as page number
                try {
                    int pageNum = Integer.parseInt(command);
                    if (pageNum >= 1 && pageNum <= totalPages) {
                        return pageNum;
                    }
                } catch (NumberFormatException e) {
                    // Invalid input
                }
                return -1;
        }
    }
    
    /**
     * Validate page number
     * @param pageNumber Page number to validate
     * @param totalPages Total number of pages
     * @return Valid page number (clamped to valid range)
     */
    public static int validatePageNumber(int pageNumber, int totalPages) {
        if (totalPages <= 0) {
            return 1;
        }
        return Math.max(1, Math.min(pageNumber, totalPages));
    }
    
    /**
     * Display page summary for debugging
     * @param items List of items
     * @param pageNumber Current page
     * @param pageSize Page size
     */
    public static <T> void displayPageSummary(List<T> items, int pageNumber, int pageSize) {
        int totalItems = items.size();
        int totalPages = getTotalPages(totalItems, pageSize);
        
        System.out.printf("Page Summary: Page %d/%d, Items: %d, Page Size: %d%n", 
                         pageNumber, totalPages, totalItems, pageSize);
    }
}
