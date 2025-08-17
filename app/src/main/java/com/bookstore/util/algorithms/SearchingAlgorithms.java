package com.bookstore.util.algorithms;

import com.bookstore.model.Order;
import java.util.Comparator;
import java.util.List;

/**
 * Utility class providing searching algorithms for various data types.
 * Includes both generic and specialized search implementations.
 */
public class SearchingAlgorithms {

    // Comparator for OrderId
    public static final Comparator<Order> ORDER_ID_COMPARATOR = (order1, order2) ->
        Integer.compare(order1.getOrderId(), order2.getOrderId());

    // Generic Binary Search Implementation
    public static <T> T binarySearch(List<T> sortedList, T target, Comparator<T> comparator) {
        if (sortedList == null || sortedList.isEmpty()) {
            return null;
        }

        int low = 0;
        int high = sortedList.size() - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            T midElement = sortedList.get(mid);

            int comparison = comparator.compare(midElement, target);

            if (comparison == 0) {
                return midElement;
            } else if (comparison < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return null;
    }

    // Convenience method for Order search by ID (delegates to generic binarySearch)
    public static Order binarySearchOrderById(List<Order> sortedOrders, int targetOrderId) {
        if (sortedOrders == null || sortedOrders.isEmpty()) {
            return null;
        }

        // Create a dummy order with the target ID for comparison
        Order targetOrder = new Order();
        targetOrder.setOrderId(targetOrderId);

        return binarySearch(sortedOrders, targetOrder, ORDER_ID_COMPARATOR);
    }
}