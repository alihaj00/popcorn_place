package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.dto.BookingRequest;
import com.att.tdp.popcorn_palace.model.Booking;
import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.repository.BookingRepository;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    // Injecting required repository instances using Spring's dependency injection.
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ShowtimeRepository showtimeRepository;

    /**
     * Endpoint to book a ticket for a given showtime.
     * <p>
     * This method handles ticket booking by ensuring:
     * <ul>
     *   <li>The provided showtime ID exists.</li>
     *   <li>The requested seat is not already booked for that showtime.</li>
     * </ul>
     * If any condition fails, it returns an appropriate error response.
     * Otherwise, it creates a new booking and persists it to the database.
     * </p>
     *
     * @param request The booking request payload containing showtimeId, seatNumber, and userId.
     * @return A ResponseEntity containing either the persisted Booking or an error message.
     */
    @PostMapping
    public ResponseEntity<?> bookTicket(@Valid @RequestBody BookingRequest request) {
        // Attempt to retrieve the showtime associated with the given ID.
        Optional<Showtime> showtimeOpt = showtimeRepository.findById(request.getShowtimeId());

        // Validate that the showtime exists.
        if (showtimeOpt.isEmpty()) {
            // Return a 400 Bad Request if the showtime is not found.
            return ResponseEntity.badRequest().body("Invalid showtimeId: Showtime does not exist.");
        }
        Showtime showtime = showtimeOpt.get();

        // Check if the seat has already been booked for the retrieved showtime.
        if (bookingRepository.existsByShowtimeAndSeatNumber(showtime, request.getSeatNumber())) {
            // Return a 400 Bad Request if the seat is already taken.
            return ResponseEntity.badRequest().body("Seat already booked for this showtime. Please choose another seat.");
        }

        // Create a new booking instance and set its properties.
        Booking booking = new Booking();
        booking.setShowtime(showtime);
        booking.setSeatNumber(request.getSeatNumber());
        booking.setUserId(request.getUserId());

        // Save the new booking to the database.
        Booking savedBooking = bookingRepository.save(booking);

        // Return a 200 OK response with the saved booking details.
        return ResponseEntity.ok(savedBooking);
    }
}
