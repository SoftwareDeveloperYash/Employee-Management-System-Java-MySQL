-- Run this in MySQL Workbench (File > Open SQL Script > Execute)

CREATE DATABASE IF NOT EXISTS ems_db;
USE ems_db;

CREATE TABLE IF NOT EXISTS employees (
    employee_id VARCHAR(20)   PRIMARY KEY,
    name        VARCHAR(100)  NOT NULL,
    father_name VARCHAR(100),
    contact     VARCHAR(20),
    email       VARCHAR(100),
    position    VARCHAR(100),
    salary      DECIMAL(12,2)
);

-- Optional: your existing record from file1234.txt
INSERT IGNORE INTO employees
    (employee_id, name, father_name, contact, email, position, salary)
VALUES
    ('1234', 'yashwanth', 'laxman', '234567856', 'y@gmail.com', 'SDE', 1300000);

SELECT * FROM employees;
