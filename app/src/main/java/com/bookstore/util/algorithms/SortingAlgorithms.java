package com.bookstore.util.algorithms;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.bookstore.model.OrderItem;
import com.bookstore.model.Book;
import com.bookstore.dao.BookDAO;

/**
 * Utility class providing sorting algorithms for various data types.
 * Uses generic methods to reduce code duplication while maintaining type safety.
 */
public class SortingAlgorithms {
    // Factory method to create book title comparator for OrderItems
    public static Comparator<OrderItem> createBookTitleComparator(BookDAO bookDAO) {
        return (item1, item2) -> {
            Book book1 = bookDAO.getBookById(item1.getBookId());
            Book book2 = bookDAO.getBookById(item2.getBookId());

            if (book1 == null || book1.getTitle() == null)
                return -1;
            if (book2 == null || book2.getTitle() == null)
                return 1;
            return book1.getTitle().compareToIgnoreCase(book2.getTitle());
        };
    }

    // Comparators for Book (for sorting books directly)
    public static final Comparator<Book> BOOK_TITLE_COMPARATOR_BOOK = (book1, book2) -> {
        if (book1.getTitle() == null) return -1;
        if (book2.getTitle() == null) return 1;
        return book1.getTitle().compareToIgnoreCase(book2.getTitle());
    };

    public static final Comparator<Book> BOOK_PRICE_COMPARATOR = (book1, book2) -> {
        return Double.compare(book1.getPrice(), book2.getPrice());
    };

    // Generic Merge Sort Algorithm (for stable sorting requirements)
    public static <T> void mergeSort(List<T> list, Comparator<T> comparator) {
        if (list == null || list.size() <= 1) return;

        List<T> temp = new ArrayList<>(list);
        mergeSort(list, temp, 0, list.size() - 1, comparator);
    }

    private static <T> void mergeSort(List<T> list, List<T> temp, int left, int right, Comparator<T> comparator) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(list, temp, left, mid, comparator);
            mergeSort(list, temp, mid + 1, right, comparator);
            merge(list, temp, left, mid, right, comparator);
        }
    }

    private static <T> void merge(List<T> list, List<T> temp, int left, int mid, int right, Comparator<T> comparator) {
        // Copy elements to temp array
        for (int i = left; i <= right; i++) {
            temp.set(i, list.get(i));
        }

        int i = left, j = mid + 1, k = left;

        // Merge back to original list
        while (i <= mid && j <= right) {
            if (comparator.compare(temp.get(i), temp.get(j)) <= 0) {
                list.set(k++, temp.get(i++));
            } else {
                list.set(k++, temp.get(j++));
            }
        }

        // Copy remaining elements
        while (i <= mid) {
            list.set(k++, temp.get(i++));
        }
        while (j <= right) {
            list.set(k++, temp.get(j++));
        }
    }

    // Generic Quick Sort Algorithm
    public static <T> void quickSort(List<T> list, Comparator<T> comparator) {
        if (list == null || list.size() <= 1) return;
        quickSort(list, 0, list.size() - 1, comparator);
    }

    private static <T> void quickSort(List<T> list, int low, int high, Comparator<T> comparator) {
        if (low < high) {
            int pi = partition(list, low, high, comparator);
            quickSort(list, low, pi - 1, comparator);
            quickSort(list, pi + 1, high, comparator);
        }
    }

    private static <T> int partition(List<T> list, int low, int high, Comparator<T> comparator) {
        T pivot = list.get(high);
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            if (comparator.compare(list.get(j), pivot) <= 0) {
                i++;
                // swap list.get(i) and list.get(j)
                T temp = list.get(i);
                list.set(i, list.get(j));
                list.set(j, temp);
            }
        }
        // swap list.get(i+1) and list.get(high) (pivot)
        T temp = list.get(i + 1);
        list.set(i + 1, list.get(high));
        list.set(high, temp);
        return i + 1;
    }

    // Convenience method for Books (delegates to generic quickSort)
    public static void quickSortBooks(List<Book> list, Comparator<Book> comparator) {
        quickSort(list, comparator);
    }
}
