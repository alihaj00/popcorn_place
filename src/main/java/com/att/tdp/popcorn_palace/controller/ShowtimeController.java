package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.dto.ShowtimeRequest;
import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/showtimes")
public class ShowtimeController {

    // Injecting the ShowtimeRepository to interact with showtime data
    @Autowired
    private ShowtimeRepository showtimeRepository;

    // Injecting the MovieRepository to validate and fetch associated movie details
    @Autowired
    private MovieRepository movieRepository;

    /**
     * Add a new showtime while validating that it does not overlap with any existing showtime in the same theater.
     *
     * @param request The showtime request payload, which includes movieId, theater, price, startTime, and endTime.
     * @return A ResponseEntity containing the saved showtime or an error message if validation fails.
     */
    @PostMapping
    public ResponseEntity<?> addShowtime(@Valid @RequestBody ShowtimeRequest request) {
        // Retrieve the movie associated with the provided movieId
        Optional<Movie> movieOpt = movieRepository.findById(request.getMovieId());
        if (movieOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid movieId");
        }

        // Validate that the new showtime does not overlap with any existing showtimes in the same theater
        List<Showtime> existingShowtimes = showtimeRepository.findByTheater(request.getTheater());
        for (Showtime s : existingShowtimes) {
            if (request.getStartTime().isBefore(s.getEndTime()) &&
                    request.getEndTime().isAfter(s.getStartTime())) {
                return ResponseEntity.badRequest()
                        .body("Showtime overlaps with an existing showtime in the same theater.");
            }
        }

        // Create and populate a new Showtime instance
        Showtime showtime = new Showtime();
        showtime.setMovie(movieOpt.get());
        showtime.setPrice(request.getPrice());
        showtime.setTheater(request.getTheater());
        showtime.setStartTime(request.getStartTime());
        showtime.setEndTime(request.getEndTime());

        // Persist the new showtime in the repository
        Showtime savedShowtime = showtimeRepository.save(showtime);
        System.out.println("Current showtime added: " + savedShowtime);
        return ResponseEntity.ok(savedShowtime);
    }

    /**
     * Retrieve a showtime by its ID.
     *
     * @param showtimeId The unique identifier of the showtime.
     * @return A ResponseEntity containing the showtime if found, or a 404 Not Found response if it does not exist.
     */
    @GetMapping("/{showtimeId}")
    public ResponseEntity<?> getShowtime(@PathVariable Long showtimeId) {
        Optional<Showtime> showtimeOpt = showtimeRepository.findById(showtimeId);
        System.out.println("Retrieved showtime: " + showtimeOpt);
        return showtimeOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Update an existing showtime by its ID while ensuring there is no overlapping with other showtimes.
     *
     * @param showtimeId The ID of the showtime to update.
     * @param request    The updated showtime details.
     * @return A ResponseEntity containing the updated showtime if successful, or an error response if validation fails.
     */
    @PostMapping("/update/{showtimeId}")
    public ResponseEntity<?> updateShowtime(@PathVariable Long showtimeId, @Valid @RequestBody ShowtimeRequest request) {
        // Fetch the current showtime to update
        Optional<Showtime> showtimeOpt = showtimeRepository.findById(showtimeId);
        if (showtimeOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Validate that the new timings do not overlap with other showtimes in the same theater (excluding the current showtime)
        List<Showtime> existingShowtimes = showtimeRepository.findByTheater(request.getTheater());
        for (Showtime s : existingShowtimes) {
            if (!s.getId().equals(showtimeId) &&
                    request.getStartTime().isBefore(s.getEndTime()) &&
                    request.getEndTime().isAfter(s.getStartTime())) {
                return ResponseEntity.badRequest()
                        .body("Showtime overlaps with an existing showtime in the same theater.");
            }
        }

        // Validate the provided movieId by fetching the corresponding Movie
        Optional<Movie> movieOpt = movieRepository.findById(request.getMovieId());
        if (movieOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid movieId");
        }

        // Update the showtime details
        Showtime showtime = showtimeOpt.get();
        showtime.setMovie(movieOpt.get());
        showtime.setPrice(request.getPrice());
        showtime.setTheater(request.getTheater());
        showtime.setStartTime(request.getStartTime());
        showtime.setEndTime(request.getEndTime());

        // Persist the updated showtime in the repository
        Showtime updatedShowtime = showtimeRepository.save(showtime);
        System.out.println("Updated showtime: " + updatedShowtime);
        return ResponseEntity.ok(updatedShowtime);
    }

    /**
     * Delete a showtime by its unique ID.
     *
     * @param showtimeId The ID of the showtime to be deleted.
     * @return A ResponseEntity indicating the result of the deletion operation.
     */
    @DeleteMapping("/{showtimeId}")
    public ResponseEntity<?> deleteShowtime(@PathVariable Long showtimeId) {
        // Check if the showtime exists before attempting deletion
        if (!showtimeRepository.existsById(showtimeId)) {
            return ResponseEntity.notFound().build();
        }
        System.out.println("Deleting showtime with ID: " + showtimeId);
        showtimeRepository.deleteById(showtimeId);
        return ResponseEntity.ok().build();
    }

    /**
     * Delete a showtime using movie title, theater, and start time.
     *
     * <p>
     * This endpoint allows deletion of a showtime by identifying it using a combination of movie title,
     * theater, and start time. The start time is expected in ISO-8601 format.
     * </p>
     *
     * @param movieTitle The title of the movie associated with the showtime.
     * @param theater    The theater where the showtime is scheduled.
     * @param startTime  The start time of the showtime in ISO-8601 format.
     * @return A ResponseEntity indicating success or the reason for failure.
     */
    @DeleteMapping("/by-details")
    public ResponseEntity<?> deleteShowtimeByDetails(
            @RequestParam String movieTitle,
            @RequestParam String theater,
            @RequestParam String startTime) {

        // Attempt to parse the startTime string into a LocalDateTime object
        LocalDateTime parsedStartTime;
        try {
            parsedStartTime = LocalDateTime.parse(startTime);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest()
                    .body("Invalid startTime format. Use ISO-8601 format (e.g., 2025-03-23T15:00:00).");
        }

        // Find the movie by its title to ensure it exists
        Optional<Movie> movieOpt = movieRepository.findByTitle(movieTitle);
        if (movieOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Movie not found for given title");
        }
        Movie movie = movieOpt.get();

        // Attempt to locate the showtime based on the movie, theater, and parsed start time
        Optional<Showtime> showtimeOpt = showtimeRepository.findByMovieAndTheaterAndStartTime(movie, theater, parsedStartTime);
        if (showtimeOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Delete the located showtime and return a success response
        showtimeRepository.delete(showtimeOpt.get());
        return ResponseEntity.ok().build();
    }
}
