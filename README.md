# BookStore

An Online Bookstore Application designed for browsing, ordering and managing books.

## Features
- [x] **Browsing**: Search for books with advanced filtering and pagination.
- [x] **Book Management**: Add, update, or delete book details.
- [ ] **User Functionality**: Add, update or delete users.
- [ ] **Shopping Cart**: Add books to a cart and proceed to checkout.
- [ ] **Order Management**: Place and track orders.
- [ ] **Authentication**: Secure user login and registration system.

## Requirements
- **Java**: Java JDK 21
- **Docker Desktop**: Docker Desktop installed and open

## Installation
1. Clone the repository.
2. Launch Docker Desktop.
3. Run ```docker compose up -d``` in the IntelliJ terminal, to start up the PostgreSQL docker container.
4. Run the BookStoreApplication class in src/main/java/com/vagsoft/bookstore/BookStoreApplication.java.

## Usage
1. Launch Docker Desktop
2. Run ```docker compose up -d``` in the IntelliJ terminal, to start up the PostgreSQL docker container.
3. Run the BookStoreApplication class in src/main/java/com/vagsoft/bookstore/BookStoreApplication.java.
4. Send HTTP requests at ```http://localhost:8080```.
5. Check the API docs with Swagger UI at ```http://localhost:8080/swagger-ui/index.html```.

## Database schema design
![BookStore](https://github.com/user-attachments/assets/7b89aeef-96d8-4ff0-bd61-542f3fd31a2c)

## Useful Information
- The database will be populated with some initial data the first time the application is executed (look at [this file](src/main/resources/db/migrations/dev/V1_0_1__initial_data.sql)).
- The unit and integration tests use another DB instance that is set up automatically and temporarily when the tests are run.
