# ATM
🏧 Secure ATM Interface System

A robust, console-based banking simulation built with Core Java and MySQL. Designed to demonstrate secure database connectivity and Object-Oriented best practices.

🚀 Overview

The ATM Interface System is a backend application that simulates real-world banking operations. Unlike simple static projects, this system persists all data in a MySQL database, ensuring that user balances and transaction histories are saved even after the application closes.

It is architected using Object-Oriented Programming (OOP) principles—encapsulation for security, inheritance for account types, and polymorphism for transaction handling—making it scalable and modular.

✨ Key Features

Feature

Description

🔐 Secure Login

Authentication system verifying Account Number & PIN against the database.

🆕 Account Creation

Auto-generates unique Account IDs for new users.

💸 Cash Withdrawal

Real-time balance validation preventing overdrafts.

💰 Cash Deposit

Instantly updates account balance and logs the transaction.

📜 Mini Statement

Fetches the last 5 transactions (Date, Type, Amount) using SQL queries.

📊 Balance Inquiry

View current available funds securely.

❌ Delete Account

Remove user records permanently from the system.

🛠️ Tech Stack

Language: Java (JDK 17+)

Database: MySQL

Connectivity: JDBC (Java Database Connectivity)

Concepts: OOPs (Polymorphism, Inheritance, Encapsulation), Exception Handling, Collections.

⚙️ Installation & Setup

Follow these steps to run the project locally.

1. Database Setup

Open MySQL Workbench or your terminal and run the following SQL script to create the necessary tables:

CREATE DATABASE atm_db;
USE atm_db;

-- Account Table
CREATE TABLE accounts (
    account_number INT AUTO_INCREMENT PRIMARY KEY,
    holder_name VARCHAR(100) NOT NULL,
    pin VARCHAR(4) NOT NULL,
    balance DOUBLE DEFAULT 0.0
);

-- Transaction History Table
CREATE TABLE transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    account_number INT,
    type VARCHAR(20),
    amount DOUBLE,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_number) REFERENCES accounts(account_number) ON DELETE CASCADE
);


2. Configure Java Connection

Open ATMSystem.java and update the DatabaseHandler class with your MySQL credentials:

private static final String USER = "root";  // Your MySQL Username
private static final String PASS = "your_password"; // Your MySQL Password


3. Run the Application

Make sure you have the mysql-connector-j JAR file in your project folder.

Compile:

javac ATMSystem.java


Run (Windows):

java -cp "mysql-connector-j-8.x.x.jar;." ATMSystem


Run (Mac/Linux):

java -cp "mysql-connector-j-8.x.x.jar:." ATMSystem


📸 Usage Demo

Main Menu

=== JAVA ATM INTERFACE ===
1. Login
2. Create New Account
3. Exit
Select Option: 


Transaction History

--- Last 5 Transactions ---
2024-10-24 10:30:00 | DEPOSIT | $500.00
2024-10-24 10:35:00 | WITHDRAW | $200.00
---------------------------


👨‍💻 Author

Dhiraj Kumawat

https://www.linkedin.com/in/dhiraj-kumawat-16159b1b1/
