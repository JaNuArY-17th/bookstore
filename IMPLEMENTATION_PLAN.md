# Bookstore Application Refactoring Implementation Plan

## Overview
This document provides a step-by-step implementation plan for refactoring the bookstore application according to the PROJECT_DOCUMENTATION.md requirements.

## Phase 1: DAO Layer Refactoring

### 1.1 BookDAO Modifications
**File**: `app/src/main/java/com/bookstore/dao/BookDAO.java`

**Actions Required**:
- ✅ Keep existing methods: `addBook()`, `updateBook()`, `deleteBook()`, `getBookById()`
- ✅ Keep existing method: `getAllBooks()` (already exists)
- ❌ Remove method: `searchBooks()` (move logic to BookService)

### 1.2 OrderDAO Modifications  
**File**: `app/src/main/java/com/bookstore/dao/OrderDAO.java`

**Actions Required**:
- ✅ Keep existing methods: `addOrder()`, `updateOrder()`, `deleteOrder()`, `getOrderById()`
- ✅ Keep existing method: `getAllOrders()` (already exists)
- ✅ Keep existing method: `getOrdersByCustomerId()` (needed for customer data loading)

### 1.3 CustomerDAO Modifications
**File**: `app/src/main/java/com/bookstore/dao/CustomerDAO.java`

**Actions Required**:
- ✅ Keep existing methods: `addCustomer()`, `updateCustomer()`, `deleteCustomer()`, `getCustomerById()`
- ➕ Add method: `getAllCustomers()` (if not exists)

### 1.4 UserDAO Modifications
**File**: `app/src/main/java/com/bookstore/dao/UserDAO.java`

**Actions Required**:
- ✅ Keep existing methods as-is
- ➕ Ensure `getAllUsers()` method exists for admin data loading

## Phase 2: Session Data Management

### 2.1 Create SessionDataManager
**File**: `app/src/main/java/com/bookstore/service/SessionDataManager.java`

**Purpose**: Manage role-based data caching during user sessions and integrate with existing queue system

**Key Features**:
- Initialize ArrayLists on login based on user role
- **Automatically initialize queues from cached data**
- Clear data and queues on logout
- Provide cached data to Service classes
- **Integrate with existing OrderQueueManager**

**Data Structure**:
```java
// Admin caches
private List<User> cachedUsers;
private List<Book> cachedBooks;
private List<Order> cachedOrders;

// Customer caches
private List<Book> cachedBooks;
private List<Order> cachedCustomerOrders;

// Queue integration
private QueueService queueService;
```

**Queue Integration Methods**:
```java
private void initializeQueuesFromCache() {
    OrderQueueManager.clearAllQueues();
    for (Order order : cachedOrders) {
        if ("PENDING".equals(order.getStatus().name()) ||
            "PROCESSING".equals(order.getStatus().name())) {
            OrderQueueManager.addOrderToQueues(order, currentUser);
        }
    }
}
```

### 2.2 Modify AuthService
**File**: `app/src/main/java/com/bookstore/service/AuthService.java`

**Actions Required**:
- ✅ Keep existing functionality
- ➕ Integrate SessionDataManager
- ➕ **Initialize data cache AND queues on successful login**
- ➕ **Clear data cache AND queues on logout**

**Integration Points**:
```java
public boolean login(String username, String password) {
    // Existing login logic...
    if (user != null && PasswordUtil.verifyPassword(password, user.getPassword())) {
        this.currentUser = user;
        userDAO.updateLastLogin(user.getUserId());

        // NEW: Initialize session data and queues
        initializeUserSession(user);

        return true;
    }
    return false;
}

public void logout() {
    this.currentUser = null;
    this.cartService.clearCart();

    // NEW: Clear session data and queues
    if (sessionManager != null) {
        sessionManager.clearSession(); // This will also clear queues
    }
}
```

## Phase 3: Create New Service Classes

### 3.1 Create BookService
**File**: `app/src/main/java/com/bookstore/service/BookService.java`

**Required Methods**:
- `sort(String field, boolean ascending)` - Fields: book_id, title, price, stock_quantity
- `search(String searchTerm)` - Search by book_id, title, author
- `filter(String category, boolean ascending)` - Filter by category
- `addBook(Book book)` - Admin only
- `updateBook(Book book)` - Admin only  
- `deleteBook(int bookId)` - Admin only

**Search Algorithm Recommendation**:
- Use **Multi-field String Matching** with weighted scoring
- Implementation: KMP algorithm for exact matches + fuzzy matching for partial matches
- Search priority: book_id (exact) > title (partial) > author (partial)

### 3.2 Create CustomerService
**File**: `app/src/main/java/com/bookstore/service/CustomerService.java`

**Required Methods**:
- `sort(String field, boolean ascending)` - Fields: customer_id, name
- `search(String searchTerm)` - Search by customer_id, name
- `addCustomer(Customer customer)` - Admin or signup
- `updateCustomer(Customer customer)` - User themselves
- `deleteCustomer(int customerId)` - Admin only

**Search Algorithm Recommendation**:
- Similar to BookService but for customer fields
- Priority: customer_id (exact) > name (partial/fuzzy)

### 3.3 Enhance OrderService
**File**: `app/src/main/java/com/bookstore/service/OrderService.java`

**Additional Methods Needed**:
- `sort(String field, boolean ascending)` - Fields: order_id, order_date
- `search(String searchTerm)` - Search by order_id, customer_id
- `filter(OrderStatus status, boolean ascending)` - Filter by status
- ✅ **Keep existing queue-related methods (already well-implemented)**

**Queue Integration Updates**:
```java
public int createNewOrder(Order order, User currentUser) {
    // 1. Save to database
    int orderId = orderDAO.addOrder(order);
    order.setOrderId(orderId);

    // 2. Add to session cache
    sessionManager.addOrderToCache(order);

    // 3. Add to existing queue system (no changes needed)
    queueService.addOrderToQueue(order, currentUser);

    return orderId;
}
```

**Note**: The existing queue functionality (`initializeQueues()`, `processNextOrderInQueue()`, etc.) works perfectly with the new cached design and requires no changes.

## Phase 4: Algorithm Integration

### 4.1 Enhance SearchingAlgorithms
**File**: `app/src/main/java/com/bookstore/util/algorithms/SearchingAlgorithms.java`

**Add Methods**:
- `searchBooks(List<Book> books, String searchTerm)`
- `searchCustomers(List<Customer> customers, String searchTerm)`
- `searchOrders(List<Order> orders, String searchTerm)`

### 4.2 Enhance SortingAlgorithms  
**File**: `app/src/main/java/com/bookstore/util/algorithms/SortingAlgorithms.java`

**Add Comparators**:
- Book comparators: ID, title, price, stock_quantity
- Customer comparators: ID, name
- Order comparators: ID, date, status

## Phase 5: Controller Layer Updates

### 5.1 Update Controllers
**Files**: All controller classes

**Actions Required**:
- Replace direct DAO calls with Service layer calls
- Update search/sort/filter functionality to use new Service methods
- Ensure proper error handling and user feedback

## Phase 6: Queue System Integration (Not Enhancement)

### 6.1 Current Queue System Status
✅ **Already Well Implemented and Ready for Integration**:
- OrderQueueManager with multiple queue types (admin, user, pending, completed)
- Role-based queue access (admin sees all, customers see only their orders)
- Queue statistics and monitoring
- Thread-safe operations with ConcurrentHashMap
- Integration with OrderService and QueueService
- **No enhancements needed - just integration with cached data**

### 6.2 Queue System Integration with Cached Data

**Current Queue System Status**: ✅ **Well-implemented and ready for integration**
- OrderQueueManager with role-based access
- Multiple queue types (admin, user, pending, completed)
- Thread-safe operations with ConcurrentHashMap
- Comprehensive statistics and monitoring

**Integration Strategy**:
The existing queue system needs to be integrated with the new SessionDataManager for automatic initialization during login.

**Key Integration Points**:

1. **Automatic Queue Initialization on Login**
```java
// In SessionDataManager.initializeUserSession()
private void initializeQueuesFromCache() {
    // Clear existing queues first
    OrderQueueManager.clearAllQueues();

    // Load orders from cache into queues
    for (Order order : cachedOrders) {
        String status = order.getStatus().name();
        if ("PENDING".equals(status) || "PROCESSING".equals(status)) {
            OrderQueueManager.addOrderToQueues(order, currentUser);
        }
    }
}
```

2. **Queue-Cache-Database Synchronization**
```java
// When order status changes
public boolean updateOrderStatus(int orderId, String newStatus) {
    // 1. Update database
    boolean dbUpdated = orderDAO.updateOrderStatus(orderId, newStatus);

    if (dbUpdated) {
        // 2. Update cache
        Order cachedOrder = sessionManager.findOrderInCache(orderId);
        cachedOrder.setStatus(OrderStatus.valueOf(newStatus));

        // 3. Update queues
        OrderQueueManager.updateOrderInQueues(cachedOrder);
    }
    return dbUpdated;
}
```

3. **Role-Based Queue Access with Cached Data**
- **Admin**: Access all cached orders through admin queue
- **Customer**: Access only their cached orders through user-specific queue
- **Queue operations work with cached Order objects** (no database queries during queue processing)

## Implementation Timeline

### Week 1: DAO Layer Refactoring
- Day 1-2: Remove search methods from DAOs
- Day 3-4: Add missing getAll methods
- Day 5: Testing and validation

### Week 2: Service Layer Creation & Queue Integration
- Day 1-2: Create SessionDataManager with queue integration
- Day 3-4: Create BookService and CustomerService
- Day 5: Enhance OrderService and integrate with cached data

### Week 3: Algorithm Integration
- Day 1-2: Enhance SearchingAlgorithms
- Day 3-4: Add new comparators to SortingAlgorithms
- Day 5: Integration testing

### Week 4: Controller Updates & Testing
- Day 1-3: Update all controllers
- Day 4-5: End-to-end testing and bug fixes

## Testing Strategy

### Unit Tests Required
- Service layer methods (sort, search, filter)
- Algorithm implementations
- Session data management

### Integration Tests Required  
- Login/logout data caching
- Service-DAO integration
- Controller-Service integration

### Performance Tests
- Search algorithm efficiency
- Large dataset handling
- Memory usage optimization

## Risk Mitigation

### Data Consistency
- Implement proper transaction handling
- Add data validation in Service layer
- Ensure cache synchronization

### Performance Considerations
- Monitor memory usage with large datasets
- Implement pagination for large result sets
- Consider lazy loading for non-critical data

### Security Considerations
- Maintain role-based access control
- Validate user permissions in Service layer
- Secure sensitive operations (admin-only functions)

## Success Criteria

1. ✅ All search/sort/filter logic moved from DAO to Service layer
2. ✅ Role-based data caching implemented
3. ✅ All algorithms from util.algorithms package utilized
4. ✅ **Existing queue functionality preserved and integrated with cached data**
5. ✅ **Automatic queue initialization on login implemented**
6. ✅ **Queue-cache-database synchronization working**
7. ✅ No breaking changes to user interface
8. ✅ Performance maintained or improved
9. ✅ All tests passing

## Detailed Implementation Examples

### SessionDataManager Implementation
```java
@Component
public class SessionDataManager {
    private List<User> cachedUsers;
    private List<Book> cachedBooks;
    private List<Order> cachedOrders;
    private User currentUser;

    public void initializeUserSession(User user, UserDAO userDAO, BookDAO bookDAO, OrderDAO orderDAO) {
        this.currentUser = user;

        if (user.getRole() == Role.ADMIN) {
            // Admin gets all data
            this.cachedUsers = userDAO.getAllUsers();
            this.cachedBooks = bookDAO.getAllBooks();
            this.cachedOrders = orderDAO.getAllOrders();
        } else {
            // Customer gets limited data
            this.cachedUsers = null; // No access to users
            this.cachedBooks = bookDAO.getAllBooks();
            this.cachedOrders = orderDAO.getOrdersByCustomerId(user.getUserId());
        }
    }

    public void clearSession() {
        this.cachedUsers = null;
        this.cachedBooks = null;
        this.cachedOrders = null;
        this.currentUser = null;
    }

    // Getters for cached data
    public List<Book> getCachedBooks() { return cachedBooks; }
    public List<Order> getCachedOrders() { return cachedOrders; }
    public List<User> getCachedUsers() { return cachedUsers; }
}
```

### BookService Implementation Template
```java
@Service
public class BookService {
    private SessionDataManager sessionManager;
    private BookDAO bookDAO;

    public List<Book> sort(String field, boolean ascending) {
        List<Book> books = new ArrayList<>(sessionManager.getCachedBooks());
        Comparator<Book> comparator = getBookComparator(field);

        if (!ascending) {
            comparator = comparator.reversed();
        }

        SortingAlgorithms.quickSort(books, comparator);
        return books;
    }

    public List<Book> search(String searchTerm) {
        return SearchingAlgorithms.searchBooks(sessionManager.getCachedBooks(), searchTerm);
    }

    public List<Book> filter(String category, boolean ascending) {
        // Implementation for category filtering
        // Note: Requires adding category field to Book model
    }

    private Comparator<Book> getBookComparator(String field) {
        switch (field.toLowerCase()) {
            case "book_id": return Comparator.comparing(Book::getBookId);
            case "title": return Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER);
            case "price": return Comparator.comparing(Book::getPrice);
            case "stock_quantity": return Comparator.comparing(Book::getStockQuantity);
            default: throw new IllegalArgumentException("Invalid sort field: " + field);
        }
    }
}
```

### Enhanced SearchingAlgorithms
```java
public class SearchingAlgorithms {

    public static List<Book> searchBooks(List<Book> books, String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return new ArrayList<>(books);
        }

        String term = searchTerm.toLowerCase().trim();
        List<BookSearchResult> results = new ArrayList<>();

        for (Book book : books) {
            int score = calculateBookSearchScore(book, term);
            if (score > 0) {
                results.add(new BookSearchResult(book, score));
            }
        }

        // Sort by relevance score (highest first)
        results.sort((a, b) -> Integer.compare(b.score, a.score));

        return results.stream()
                .map(result -> result.book)
                .collect(Collectors.toList());
    }

    private static int calculateBookSearchScore(Book book, String searchTerm) {
        int score = 0;

        // Exact ID match (highest priority)
        try {
            int searchId = Integer.parseInt(searchTerm);
            if (book.getBookId() == searchId) {
                return 1000;
            }
        } catch (NumberFormatException ignored) {}

        // Title matching
        String title = book.getTitle().toLowerCase();
        if (title.equals(searchTerm)) {
            score += 100; // Exact title match
        } else if (title.contains(searchTerm)) {
            score += 50; // Partial title match
        }

        // Author matching
        String author = book.getAuthor().toLowerCase();
        if (author.equals(searchTerm)) {
            score += 80; // Exact author match
        } else if (author.contains(searchTerm)) {
            score += 30; // Partial author match
        }

        // ISBN matching
        if (book.getIsbn().toLowerCase().contains(searchTerm)) {
            score += 60;
        }

        return score;
    }

    private static class BookSearchResult {
        final Book book;
        final int score;

        BookSearchResult(Book book, int score) {
            this.book = book;
            this.score = score;
        }
    }
}
```

## Queue System Integration Details

### Current Queue System Strengths
Your existing queue implementation is excellent and requires no enhancements:

✅ **Well-Designed Architecture**:
- `OrderQueueManager` - Static queue management with role-based access
- `QueueService` - Business logic layer for queue operations
- `OrderService` - High-level integration with queue functionality

✅ **Multiple Queue Types**:
- **Admin Queue** - All orders (for admin users)
- **User Queues** - Individual customer queues (customers see only their orders)
- **Pending Queue** - Orders with PENDING status
- **Completed Queue** - Completed orders for history/reporting

✅ **Thread-Safe Operations**:
- Uses `ConcurrentHashMap` for user queues
- Proper exception handling with `QueueEmptyException` and `QueueFullException`

✅ **Comprehensive Features**:
- Queue statistics and monitoring
- Role-based queue access
- Order status tracking
- Queue clearing operations (admin only)

### Integration with Cached Data Flow

**Current Flow** (Manual):
```
Login → Manual queue initialization → Queue operations
```

**New Flow** (Automatic):
```
Login → Load cached data → Auto-initialize queues → Queue operations
```

### Key Integration Benefits

1. **Performance**: Queue operations work with cached Order objects (no database queries)
2. **Consistency**: Single data source (cache) for both Service operations and Queue operations
3. **Role-based Access**: Existing queue role separation works perfectly with cached data
4. **No Breaking Changes**: All existing queue functionality preserved

## Next Steps

1. Review and approve this implementation plan
2. Set up development branch for refactoring
3. Begin Phase 1 implementation
4. Regular progress reviews and adjustments

## Additional Considerations

### Database Schema Updates
- Consider adding `category` field to Books table for filtering
- Add `priority` field to Orders table for queue management
- Add indexes for frequently searched fields

### Configuration Management
- Add configuration for queue sizes and processing strategies
- Configure search algorithm parameters
- Set up performance monitoring thresholds

### Documentation Updates
- Update API documentation for new Service methods
- Create user guides for new search/filter capabilities
- Document queue management procedures for administrators
