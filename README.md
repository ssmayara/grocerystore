🛒 Grocery Store – Pricing & Discount Engine

This project is a Grocery Store API built with Spring Boot, featuring a flexible pricing engine that supports multiple discount rules based on product type.
The goal of the challenge was to design a clean, extensible architecture capable of handling different pricing strategies while maintaining high code quality and good OO modeling practices.

The application runs using MariaDB inside Docker and automatically initializes the database with sample data on the first startup.

🚀 Features
✔ Product Management

* Create and retrieve products
* Products have category, unit type, and price
* Supports multiple discount rules per product

✔ Order & Order Item processing

* Create an order consisting of one or more items

Each item calculates:

* Unit price
* Base line total
* Discount applied
* Final line total

✔ Automatic Discount Engine

Discounts are applied automatically based on product category and configured rules.

Supported discount rules:

🥦 Vegetables — Weight-based percentage discounts

Discount applied based on weight range (grams)

Example:

* 0–100g → 5%
* 101–500g → 7%
* 501g+ → 10%

🍞 Bread — “Buy X Take Y” depending on bread age

Discount based on how many days old the bread is

Example:

* Between 3–5 days old → buy 1 take 2
* 6 days old → buy 1 take 3

🍺 Beer — Fixed amount discount per pack

Discount applied when purchasing a full pack (6 units)

Depends on country of origin

Example:

* Belgium → $3.00 discount per pack
* Netherlands → $2.00
* Germany → $4.00

✔ Strategy Pattern Architecture

The discount logic is implemented using the Strategy Pattern, allowing new discount types to be added easily without modifying the core OrderItem service.

Database Initialization

The application uses MariaDB in Docker, and the database is automatically populated on first startup using SQL scripts.

🔹 What gets created automatically?

All tables:

* product
* orders
* order_item
* discount
* product_discount

🧪 Running the Application
🔧 Prerequisites

1. Before running the application, make sure you have:

* Java 21+
* Maven 3.8+
* Docker (to run the MariaDB container)
* An IDE such as IntelliJ, Eclipse, or VS Code

The MariaDB database starts automatically using Docker Compose, and the Spring Boot application can be launched directly from your IDE.

🗄 Database Access

MariaDB runs automatically through Docker using the following credentials:

* Database: mydatabase
* User: myuser
* Password: secret

These values are already configured in application.properties.

🔍 Accessing the Database via Adminer (Recommended)

You can easily browse and query the database using Adminer, a lightweight web-based SQL client.
If Adminer is running in your Docker environment, access it at:


```bash
http://localhost:8090
```

Use the following connection settings:

* System: MariaDB
* Server: mariadb
* Username: myuser
* Password: secret
* Database: mydatabase

This allows you to view tables, execute SQL queries, and inspect the seeded data loaded at startup.

2. Swagger UI available at:
Interactive API documentation:

```bash
http://localhost:8090/swagger-ui.html
```

🧪 Example Request – Get All Products

You can retrieve all products using the following curl command:

```bash
curl --location --request GET 'http://localhost:8090/api/products'
```

This endpoint returns the full list of products preloaded into the database during initialization.

🧪 Example Request – Create an Order

```bash
curl --location --request POST 'http://localhost:8090/api/orders' \
--header 'Content-Type: application/json' \
--data-raw '{
    "items": [
        {
            "productId": 7,
            "quantity": 6
        },
        {
            "productId": 5,
            "weightGrams": 200
        },
        {
            "productId": 2,
            "quantity": 3
        }
    ]
}'
```

This request demonstrates:

* Buying 6 beers (triggers beer pack discount)
* Buying 200g of a vegetable (percentage discount by weight)
* Buying 3 breads (may trigger buy/take discount depending on age)

📦 Example Response – Create Order

A successful request to POST /api/orders returns an order summary with all calculated discounts:


```bash
{
    "orderId": 15,
    "total": 4.86,
    "items": [
        {
            "productName": "Beer Bottle Dutch",
            "quantity": 6,
            "weightGrams": null,
            "unitPrice": 0.50,
            "discountApplied": 2.00,
            "lineTotal": 1.00
        },
        {
            "productName": "Tomato",
            "quantity": 1,
            "weightGrams": 200,
            "unitPrice": 1.00,
            "discountApplied": 0.14,
            "lineTotal": 1.86
        },
        {
            "productName": "French Bread 3 days",
            "quantity": 3,
            "weightGrams": null,
            "unitPrice": 1.00,
            "discountApplied": 1.00,
            "lineTotal": 2.00
        }
    ]
}

```
