 Banking System

A full-stack banking system built with Spring Boot, MySQL, and vanilla JavaScript.

 Features

- Create and manage bank accounts
- Deposit, withdraw, and transfer funds with currency conversion (EGP, USD, EUR)
- Loan system with real monthly interest calculation and overdue penalties
- Admin panel — freeze/unfreeze accounts, view all accounts and active loans
- Transaction history per account
- Persistent data with MySQL

 Tech Stack

- **Backend** — Java, Spring Boot, Spring Data JPA
- **Database** — MySQL
- **Frontend** — HTML, CSS, JavaScript

 Project Structure
 src/main/java/com/banking/banking_system/
├── controller/    → REST API endpoints
├── service/       → Business logic
├── repository/    → Database access
└── model/         → Entities (BankAccount, Loan)

## How to Run

1. Install MySQL and create a database:
```sql
CREATE DATABASE banking_system;
```

2. Update `src/main/resources/application.properties` with your MySQL password

3. Run the Spring Boot application

4. Open `http://localhost:8080` in your browser

## Default Admin PIN
`1234`
