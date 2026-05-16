# Java Online Ordering System

A Java-based command-line ordering system built with object-oriented programming.

The system supports menu management, order creation, inventory control, discount calculation, order lookup, and CSV-based data persistence.

## Features

- Load and display menu items
- Create customer orders
- Add and manage order items
- Calculate total price automatically
- Apply discount strategies
- Search and manage orders
- Store order data with CSV-based persistence

## Tech Stack

- Java
- Object-Oriented Programming
- File I/O
- CSV data persistence
- Command-line interface

## Project Structure

```text
src/
├── Main.java
├── Category.java
├── Menu.java
├── MenuItem.java
├── Order.java
├── OrderItem.java
├── OrderManager.java
├── DiscountStrategy.java
├── OverAmountDiscount.java
└── PercentageDiscount.java
```

## Core Design

The project separates responsibilities into multiple classes:

- `Main`: controls the command-line interaction flow
- `Category`: defines menu item categories
- `Menu`: manages menu data
- `MenuItem`: represents each food item
- `Order`: stores order information
- `OrderItem`: represents items inside an order
- `OrderManager`: handles order operations
- `DiscountStrategy`: defines the discount interface
- `OverAmountDiscount`: applies discounts based on order amount
- `PercentageDiscount`: applies percentage-based discounts

## How to Run

```bash
javac *.java
java Main
```

## What I Learned

Through this project, I practiced Java OOP design, class separation, file reading and writing, command-line interaction, and basic business logic implementation.

## Future Improvements

- Add a graphical user interface
- Replace CSV files with a database
- Add user login and role management
- Add unit tests
