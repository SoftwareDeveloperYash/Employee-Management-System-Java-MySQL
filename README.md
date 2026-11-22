# Employee Management System (Java + MySQL)

A console-based Employee Management System built with Java and JDBC, storing data in a MySQL database.

## Features
- Add, view, update and remove employees
- View all employees in a formatted table
- Search employees by name
- Input validation (email, 10-digit contact, salary)
- Secure queries using PreparedStatement

## Tech stack
Java, JDBC, MySQL, IntelliJ IDEA

## Setup
1. Install MySQL and run schema.sql (in MySQL Workbench) to create the ems_db database and employees table.
2. Add the MySQL driver: com.mysql:mysql-connector-j:8.4.0
3. Copy db.properties.example to db.properties in the project root and enter your own MySQL password.
4. Run EmployManagementSystem from MainMenu.java.

## Menu
| Option | Action |
|--------|--------|
| 1 | Add an employee |
| 2 | See an employee's details |
| 3 | Remove an employee |
| 4 | Update employee details |
| 5 | View all employees |
| 6 | Search employees by name |
| 7 | Exit |

## Author
Yashwanth Bhukya