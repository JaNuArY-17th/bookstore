package com.bookstore.web.api;

import com.bookstore.service.OrderService;
import com.bookstore.dao.OrderDAO;
import com.bookstore.model.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API Controller for Order operations
 * This layer imports and uses existing OrderService logic without modifying it
 */
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderApiController {
    
    // Import existing logic - no changes to existing files needed
    private final OrderService orderService;
    private final OrderDAO orderDAO;
    
    public OrderApiController() {
        this.orderService = new OrderService();
        this.orderDAO = new OrderDAO();
    }
    
    /**
     * GET /api/orders - Get all orders
     */
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        try {
            List<Order> orders = orderDAO.getAllOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * GET /api/orders/{id} - Get order by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable int id) {
        try {
            Order order = orderService.findOrderById(id);
            if (order != null) {
                return ResponseEntity.ok(order);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * POST /api/orders - Create new order
     */
    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody Order order) {
        try {
            int orderId = orderService.createNewOrder(order);
            if (orderId != -1) {
                return ResponseEntity.ok("Order created successfully with ID: " + orderId);
            } else {
                return ResponseEntity.badRequest().body("Failed to create order");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error creating order: " + e.getMessage());
        }
    }
    
    /**
     * POST /api/orders/process-queue - Process next order in queue
     */
    @PostMapping("/process-queue")
    public ResponseEntity<String> processNextOrder() {
        try {
            orderService.processNextOrderInQueue();
            return ResponseEntity.ok("Order processed successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error processing order: " + e.getMessage());
        }
    }
    
    /**
     * GET /api/orders/queue/size - Get queue size
     */
    @GetMapping("/queue/size")
    public ResponseEntity<Integer> getQueueSize() {
        try {
            int queueSize = com.bookstore.service.OrderProcessingQueue.getQueueSize();
            return ResponseEntity.ok(queueSize);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}