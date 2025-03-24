# Popcorn Palace

Popcorn Palace is a web application for managing movies, showtimes, and ticket bookings. Built with Spring Boot and JPA/Hibernate, the application provides a robust REST API with comprehensive validation and testing.

> 📄 **Note:** To install and run the application, please refer to [instructions.md]([instructions.md](https://github.com/alihaj00/popcorn_place/blob/main/Instructions.md)).

## API Endpoints

### Movies API

- **GET** `/movies/all`  
  Retrieve all movies.

- **POST** `/movies`  
  Add a new movie.

- **POST** `/movies/update/{movieTitle}`  
  Update an existing movie by title.

- **DELETE** `/movies/{movieTitle}`  
  Delete a movie by title.

### Showtimes API

- **POST** `/showtimes`  
  Add a new showtime (with overlapping validation).

- **GET** `/showtimes/{showtimeId}`  
  Retrieve a showtime by its ID.

- **POST** `/showtimes/update/{showtimeId}`  
  Update an existing showtime (with overlapping validation).

- **DELETE** `/showtimes/{showtimeId}`  
  Delete a showtime by its ID.

- **DELETE** `/showtimes/by-details`  
  Delete a showtime by movie title, theater, and start time (expects ISO-8601 formatted startTime).

### Booking API

- **POST** `/bookings`  
  Book a ticket for a showtime.  
  **Note:** Duplicate seat bookings for the same showtime are not allowed.

## Project Overview

This application manages movies, showtimes, and ticket bookings with the following key features:
- **Movies Management:** Create, update, retrieve, and delete movies.
- **Showtimes Management:** Schedule and manage showtimes with overlapping validation.
- **Booking System:** Book tickets while preventing duplicate seat reservations.
- **REST API:** A suite of endpoints to manage movies, showtimes, and bookings.
- **In-Memory Database:** Uses H2 for development and testing (configurable).
