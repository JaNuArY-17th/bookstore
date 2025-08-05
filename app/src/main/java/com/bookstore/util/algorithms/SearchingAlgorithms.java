package com.bookstore.util.algorithms;

import com.bookstore.model.Order;
import java.util.Comparator;
import java.util.List;

public class SearchingAlgorithms {

    // Comparator for OrderId
    public static final Comparator<Order> ORDER_ID_COMPARATOR = (order1, order2) ->
        Integer.compare(order1.getOrderId(), order2.getOrderId());

    // Binary Search Implementation
    public static Order binarySearchOrderById(List<Order> sortedOrders, int targetOrderId) {
        int low = 0;
        int high = sortedOrders.size() - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            Order midOrder = sortedOrders.get(mid);

            if (midOrder.getOrderId() == targetOrderId) {
                return midOrder;
            } else if (midOrder.getOrderId() < targetOrderId) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return null;
    }
}