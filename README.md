# Book Rental Service

A Spring Boot application that provides REST APIs for managing book rentals.

## Features

- Book inventory management
- Book borrowing and returning
- User management and authentication
- Transaction retry mechanism for concurrent operations

## Tech Stack

- Java 21
- Spring Boot 3.3.2
- PostgreSQL
- Docker
- Liquibase for database migrations

## Getting Started

### Prerequisites

- Java 21
- Maven
- Docker and Docker Compose

### Building the Application

```bash
mvn clean package -DskipTests
```

### Running the Application

```bash
docker-compose up --build
```

The service will be available at `http://localhost:8080`

## API Documentation

Swagger UI is available at `http://localhost:8080/swagger-ui.html`

## Database Schema

- `app_user`: User information
- `book`: Book catalog
- `inventory`: Book inventory and borrowing records