# Bookstore Management System - Project Documentation

## 📋 Project Overview

The Bookstore Management System is a comprehensive Java console application that provides a complete e-commerce solution for managing books, customers, orders, and inventory. The system supports both customer and admin functionalities with features like shopping cart, order processing, user authentication, and advanced data management.

## 🏗️ Project Structure

```
bookstore/
├── app/
│   ├── build.gradle
│   └── src/
│       ├── main/java/com/bookstore/
│       │   ├── controller/          # MVC Controllers
│       │   ├── dao/                 # Data Access Objects
│       │   ├── main/                # Application Entry Point
│       │   ├── model/               # Data Models
│       │   ├── service/             # Business Logic Services
│       │   └── util/                # Utility Classes
│       └── test/java/com/bookstore/ # Unit Tests
├── gradle/                          # Gradle Wrapper
├── build.gradle                     # Root Build Configuration
├── settings.gradle                  # Gradle Settings
└── PROJECT_DOCUMENTATION.md        # This Documentation
```

## 📁 Detailed Directory Structure

### 🎮 Controllers (`app/src/main/java/com/bookstore/controller/`)

Controllers handle user interface logic and coordinate between views and services.

#### `AuthenticationController.java`
- **Purpose**: Manages user authentication and account operations
- **Key Functions**:
  - User login and signup
  - Password change functionality
  - Profile management for customers
  - Account creation for admins and customers
  - User profile completion and updates

#### `BookManagementController.java`
- **Purpose**: Handles all book-related administrative operations
- **Key Functions**:
  - Add, update, delete books
  - View all books with pagination (10 items per page)
  - Search books by ID and ISBN
  - Stock management
  - Sorting demonstrations using various algorithms

#### `CustomerManagementController.java`
- **Purpose**: Manages customer-related administrative functions
- **Key Functions**:
  - View all customers
  - Search customers by ID
  - Customer profile management

#### `MenuManager.java`
- **Purpose**: Central menu navigation system
- **Key Functions**:
  - Login/logout menu handling
  - Admin main menu with full system access
  - Customer main menu with limited access
  - Role-based menu display

#### `OrderManagementController.java`
- **Purpose**: Comprehensive order and shopping management
- **Key Functions**:
  - Customer book browsing with pagination
  - Book search with paginated results
  - Shopping cart management (add, remove, update quantities)
  - Order creation and processing
  - Order queue management
  - Customer order history viewing

### 💾 Data Access Objects (`app/src/main/java/com/bookstore/dao/`)

DAOs handle all database operations and data persistence.

#### `BookDAO.java`
- **Purpose**: Database operations for books
- **Key Functions**:
  - CRUD operations for books
  - Book search by title, author, ID, ISBN
  - Stock quantity management
  - Bulk book operations

#### `CustomerDAO.java`
- **Purpose**: Database operations for customers
- **Key Functions**:
  - Customer CRUD operations
  - Customer profile management
  - Customer search and retrieval
  - Customer-user relationship management

#### `OrderDAO.java`
- **Purpose**: Database operations for orders and order items
- **Key Functions**:
  - Order creation and management
  - Order item handling
  - Order status updates
  - Order history retrieval
  - Complex order queries

#### `UserDAO.java`
- **Purpose**: Database operations for user authentication
- **Key Functions**:
  - User CRUD operations
  - Password management
  - User authentication
  - Role-based user management
  - Last login tracking

### 🏢 Models (`app/src/main/java/com/bookstore/model/`)

Model classes represent the core data structures of the application.

#### `Book.java`
- **Purpose**: Represents book entities
- **Properties**: ID, title, author, ISBN, price, stock quantity, category, description, publication year
- **Features**: Complete book information management

#### `Customer.java` (extends `User.java`)
- **Purpose**: Represents customer users
- **Properties**: Customer-specific fields like address, phone, order history, payment preferences
- **Features**: Customer permissions, profile management, order capabilities

#### `Admin.java` (extends `User.java`)
- **Purpose**: Represents administrative users
- **Properties**: Admin-specific permissions and capabilities
- **Features**: Full system access, user management capabilities

#### `User.java` (Abstract Base Class)
- **Purpose**: Base class for all user types
- **Properties**: Common user fields (ID, username, password, email, name, role, status)
- **Features**: Authentication, role management, abstract methods for permissions

#### `Order.java`
- **Purpose**: Represents customer orders
- **Properties**: Order ID, customer ID, date, total amount, status, tracking info
- **Features**: Order lifecycle management, status tracking

#### `OrderItem.java`
- **Purpose**: Represents individual items within an order
- **Properties**: Order ID, book ID, quantity, unit price
- **Features**: Order composition, pricing calculations

#### Enums:
- **`Role.java`**: User roles (ADMIN, CUSTOMER)
- **`OrderStatus.java`**: Order statuses (PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED)

### 🔧 Services (`app/src/main/java/com/bookstore/service/`)

Services contain business logic and coordinate between controllers and DAOs.

#### `AuthService.java`
- **Purpose**: Authentication and authorization management
- **Key Functions**:
  - User login/logout
  - Session management
  - Password change
  - User registration
  - Permission checking
  - Shopping cart integration

#### `CartService.java`
- **Purpose**: Shopping cart functionality
- **Key Functions**:
  - Add/remove items from cart
  - Update item quantities
  - Cart validation and stock checking
  - Cart-to-order conversion
  - Session-based cart persistence

#### `OrderService.java`
- **Purpose**: Order processing business logic
- **Key Functions**:
  - Order creation and validation
  - Stock availability checking
  - Order total calculations
  - Order queue integration

#### `QueueService.java`
- **Purpose**: Order queue management
- **Key Functions**:
  - Order queue operations
  - Queue status tracking
  - Order processing workflow

### 🛠️ Utilities (`app/src/main/java/com/bookstore/util/`)

Utility classes provide common functionality across the application.

#### Database (`util/database/`)
- **`DBConnection.java`**: Database connection management and configuration
- **`DatabaseInitializer.java`**: Database schema creation and initialization

#### Security (`util/security/`)
- **`PasswordUtil.java`**: Password hashing, verification, and strength validation

#### UI (`util/ui/`)
- **`InputValidator.java`**: User input validation and formatting
- **`DisplayFormatter.java`**: Consistent data display formatting
- **`PaginationUtil.java`**: Pagination logic for large data sets (10 items per page)

#### Algorithms (`util/algorithms/`)
- **`SortingAlgorithms.java`**: Various sorting implementations for data management
- **`OrderQueueManager.java`**: Advanced queue management for order processing

### 🚀 Main Application (`app/src/main/java/com/bookstore/main/`)

#### `Main.java`
- **Purpose**: Application entry point and initialization
- **Key Functions**:
  - Database initialization
  - Default admin user creation
  - Main application loop
  - Error handling and graceful shutdown

### 🧪 Tests (`app/src/test/java/com/bookstore/`)

Comprehensive unit tests for system components.

#### Test Categories:
- **DAO Tests**: Database operation testing
- **Service Tests**: Business logic validation
- **Utility Tests**: Helper function verification
- **Integration Tests**: Component interaction testing

## 🔄 System Workflow

### Customer Workflow:
1. **Login/Signup** → Authentication
2. **Browse/Search Books** → Paginated book display
3. **Add to Cart** → Shopping cart management
4. **Manage Cart** → Update quantities, remove items
5. **Checkout** → Order creation
6. **Order Tracking** → View order history and status

### Admin Workflow:
1. **Login** → Admin authentication
2. **Manage Books** → CRUD operations with pagination
3. **Manage Customers** → Customer administration
4. **Process Orders** → Order queue management
5. **System Administration** → User and system management

## 🔐 Security Features

- **Password Hashing**: Secure password storage using salt and hashing
- **Role-Based Access**: Different permissions for customers and admins
- **Session Management**: Secure user sessions with automatic logout
- **Input Validation**: Comprehensive input sanitization and validation

## 📊 Key Features

### For Customers:
- ✅ Book browsing with pagination (10 items per page)
- ✅ Advanced book search functionality
- ✅ Shopping cart with persistent session storage
- ✅ Order placement and tracking
- ✅ Profile management
- ✅ Secure authentication

### For Administrators:
- ✅ Complete book inventory management
- ✅ Customer management
- ✅ Order processing and queue management
- ✅ User account creation and management
- ✅ System analytics and reporting
- ✅ Advanced data sorting and filtering

### Technical Features:
- ✅ Pagination system (10 items per page)
- ✅ Shopping cart functionality
- ✅ Secure authentication system
- ✅ Database integration with MySQL
- ✅ MVC architecture pattern
- ✅ Comprehensive error handling
- ✅ Unit testing framework
- ✅ Modular and extensible design

## 🗄️ Database Schema

The system uses MySQL database with the following main tables:
- **Users**: User authentication and basic info
- **Books**: Book inventory and details
- **Customers**: Customer-specific information
- **Orders**: Order management
- **OrderItems**: Order line items

## 🔧 Build and Configuration

- **Build Tool**: Gradle
- **Java Version**: Java 21
- **Database**: MySQL
- **Testing**: JUnit 5
- **Architecture**: MVC Pattern with Service Layer

## 📋 Detailed File Descriptions

### Core Application Files

#### `Main.java` - Application Bootstrap
```java
Location: app/src/main/java/com/bookstore/main/Main.java
Purpose: System initialization and main application loop
Key Responsibilities:
- Database connection verification
- Schema initialization
- Default admin user creation (username: admin, password: admin123)
- Main menu navigation loop
- Graceful error handling and system shutdown
```

#### `AuthService.java` - Authentication Core
```java
Location: app/src/main/java/com/bookstore/service/AuthService.java
Purpose: Central authentication and session management
Key Methods:
- login(username, password): User authentication
- logout(): Session cleanup and cart clearing
- getCurrentUser(): Session user retrieval
- changePassword(): Secure password updates
- registerUser(): New user creation with role assignment
- getCartService(): Shopping cart access
```

#### `CartService.java` - Shopping Cart Engine
```java
Location: app/src/main/java/com/bookstore/service/CartService.java
Purpose: Session-based shopping cart management
Key Features:
- addToCart(): Stock validation and quantity management
- removeFromCart(): Item removal with confirmation
- updateQuantity(): Dynamic quantity updates
- viewCart(): Formatted cart display with totals
- transferToOrder(): Cart-to-order conversion
- clearCart(): Session cleanup
```

### Database Layer (DAO Pattern)

#### `BookDAO.java` - Book Data Management
```java
Location: app/src/main/java/com/bookstore/dao/BookDAO.java
Database Tables: Books
Key Operations:
- getAllBooks(): Retrieves all books for pagination
- searchBooks(term): Title/author search functionality
- getBookById(id): Single book retrieval
- addBook(book): New book creation with validation
- updateBook(book): Book information updates
- deleteBook(id): Safe book deletion
- updateStock(id, quantity): Inventory management
```

#### `UserDAO.java` - User Authentication Data
```java
Location: app/src/main/java/com/bookstore/dao/UserDAO.java
Database Tables: Users
Key Operations:
- addUser(user): User creation with role assignment
- getUserByUsername(username): Login authentication
- updateUser(user): Profile updates
- changePassword(userId, hashedPassword): Secure password updates
- updateLastLogin(userId): Session tracking
```

#### `OrderDAO.java` - Order Processing Data
```java
Location: app/src/main/java/com/bookstore/dao/OrderDAO.java
Database Tables: Orders, OrderItems
Key Operations:
- addOrder(order): Order creation with transaction management
- getOrderById(id): Order retrieval with items
- getOrdersByCustomerId(customerId): Customer order history
- updateOrderStatus(id, status): Order lifecycle management
- getAllOrders(): Admin order management
```

### User Interface Controllers

#### `OrderManagementController.java` - Customer Experience
```java
Location: app/src/main/java/com/bookstore/controller/OrderManagementController.java
Purpose: Customer-facing book and order operations
Key Features:
- showCustomerBrowseBooks(): Paginated book browsing (10 items/page)
- showCustomerSearchBooks(): Search with pagination
- showCartManagement(): Full cart CRUD operations
- showCustomerPlaceOrder(): Order creation workflow
- showCustomerViewOrders(): Order history with status tracking
```

#### `BookManagementController.java` - Admin Book Operations
```java
Location: app/src/main/java/com/bookstore/controller/BookManagementController.java
Purpose: Administrative book management
Key Features:
- viewAllBooks(): Paginated admin book view (10 items/page)
- addNewBook(): Book creation with validation
- updateBook(): Book information management
- deleteBook(): Safe deletion with confirmation
- updateStock(): Inventory management
- demonstrateSorting(): Algorithm demonstrations
```

### Utility Systems

#### `PaginationUtil.java` - Pagination Engine
```java
Location: app/src/main/java/com/bookstore/util/ui/PaginationUtil.java
Purpose: Universal pagination for large datasets
Key Features:
- getPage(items, pageNumber, pageSize): Page extraction
- getTotalPages(totalItems, pageSize): Page calculation
- displayPaginationInfo(): Consistent page headers
- handleNavigationInput(): Command processing (N, P, F, L, G)
- validatePageNumber(): Boundary checking
Navigation Commands:
- N/NEXT: Next page
- P/PREV: Previous page
- F/FIRST: First page
- L/LAST: Last page
- G/GO: Go to specific page
- 1-9: Direct page number
```

#### `PasswordUtil.java` - Security Implementation
```java
Location: app/src/main/java/com/bookstore/util/security/PasswordUtil.java
Purpose: Secure password management
Key Features:
- hashPassword(password): Salt-based hashing
- verifyPassword(password, hash): Secure verification
- isPasswordStrong(password): Strength validation
- getPasswordRequirements(): User guidance
Security: Uses SHA-256 with random salt generation
```

#### `DisplayFormatter.java` - UI Consistency
```java
Location: app/src/main/java/com/bookstore/util/ui/DisplayFormatter.java
Purpose: Consistent data presentation
Key Features:
- displayBooksTable(books): Formatted book listings
- displayOrdersTable(orders): Order history formatting
- displayCustomersTable(customers): Customer management views
- displayOrderSummary(items, total): Checkout summaries
- displayOrderDetails(order): Detailed order views
```

### Data Models

#### `User.java` - Base User Class
```java
Location: app/src/main/java/com/bookstore/model/User.java
Purpose: Abstract base for all user types
Key Features:
- Abstract hasPermission(permission): Role-based access
- getFullName(): Name formatting
- isActive(): Account status checking
- Role-based inheritance (Admin, Customer)
```

#### `Book.java` - Book Entity
```java
Location: app/src/main/java/com/bookstore/model/Book.java
Purpose: Book data representation
Properties:
- bookId, title, author, isbn, price
- stockQuantity, category, description
- publicationYear, dateAdded
Key Features:
- Complete book information management
- Stock tracking capabilities
- Search optimization
```

#### `Order.java` & `OrderItem.java` - Order System
```java
Location: app/src/main/java/com/bookstore/model/Order.java
Purpose: Order lifecycle management
Key Features:
- Order status tracking (PENDING → PROCESSING → SHIPPED → DELIVERED)
- Customer association
- Total amount calculations
- Shipping and delivery tracking
- Order item composition
```

## 🔄 System Integration Points

### Authentication Flow:
1. **Login** → AuthService.login() → UserDAO.getUserByUsername()
2. **Session** → AuthService.getCurrentUser() → Role-based menu display
3. **Logout** → AuthService.logout() → CartService.clearCart()

### Shopping Flow:
1. **Browse** → BookDAO.getAllBooks() → PaginationUtil.getPage()
2. **Add to Cart** → CartService.addToCart() → Stock validation
3. **Checkout** → CartService.transferToOrder() → OrderService.createNewOrder()

### Admin Flow:
1. **Book Management** → BookDAO operations → DisplayFormatter output
2. **Order Processing** → OrderDAO.getAllOrders() → Status management
3. **User Management** → UserDAO operations → Role assignment

## 🔧 Technical Specifications

### System Requirements
- **Java Version**: Java 21 or higher
- **Database**: MySQL 8.0 or higher
- **Build Tool**: Gradle 7.0+
- **Memory**: Minimum 512MB RAM
- **Storage**: 100MB for application + database storage

### Database Configuration
```sql
Database Name: online_bookstore_db
Default Admin: username=admin, password=admin123
Connection: localhost:3306 (configurable in DBConnection.java)
Tables: Users, Books, Customers, Orders, OrderItems
```

### Application Configuration
```java
Pagination: 10 items per page (configurable in controllers)
Session Management: In-memory cart storage per user session
Password Security: SHA-256 with salt
Input Validation: Comprehensive validation for all user inputs
Error Handling: Graceful error recovery with user feedback
```

## 📖 Usage Examples

### Customer Operations
```
1. Login as Customer:
   - Username: [customer_username]
   - Password: [customer_password]

2. Browse Books (Paginated):
   - View 10 books per page
   - Navigate: N (next), P (previous), F (first), L (last)
   - Go to specific page: G → enter page number

3. Search Books:
   - Enter search term (title or author)
   - Results displayed with pagination
   - Same navigation commands available

4. Shopping Cart:
   - Add books from browse/search
   - View cart with totals
   - Update quantities or remove items
   - Proceed to checkout

5. Place Order:
   - Review cart contents
   - Confirm order details
   - Order created with tracking ID
```

### Admin Operations
```
1. Login as Admin:
   - Username: admin
   - Password: admin123

2. Book Management:
   - View all books (paginated, 10 per page)
   - Add new books with full details
   - Update existing book information
   - Manage stock quantities
   - Delete books (with confirmation)

3. Customer Management:
   - View all customers
   - Search customers by ID
   - Create new customer accounts

4. Order Management:
   - View all orders in system
   - Process pending orders
   - Update order statuses
   - Track order queue
```

### Navigation Commands (Pagination)
```
Available in all paginated views:
- N, NEXT: Go to next page
- P, PREV: Go to previous page
- F, FIRST: Go to first page
- L, LAST: Go to last page
- G, GO: Go to specific page (prompts for page number)
- 1-9: Direct page number input
- Regular menu numbers: Execute menu options
```

## 🧪 Testing Framework

### Test Structure
```
app/src/test/java/com/bookstore/
├── dao/                    # DAO layer tests
├── service/                # Service layer tests
├── util/                   # Utility function tests
└── integration/            # Integration tests
```

### Test Categories
- **Unit Tests**: Individual component testing
- **Integration Tests**: Component interaction testing
- **DAO Tests**: Database operation validation
- **Service Tests**: Business logic verification
- **Utility Tests**: Helper function validation

### Running Tests
```bash
./gradlew test                    # Run all tests
./gradlew test --tests "ClassName" # Run specific test class
./gradlew test --info            # Verbose test output
```

## 🚀 Deployment and Setup

### Initial Setup
1. **Database Setup**:
   ```sql
   CREATE DATABASE online_bookstore_db;
   -- Application will auto-create tables on first run
   ```

2. **Application Start**:
   ```bash
   ./gradlew run
   ```

3. **Default Admin Creation**:
   - System automatically creates admin user on first run
   - Username: admin, Password: admin123

### Adding Sample Data
```sql
-- Use the provided SQL script to add 50 sample books
-- Execute the INSERT statements in MySQL
```

## 🔍 Troubleshooting

### Common Issues
1. **Database Connection Failed**:
   - Check MySQL service is running
   - Verify connection details in DBConnection.java
   - Ensure database exists

2. **Login Issues**:
   - Default admin: admin/admin123
   - Check user exists in database
   - Verify password hashing

3. **Pagination Not Working**:
   - Ensure sufficient data (>10 items)
   - Check PaginationUtil implementation
   - Verify navigation command input

### Debug Mode
- Enable detailed logging in Main.java
- Check console output for error messages
- Verify database schema creation

## 📈 Performance Considerations

### Optimization Features
- **Pagination**: Limits memory usage with large datasets
- **Database Indexing**: Optimized queries for search operations
- **Session Management**: Efficient cart storage
- **Connection Pooling**: Database connection optimization

### Scalability
- **Modular Design**: Easy to extend with new features
- **Service Layer**: Business logic separation
- **DAO Pattern**: Database abstraction
- **MVC Architecture**: Clear separation of concerns

This comprehensive documentation provides everything needed to understand, deploy, and maintain the Bookstore Management System. The modular architecture ensures easy maintenance and future enhancements.
