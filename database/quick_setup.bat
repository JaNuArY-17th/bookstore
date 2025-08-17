@echo off
echo =====================================================
echo Bookstore Database Quick Setup for XAMPP
echo =====================================================
echo.

echo Checking if MySQL is running...
tasklist /FI "IMAGENAME eq mysqld.exe" 2>NUL | find /I /N "mysqld.exe">NUL
if "%ERRORLEVEL%"=="0" (
    echo ✓ MySQL service is running
) else (
    echo ✗ MySQL service is not running
    echo Please start XAMPP and ensure MySQL service is running
    pause
    exit /b 1
)

echo.
echo Setting up database...
echo.

REM Navigate to MySQL bin directory (adjust path if needed)
set MYSQL_PATH=C:\xampp\mysql\bin
if not exist "%MYSQL_PATH%\mysql.exe" (
    echo MySQL not found at %MYSQL_PATH%
    echo Please update the MYSQL_PATH in this script
    pause
    exit /b 1
)

echo Creating database and importing schema...
"%MYSQL_PATH%\mysql.exe" -u root -p -e "CREATE DATABASE IF NOT EXISTS online_bookstore_db; USE online_bookstore_db; SOURCE schema.sql;"

if %ERRORLEVEL% EQU 0 (
    echo ✓ Database schema created successfully
    echo.
    echo Do you want to import sample data? (Y/N)
    set /p choice=
    if /i "%choice%"=="Y" (
        echo Importing sample data...
        "%MYSQL_PATH%\mysql.exe" -u root -p online_bookstore_db < sample_data.sql
        if %ERRORLEVEL% EQU 0 (
            echo ✓ Sample data imported successfully
        ) else (
            echo ✗ Failed to import sample data
        )
    )
) else (
    echo ✗ Failed to create database schema
    echo Please check the error messages above
)

echo.
echo Setup completed!
echo You can now run the bookstore application.
echo Default login: admin / admin123
echo.
pause