package com.bookstore.util;

import java.util.Scanner;

/**
 * Utility class for handling user input validation and formatting
 * Extracted from Main.java to improve code organization
 */
public class InputValidator {
    private static Scanner scanner = new Scanner(System.in);

    /**
     * Get integer input from user with validation
     * @param prompt The prompt message to display
     * @return Valid integer input
     */
    public static int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    /**
     * Get double input from user with validation
     * @param prompt The prompt message to display
     * @return Valid double input
     */
    public static double getDoubleInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    /**
     * Get string input from user
     * @param prompt The prompt message to display
     * @return User input string
     */
    public static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    /**
     * Get trimmed string input from user
     * @param prompt The prompt message to display
     * @return Trimmed user input string
     */
    public static String getTrimmedStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /**
     * Get masked password input
     * @param prompt The prompt message to display
     * @return Password string
     */
    public static String getMaskedPassword(String prompt) {
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

    /**
     * Truncate string to specified length with ellipsis
     * @param str String to truncate
     * @param length Maximum length
     * @return Truncated string
     */
    public static String truncate(String str, int length) {
        if (str == null)
            return "";
        return str.length() <= length ? str : str.substring(0, length - 3) + "...";
    }

    /**
     * Get yes/no confirmation from user
     * @param prompt The prompt message to display
     * @return true if user confirms (y/yes), false otherwise
     */
    public static boolean getConfirmation(String prompt) {
        System.out.print(prompt);
        String response = scanner.nextLine().trim().toLowerCase();
        return response.equals("y") || response.equals("yes");
    }

    /**
     * Get the scanner instance
     * @return Scanner instance
     */
    public static Scanner getScanner() {
        return scanner;
    }

    /**
     * Set a new scanner instance (useful for testing)
     * @param newScanner Scanner to use
     */
    public static void setScanner(Scanner newScanner) {
        scanner = newScanner;
    }
}
