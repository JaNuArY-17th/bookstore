#!/bin/bash

echo "====================================================="
echo "Bookstore Database Quick Setup for XAMPP (Linux/Mac)"
echo "====================================================="
echo

# Check if MySQL is running
if pgrep -x "mysqld" > /dev/null; then
    echo "✓ MySQL service is running"
else
    echo "✗ MySQL service is not running"
    echo "Please start XAMPP and ensure MySQL service is running"
    exit 1
fi

echo
echo "Setting up database..."
echo

# Find MySQL binary
MYSQL_PATH="/opt/lampp/bin/mysql"
if [ ! -f "$MYSQL_PATH" ]; then
    MYSQL_PATH="/Applications/XAMPP/xamppfiles/bin/mysql"
fi
if [ ! -f "$MYSQL_PATH" ]; then
    MYSQL_PATH="mysql"  # Try system PATH
fi

echo "Creating database and importing schema..."
$MYSQL_PATH -u root -p -e "CREATE DATABASE IF NOT EXISTS online_bookstore_db; USE online_bookstore_db; SOURCE schema.sql;"

if [ $? -eq 0 ]; then
    echo "✓ Database schema created successfully"
    echo
    echo "Do you want to import sample data? (y/N)"
    read -r choice
    if [[ "$choice" =~ ^[Yy]$ ]]; then
        echo "Importing sample data..."
        $MYSQL_PATH -u root -p online_bookstore_db < sample_data.sql
        if [ $? -eq 0 ]; then
            echo "✓ Sample data imported successfully"
        else
            echo "✗ Failed to import sample data"
        fi
    fi
else
    echo "✗ Failed to create database schema"
    echo "Please check the error messages above"
fi

echo
echo "Setup completed!"
echo "You can now run the bookstore application."
echo "Default login: admin / admin123"
echo