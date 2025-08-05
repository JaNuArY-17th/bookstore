package com.bookstore.util.algorithms;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.bookstore.model.OrderItem;

public class SortingAlgorithms {
    public static final Comparator<OrderItem> BOOK_TITLE_COMPARATOR = (item1, item2) -> {
        if (item1.getBook() == null || item1.getBook().getTitle() == null)
            return -1;
        if (item2.getBook() == null || item2.getBook().getTitle() == null)
            return 1;
        return item1.getBook().getTitle().compareToIgnoreCase(item2.getBook().getTitle());
    };

    // Merge sort algorithm
    public static void mergeSort(List<OrderItem> list, Comparator<OrderItem> comparator) {
        if (list == null || list.size() <= 1)
            return;

        int mid = list.size() / 2;

        List<OrderItem> left = new ArrayList<>(list.subList(0, mid));
        List<OrderItem> right = new ArrayList<>(list.subList(mid, list.size()));

        mergeSort(left, comparator);
        mergeSort(right, comparator);

        merge(list, left, right, comparator);
    }

    public static void merge(List<OrderItem> list, List<OrderItem> left, List<OrderItem> right,
            Comparator<OrderItem> comparator) {
        int i = 0, j = 0, k = 0;
        while (i < left.size() && j < right.size()) {
            if (comparator.compare(left.get(i), right.get(j)) <= 0) {
                list.set(k++, left.get(i++));
            } else {
                list.set(k++, right.get(j++));
            }
        }
        while (i < left.size()) {
            list.set(k++, left.get(i++));
        }
        while (j < right.size()) {
            list.set(k++, right.get(j++));
        }
    }

    // Quick sort algorithm
    public static void quickSort(List<OrderItem> list, Comparator<OrderItem> comparator) {
        quickSort(list, 0, list.size() - 1, comparator);
    }

    private static void quickSort(List<OrderItem> list, int low, int high, Comparator<OrderItem> comparator) {
        if (low < high) {
            int pi = partition(list, low, high, comparator);
            quickSort(list, low, pi - 1, comparator);
            quickSort(list, pi + 1, high, comparator);
        }
    }

    private static int partition(List<OrderItem> list, int low, int high, Comparator<OrderItem> comparator) {
        OrderItem pivot = list.get(high);
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            if (comparator.compare(list.get(j), pivot) <= 0) {
                i++;
                // swap list.get(i) and list.get(j)
                OrderItem temp = list.get(i);
                list.set(i, list.get(j));
                list.set(j, temp);
            }
        }
        // swap list.get(i+1) and list.get(high) (pivot)
        OrderItem temp = list.get(i + 1);
        list.set(i + 1, list.get(high));
        list.set(high, temp);
        return i + 1;
    }
}
