# Bookstore Database Setup for XAMPP

## Prerequisites
- XAMPP installed and running
- MySQL/MariaDB service started in XAMPP
- phpMyAdmin accessible (usually at http://localhost/phpmyadmin)

## Setup Instructions

### Method 1: Using phpMyAdmin (Recommended for XAMPP)

1. **Start XAMPP Services**
   ```
   - Start Apache
   - Start MySQL
   ```

2. **Open phpMyAdmin**
   - Go to: http://localhost/phpmyadmin
   - Login (usually no password required for local XAMPP)

3. **Create Database**
   - Click "New" in the left sidebar
   - Database name: `online_bookstore_db`
   - Collation: `utf8mb4_unicode_ci`
   - Click "Create"

4. **Import Schema**
   - Select the `online_bookstore_db` database
   - Click "Import" tab
   - Choose file: `database/schema.sql`
   - Click "Go"

### Method 2: Using MySQL Command Line

1. **Open Command Prompt/Terminal**

2. **Navigate to MySQL bin directory** (if not in PATH)
   ```bash
   cd C:\xampp\mysql\bin
   ```

3. **Connect to MySQL**
   ```bash
   mysql -u root -p
   ```
   (Press Enter if no password is set)

4. **Run the schema script**
   ```sql
   source /path/to/your/project/database/schema.sql
   ```

### Method 3: Using MySQL Workbench

1. **Open MySQL Workbench**
2. **Create connection to localhost:3306**
3. **Open SQL script**: `database/schema.sql`
4. **Execute the script**

## Database Configuration

### Update Application Configuration

Update your `DBConnection.java` file if needed:

```java
private static final String URL = "jdbc:mysql://localhost:3306/online_bookstore_db";
private static final String USER = "root";
private static final String PASSWORD = ""; // Usually empty for XAMPP
```

### Default Credentials

After setup, you can login with:
- **Username**: `admin`
- **Password**: `admin123` (will be set by the application on first run)

## Database Structure

### Tables Created:
1. **Users** - Authentication and user management
2. **Books** - Book inventory and catalog
3. **Customers** - Customer information
4. **Orders** - Order management
5. **OrderItems** - Order line items
6. **OrderStatusHistory** - Audit trail for order changes
7. **InventoryTransactions** - Stock movement tracking
8. **UserSessions** - Session management (optional)

### Views Created:
- **OrderSummary** - Order details with customer info
- **LowStockBooks** - Books with low inventory
- **SalesSummary** - Daily sales summary

### Sample Data:
- 8 sample books (fiction and technology)
- 5 sample customers
- Default admin user

## Verification

### Check if setup was successful:

1. **In phpMyAdmin**:
   - Navigate to `online_bookstore_db`
   - You should see 8 tables
   - Check `Books` table - should have 8 sample books
   - Check `Users` table - should have 1 admin user

2. **Run the application**:
   ```bash
   ./gradlew run
   ```
   - Should connect to database successfully
   - Should show "Default admin user created" message (first run only)

## Troubleshooting

### Common Issues:

1. **Connection refused**
   - Make sure MySQL service is running in XAMPP
   - Check if port 3306 is available

2. **Access denied**
   - Verify username/password in `DBConnection.java`
   - For XAMPP, usually username is `root` with no password

3. **Database doesn't exist**
   - Make sure you created the database first
   - Check database name spelling

4. **Table doesn't exist**
   - Re-run the schema.sql script
   - Check for any SQL errors in phpMyAdmin

### Reset Database:
If you need to start fresh:
```sql
DROP DATABASE IF EXISTS online_bookstore_db;
```
Then re-run the schema.sql script.

## Security Notes

### For Production Use:
1. **Change default passwords**
2. **Create dedicated database user**:
   ```sql
   CREATE USER 'bookstore_app'@'localhost' IDENTIFIED BY 'secure_password';
   GRANT SELECT, INSERT, UPDATE, DELETE ON online_bookstore_db.* TO 'bookstore_app'@'localhost';
   FLUSH PRIVILEGES;
   ```
3. **Update connection string** to use the new user
4. **Enable SSL** for database connections
5. **Regular backups**

## Backup and Restore

### Create Backup:
```bash
mysqldump -u root -p online_bookstore_db > bookstore_backup.sql
```

### Restore from Backup:
```bash
mysql -u root -p online_bookstore_db < bookstore_backup.sql
```

## Performance Optimization

The schema includes:
- **Indexes** on frequently queried columns
- **Foreign key constraints** for data integrity
- **Triggers** for automatic inventory management
- **Views** for common queries
- **Stored procedures** for complex operations

## Next Steps

1. **Test the application** with the database
2. **Create additional users** through the admin interface
3. **Add more sample data** if needed
4. **Configure backup strategy** for production use

For any issues, check the application logs and MySQL error logs in XAMPP.