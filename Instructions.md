# Popcorn Palace Application Instructions

## 1. Overview

Popcorn Palace is a web application for managing movies, showtimes, and ticket bookings. It leverages Spring Boot, JPA/Hibernate, and Jakarta Validation to provide a robust REST API.

## 2. Prerequisites

Before you begin, ensure you have the following installed:
1. **Java Development Kit (JDK):** Version 11 or later.  
   **Note:** This project was built using Amazon Corretto 21.0.6, located .
2. **Maven Wrapper:** The project includes the Maven wrapper (`mvnw`), so you don't need to install Maven separately.
3. **Docker:** To run the application in a containerized environment.  
   **Important:** Ensure Docker Desktop is installed and running before proceeding.(https://docs.docker.com/desktop/setup/install/windows-install/)
4. **Postman:** Use Postman  to easily send requests and test your APIs .(https://www.postman.com/downloads/)







## 3. Setup

### 3.1. Clone the Repository
1. Open your terminal.
2. Run the following commands:

    ```bash
    git clone https://github.com/alihaj00/popcorn_palace.git
    cd popcorn_palace
    ```

### 3.2. Configure the Database
By default, the application uses an in-memory H2 database for development and testing.  
If you need to use a different database (e.g., MySQL, PostgreSQL), update the `application.properties` file in `src/main/resources` with the necessary connection details.

## 4. Build the Project

1. Open your terminal in the project directory.
2. Run the following command to clean, compile, run tests, and package the application:

    ```bash
    ./mvnw clean install
    ```

## 5. Running the Application (Starting the Server)

You can run the server using one of two methods:

### 5.1. Running with Docker Compose (Recommended)
1. **Start Docker Desktop:**  
   Open Docker Desktop and ensure it is running.
2. **Run the Application:**  
   In the project root (where the `docker-compose.yml` file is located), run:

    ```bash
    docker compose up
    ```

3. The application will start inside a container and will be accessible at `http://localhost:8080`.

### 5.2. Running Directly with the Maven Wrapper
If you prefer to run the server without Docker, you can start it directly:
1. Open your terminal in the project directory.
2. Run the following command:

    ```bash
    ./mvnw spring-boot:run
    ```

3. The application will start and listen on port **8080** by default. To change the port, update the `application.properties` file.

## 6. Testing the Application

To run the tests, use the following Maven wrapper command:

```bash
./mvnw test
