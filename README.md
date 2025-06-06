# BookStore

An Online Bookstore Application built with Spring Boot, featuring user authentication, book management, shopping cart functionality, order processing, and administrative capabilities with a RESTful API architecture.

## Features
- **User Authentication/Authorization**: User registration/login system using Spring Security with JWT tokens for authentication and role-based authorization (USER/ADMIN roles)
- **Book Catalog**: Comprehensive Book catalog for browsing books with advanced filtering and pagination
- **Shopping Cart**: Shopping cart system allowing users to add/remove books, update quantities and proceed to checkout
- **Order Processing**: Complete ordering system including order placement, status tracking, and order history
- **User Favorites**: Users are able to favorite and browse their preferred books
- **Admin Panel**: Administrative capabilities for ADMINs to manage books, carts, orders, and user accounts with role-based access control
- **RESTful API**: Comprehensive REST endpoints with OpenAPI/Swagger documentation

## Tech Stack
- **Framework**: Spring Boot 3.x with Spring MVC architecture
- **Security**: Spring Security with JWT-based authentication and authorization
- **Data Layer**:
  - Spring Data JPA
  - Dockerized PostgreSQL database
  - Flyway for database migrations
  - Testcontainers for integration testing
- **Build System**: Maven wrapper
- **Testing**: JUnit 5, Mockito, Testcontainers
- **Documentation**: OpenAPI 3.0 specification with Swagger UI integration
- **Code Quality**: 
  - Lombok and MapStruct for boilerplate reduction and mapping
  - JavaDoc for code documentation
  - Checkstyle for static code analysis
  - Spotless for code formatting

## Requirements
- **Java**: Java JDK 21
- **Docker Desktop**: Docker Desktop installed and running

## Installation
1. Clone the repository.
2. Launch Docker Desktop.
3. Run ```docker compose up -d``` in the terminal, to start up the PostgreSQL docker container.
4. Run the BookStoreApplication class in src/main/java/com/vagsoft/bookstore/BookStoreApplication.java.

## Usage
### 1. Clone the repository
    ```bash
    git clone https://github.com/Vag-Soft/BookStore.git
    cd bookstore
    ```

### 2. Start up database
    ```bash
    # Ensure Docker Desktop is running
    docker compose up -d
    # Run ```docker compose down``` to close and delete 
    # the main database instance, useful for testing
    ```
### 3. Run the application
    ```bash
    ./mvnw spring-boot:run

    # Or run the main class directly in your IDE:
    # src/main/java/com/vagsoft/bookstore/BookStoreApplication.java
    ```
### 4. Use the application
 - Send HTTP requests at ```http://localhost:8080```
 - Check the API docs with Swagger UI at ```http://localhost:8080/swagger-ui/index.html```
 - Use the default credentials to log in:
   - **Username**: `admin`
   - **Password**: `admin`


#### Testing
- Checkout to the testing branch before running the application for testing purposes.
- Run all unit and integration tests with:
    ```bash
    ./mvnw test
  
    # Or run tests directly in your IDE
    ```
- The database will be populated with some initial data the first time the application is executed (look at [this file](src/main/resources/db/migrations/dev/V1_0_1__initial_data.sql)).
- The unit and integration tests use another DB instance that is set up automatically and temporarily when the tests are run.
- Run ```docker compose down``` to close and delete the main database instance

## API Endpoints

### Authentication
- `POST /auth/register` - User registration
- `POST /auth/login` - User authentication

### Books
- `GET /books` - Browse books with pagination and filtering
- `GET /books/{id}` - Get specific book details
- `POST /books` - Add a new book (Admin only)
- `PUT /books/{id}` - Update a book (Admin only)
- `DELETE /books/{id}` - Delete a book (Admin only)

### Users
- `GET /users/me` - Get logged-in user profile
- `PUT /users/me` - Update logged-in user profile
- `DELETE /users/me` - Delete logged-in user account
- `GET /users` - Get users with pagination and filtering (Admin only)
- `GET /users/{id}` - Get user by ID (Admin only)
- `PUT /users/{id}` - Update user by ID (Admin only)
- `DELETE /users/{id}` - Delete user by ID (Admin only)

### Favourites
- `GET /users/me/favourites` - Get logged-in user's favourites
- `POST /users/me/favourites` - Add favourite for logged-in user
- `DELETE /users/me/favourites/{bookID}` - Delete favourite for logged-in user
- `GET /users/{userID}/favourites` - Get favourites for a specific user (Admin only)
- `POST /users/{userID}/favourites` - Add favourite for a specific user (Admin only)
- `DELETE /users/{userID}/favourites/{bookID}` - Delete favourite for a specific user (Admin only)

### Carts
- `GET /carts/me` - Get logged-in user's cart
- `GET /carts` - Get all carts with pagination (Admin only)
- `GET /carts/{userID}` - Get a specific cart by user ID (Admin only)

### Cart Items
- `GET /carts/me/items` - Get logged-in user's cart items
- `POST /carts/me/items` - Add cart item for logged-in user
- `GET /carts/me/items/{bookID}` - Get specific cart item for logged-in user
- `PUT /carts/me/items/{bookID}` - Update cart item for logged-in user
- `DELETE /carts/me/items/{bookID}` - Delete cart item for logged-in user
- `GET /carts/{userID}/items` - Get all cart items for a specific user (Admin only)
- `GET /carts/{userID}/items/{bookID}` - Get specific cart item for a user (Admin only)
- `PUT /carts/{userID}/items/{bookID}` - Update cart item for a specific user (Admin only)
- `DELETE /carts/{userID}/items/{bookID}` - Delete cart item for a specific user (Admin only)

### Orders
- `GET /orders/me` - Get logged-in user's orders with filtering (amount range, status) and pagination
- `POST /orders/me` - Place a new order for logged-in user
- `GET /orders/me/{orderID}` - Get specific order for logged-in user
- `GET /orders` - Get all orders with filtering (userID, amount range, status) and pagination (Admin only)
- `GET /orders/{orderID}` - Get specific order by ID (Admin only)
- `PUT /orders/{orderID}` - Update order by ID (Admin only)

### Order Items
- `GET /orders/me/{orderID}/items` - Get order items for logged-in user's specific order
- `GET /orders/me/{orderID}/items/{bookID}` - Get specific order item for logged-in user's order
- `GET /orders/{orderID}/items` - Get all order items for a specific order (Admin only)
- `GET /orders/{orderID}/items/{bookID}` - Get specific order item by order ID and book ID (Admin only)

For more details on the API endpoints, refer to the API docs ```http://localhost:8080/swagger-ui/index.html```.

## Database schema design
![BookStore](https://github.com/user-attachments/assets/7b89aeef-96d8-4ff0-bd61-542f3fd31a2c)
