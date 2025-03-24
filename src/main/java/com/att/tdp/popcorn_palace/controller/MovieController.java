package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/movies")
public class MovieController {

    /**
     * Simple greeting endpoint.
     *
     * @return A greeting string.
     */
    @RequestMapping("/")
    public String getGreet() {
        return "Hello World";
    }

    // Injecting the MovieRepository instance using Spring's dependency injection.
    @Autowired
    private MovieRepository movieRepository;

    /**
     * Retrieve all movies from the database.
     *
     * @return A ResponseEntity containing a list of movies.
     */
    @GetMapping("/all")
    public ResponseEntity<List<Movie>> getAllMovies() {
        List<Movie> movies = movieRepository.findAll();
        // Log each movie for debugging purposes.
        for (Movie m : movies) {
            System.out.println(m);
        }
        return ResponseEntity.ok(movies);
    }

    /**
     * Add a new movie to the database.
     *
     * @param movie The Movie object to be added, validated against constraints.
     * @return A ResponseEntity containing the saved movie.
     */
    @PostMapping
    public ResponseEntity<Movie> addMovie(@Valid @RequestBody Movie movie) {
        // Save the new movie to the database.
        Movie savedMovie = movieRepository.save(movie);
        System.out.println("Saved movie: " + savedMovie);
        return ResponseEntity.ok(savedMovie);
    }

    /**
     * Update an existing movie based on its title.
     *
     * <p>
     * This endpoint searches for a movie using the provided title.
     * If found, it updates the movie's details with the values from the request.
     * If not found, it returns a 404 Not Found response.
     * </p>
     *
     * @param movieTitle   The title of the movie to be updated.
     * @param updatedMovie The movie details to be updated, validated against constraints.
     * @return A ResponseEntity containing the updated movie if found, or a 404 response if not.
     */
    @PostMapping("/update/{movieTitle}")
    public ResponseEntity<Movie> updateMovie(@PathVariable String movieTitle, @Valid @RequestBody Movie updatedMovie) {
        // Find the movie by its title.
        Optional<Movie> movieOpt = movieRepository.findByTitle(movieTitle);
        if (movieOpt.isPresent()) {
            Movie movie = movieOpt.get();
            // Update movie details
            movie.setTitle(updatedMovie.getTitle());
            movie.setGenre(updatedMovie.getGenre());
            movie.setDuration(updatedMovie.getDuration());
            movie.setRating(updatedMovie.getRating());
            movie.setReleaseYear(updatedMovie.getReleaseYear());
            // Save the updated movie back to the database.
            Movie savedMovie = movieRepository.save(movie);
            return ResponseEntity.ok(savedMovie);
        }
        // If the movie is not found, return a 404 Not Found response.
        return ResponseEntity.notFound().build();
    }

    /**
     * Delete a movie based on its title.
     *
     * <p>
     * This endpoint searches for a movie by its title. If the movie exists, it is deleted from the database.
     * If not found, a 404 Not Found response is returned.
     * </p>
     *
     * @param movieTitle The title of the movie to be deleted.
     * @return A ResponseEntity with status 200 OK if deletion is successful, or 404 if not found.
     */
    @DeleteMapping("/{movieTitle}")
    public ResponseEntity<Void> deleteMovie(@PathVariable String movieTitle) {
        // Find the movie by its title.
        Optional<Movie> movieOpt = movieRepository.findByTitle(movieTitle);
        if (movieOpt.isPresent()) {
            // Delete the found movie.
            movieRepository.delete(movieOpt.get());
            return ResponseEntity.ok().build();
        }
        // Return 404 Not Found if no movie matches the provided title.
        return ResponseEntity.notFound().build();
    }
}
