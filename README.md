![book-store.jpg](book-store.jpg)
# Online-Book-Store

## Introduction
The online-book-store is a Java-based web application built using the Spring Boot framework. It serves as a comprehensive platform for managing books, categories, user registrations, shopping carts, and orders within a bookstore. This project was inspired by the need for an efficient and scalable solution to streamline bookstore operations.

## Technologies Used
* **Spring Boot**: Is a powerful framework for building production-grade Java Applications.
* **Spring Security:** Is a powerful and highly customizable authentication and access-control 
  framework.
* **Spring Data JPA:** Simplifies the implementation of JPA-based (Java Persistence API) repositories.
* **Hibernate:** ORM tool for Java applications.
* **MySQL:** A relational database management system.
* **Liquibase:** Database schema migration tool.
* **Docker:** Docker helps developers build, share, run, and verify applications in containers.
* **Lombok:** A library to reduce boilerplate code in Java.
* **MapStruct:** Implements mapping between DTOs and entity models.
* **Swagger:** Tool for API documentation and testing.

## Functionalities
### User Management
* **User registration.**
  ``` 
  POST: /api/auth/registration
  ``` 
  Example of request body to register user:
  ```json
  {
    "email": "john@mail.com",
    "password": "password1234",
    "repeatPassword": "password1234",
    "firstName": "John",
    "lastName": "Doe",
    "shippingAddress": "1234 Main St, City"
  }
  ```
  
* **Secure user login with JWT-based authentication.**
  ``` 
  POST: /api/auth/login
  ``` 

  Example of request body to do log-in:
  ```json
  {
  "email": "john@mail.com",
  "password": "password1234"
  }
  ```

  Example of response body after successful log-in. Use generated JWT token in the Authorization 
  header in 
  your requests.
  ```json
  {
  "token": "eyJhbGci .... .... eoWbArcG7o-CNQO2Jo"
  }
  ```

### Book Management
* **Create, retrieve, update, and delete books.**
* **Search for books based on various parameters.**
* **Associate books with multiple categories.**
<br/><br/>
**Available endpoints for Book Management**

  with USER role
  ``` 
  GET: /api/books
  
  GET: /api/books/{id} 
  
  GET: /api/books/search
  ```

  with ADMIN role
  ``` 
  POST: /api/books/
  
  DELETE: /api/books/{id}
  
  PUT: /api/books/{id}
  ``` 

  Example of request body to **create new book**:
  ```json
  {
    "title": "Book title",
    "author": "Book author",
    "price": "100", 
    "description": "Description for book",
    "coverImage": "Book image",
    "isbn": "978-1-4028-9462-6",
    "categoryIds": [1, 2]
  }
  ```
  If you want to add category to book, you should crate category first, or update book later. Field categoryIds is an optional field.

  To update Book you should use same request body as for creation of a new book.

### Category Management
* **Create, retrieve, update, and delete book categories.**
  <br/><br/>
  **Available endpoints for Category Management**

  with USER role
  ``` 
  GET: /api/categories
  
  GET: /api/categories/{id}
  
  GET: /api/categories/{id}/books
  ``` 
  
  with ADMIN role
  ``` 
  POST: /api/categories
  
  PUT: /api/categories/{id}
  
  DELETE: /api/categories/{id}
  ``` 

  Example of request body to **create new category**:
  ```json
  {
    "name": "Category name",
    "description": "Category description"
  }
  ```

### Shopping Cart and Order Management

* Add books to the shopping cart.
* View and manage shopping cart items.
* Place orders, update order status, and retrieve order details.
  <br/><br/>
**Available endpoints for Shopping Cart Management**

  with USER role
  ```
  POST: /api/cart
  
  GET: /api/cart
  
  PUT: /api/cart/cart-items/{cartItemId}
  
  DELETE: /api/cart/cart-items/{cartItemId}
  ```
  Example of request body to **add items in cart**:
  ```json
  {
    "bookId": 1,
    "quantity": 1
  }
  ```
  
  Example of request body to **update book qty in cart**:
  ```json
  {
  "quantity": 1
  }
  ```

  ****Available endpoints for Order Management****

  with USER role
  ``` 
  POST: /api/orders
  
  GET: /api/orders
  
  GET: /api/orders/{orderId}/items
  
  GET: /api/orders/{orderId}/items/{itemId}
  ```
  Example of request body to **post order**:
  ```json
  {
  "shippingAddress": "1234 Main St, City"
  }
  ```
  with ADMIN role
  ```
  PUT: /api/orders/{id}
  ``` 
  Example of request body to **update order status**:
  ```json
  {
  "status": "CANCELED"
  }
  ```

## Project Structure
The project follows a modular structure:

* **model**: Models representing the business logic entities.
* **repository**: Spring Data JPA repositories.
* **service**: Business logic.
* **controller**: Controllers for handling HTTP requests.
* **dto**: Data Transfer Objects to send between the client and server.
* **mapper**: Mappers for mapping between DTOs and models.

## Setup

Clone the repository to your local machine.

Configure database settings in the application properties file.

Build and run the application.

To access Swagger documentation use link: http://localhost:8080/swagger-ui/index.html

The API uses JWT (JSON Web Tokens) for authentication.

To access protected endpoints first login to api, then include the generated JWT token in the Authorization header of your requests.

## Challenges and Solutions

Challenge: Implementing secure user authentication.

Solution: Utilized Spring Security and JWT for a robust authentication mechanism.

Challenge: Efficiently managing shopping carts and order processing.

Solution: Designed a ShoppingCartService and OrderService to handle cart operations and order management.

## Postman

For detailed API usage, you can use provided requests samples.

## Conclusion

The online-book-store is designed to offer a seamless experience for managing bookstore operations.