package com.att.tdp.popcorn_palace;

import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.repository.BookingRepository;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the Popcorn Palace application.
 * <p>
 * This class tests the Movie, Showtime, and Booking APIs by adding test data via endpoints
 * and then verifying CRUD operations as well as validation edge cases.
 * </p>
 */
@SpringBootTest
@AutoConfigureMockMvc
public class PopcornPalaceApplicationTests {

	@Autowired
	private MovieRepository movieRepository;

	@Autowired
	private ShowtimeRepository showtimeRepository;

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	/**
	 * Global setup method that clears the database before each test.
	 * <p>
	 * This ensures that tests run in isolation and that there are no interference from leftover data.
	 * </p>
	 */
	@BeforeEach
	public void globalSetup() {
		if (bookingRepository != null) {
			bookingRepository.deleteAll();
		}
		if (showtimeRepository != null) {
			showtimeRepository.deleteAll();
		}
		if (movieRepository != null) {
			movieRepository.deleteAll();
		}
	}

	// --------------------------
	// Helper methods to add test data via endpoints
	// --------------------------

	/**
	 * Helper method to add a movie via the API.
	 *
	 * @param baseTitle   The base title of the movie.
	 * @param genre       The genre of the movie.
	 * @param duration    The duration in minutes.
	 * @param rating      The movie's rating.
	 * @param releaseYear The release year.
	 * @return The created Movie object.
	 * @throws Exception if the API call fails.
	 */
	private Movie addTestMovie(String baseTitle, String genre, int duration, double rating, int releaseYear) throws Exception {
		Movie movie = new Movie();
		// Append a unique suffix to avoid duplicate title conflicts.
		String uniqueTitle = baseTitle + "_" + System.currentTimeMillis();
		movie.setTitle(uniqueTitle);
		movie.setGenre(genre);
		movie.setDuration(duration);
		movie.setRating(rating);
		movie.setReleaseYear(releaseYear);

		String response = mockMvc.perform(post("/movies")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(movie)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title", is(uniqueTitle)))
				.andReturn().getResponse().getContentAsString();

		return objectMapper.readValue(response, Movie.class);
	}

	/**
	 * Helper method to add a showtime via the API.
	 *
	 * @param movieId   The ID of the movie associated with the showtime.
	 * @param theater   The theater where the showtime takes place.
	 * @param startTime The start time of the showtime.
	 * @param endTime   The end time of the showtime.
	 * @param price     The price of the showtime.
	 * @return A Map representing the JSON response of the created showtime.
	 * @throws Exception if the API call fails.
	 */
	private Map<String, Object> addTestShowtime(Long movieId, String theater, LocalDateTime startTime, LocalDateTime endTime, double price) throws Exception {
		Map<String, Object> payload = new HashMap<>();
		payload.put("movieId", movieId);
		payload.put("theater", theater);
		// Truncate times to seconds to avoid precision mismatches.
		payload.put("startTime", startTime.truncatedTo(ChronoUnit.SECONDS));
		payload.put("endTime", endTime.truncatedTo(ChronoUnit.SECONDS));
		payload.put("price", price);

		String response = mockMvc.perform(post("/showtimes")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(payload)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.theater", is(theater)))
				.andReturn().getResponse().getContentAsString();

		return objectMapper.readValue(response, Map.class);
	}

	/**
	 * Helper method to build a booking payload.
	 *
	 * @param showtimeId The ID of the showtime to book.
	 * @param seatNumber The seat number to book.
	 * @param userId     The ID of the user making the booking.
	 * @return A Map representing the booking payload.
	 */
	private Map<String, Object> buildBookingPayload(Integer showtimeId, int seatNumber, String userId) {
		Map<String, Object> payload = new HashMap<>();
		payload.put("showtimeId", showtimeId);
		payload.put("seatNumber", seatNumber);
		payload.put("userId", userId);
		return payload;
	}

	// --------------------------
	// Integration Tests for Movie API
	// --------------------------
	@Nested
	class MovieControllerTests {

		/**
		 * Test adding multiple movies and then retrieving them.
		 *
		 * @throws Exception if an API call fails.
		 */
		@Test
		public void testAddMultipleMoviesAndList() throws Exception {
			// Add two movies.
			Movie movie1 = addTestMovie("Inception", "Sci-Fi", 148, 8.8, 2010);
			Movie movie2 = addTestMovie("Matrix", "Sci-Fi", 136, 8.7, 1999);

			// Retrieve all movies and verify both exist.
			mockMvc.perform(get("/movies/all"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$", hasSize(2)))
					.andExpect(jsonPath("$[?(@.title=='" + movie1.getTitle() + "')]").exists())
					.andExpect(jsonPath("$[?(@.title=='" + movie2.getTitle() + "')]").exists());
		}

		/**
		 * Test updating a movie that does not exist.
		 *
		 * @throws Exception if an API call fails.
		 */
		@Test
		public void testUpdateNonExistentMovie() throws Exception {
			// Attempt to update a movie that doesn't exist.
			Movie updatedMovie = new Movie();
			updatedMovie.setTitle("NonExistent Updated");
			updatedMovie.setGenre("Drama");
			updatedMovie.setDuration(120);
			updatedMovie.setRating(7.0);
			updatedMovie.setReleaseYear(2005);

			mockMvc.perform(post("/movies/update/NonExistent")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(updatedMovie)))
					.andExpect(status().isNotFound());
		}

		/**
		 * Test deleting a movie that does not exist.
		 *
		 * @throws Exception if an API call fails.
		 */
		@Test
		public void testDeleteNonExistentMovie() throws Exception {
			// Attempt to delete a movie that does not exist.
			mockMvc.perform(delete("/movies/UnknownMovie"))
					.andExpect(status().isNotFound());
		}
	}

	// --------------------------
	// Integration Tests for Showtime API
	// --------------------------
	@Nested
	class ShowtimeControllerTests {

		/**
		 * Test the full lifecycle for a showtime: add, retrieve, update, and delete.
		 *
		 * @throws Exception if an API call fails.
		 */
		@Test
		public void testAddGetUpdateDeleteShowtime() throws Exception {
			// Create a movie.
			Movie movie = addTestMovie("Inception", "Sci-Fi", 148, 8.8, 2010);
			Long movieId = movie.getId();
			LocalDateTime startTime = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS);
			LocalDateTime endTime = startTime.plusHours(2).truncatedTo(ChronoUnit.SECONDS);

			// Add a showtime.
			Map<String, Object> showtimeResponse = addTestShowtime(movieId, "Theater 1", startTime, endTime, 20.0);
			Integer showtimeId = (Integer) showtimeResponse.get("id");

			// Get the showtime by ID.
			mockMvc.perform(get("/showtimes/" + showtimeId))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.theater", is("Theater 1")));

			// Update the showtime: change theater, price, and times.
			Map<String, Object> updatePayload = new HashMap<>();
			updatePayload.put("movieId", movieId);
			updatePayload.put("theater", "Theater 2");
			updatePayload.put("price", 25.0);
			LocalDateTime newStart = startTime.plusDays(1).truncatedTo(ChronoUnit.SECONDS);
			LocalDateTime newEnd = newStart.plusHours(2).truncatedTo(ChronoUnit.SECONDS);
			updatePayload.put("startTime", newStart);
			updatePayload.put("endTime", newEnd);

			mockMvc.perform(post("/showtimes/update/" + showtimeId)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(updatePayload)))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.theater", is("Theater 2")))
					.andExpect(jsonPath("$.price", is(25.0)));

			// Delete the showtime by its ID.
			mockMvc.perform(delete("/showtimes/" + showtimeId))
					.andExpect(status().isOk());

			// Verify deletion: GET should return 404.
			mockMvc.perform(get("/showtimes/" + showtimeId))
					.andExpect(status().isNotFound());
		}

		/**
		 * Test the edge case where a new showtime overlaps with an existing one in the same theater.
		 *
		 * @throws Exception if an API call fails.
		 */
		@Test
		public void testOverlappingShowtimeEdgeCase() throws Exception {
			// Create a movie.
			Movie movie = addTestMovie("Inception", "Sci-Fi", 148, 8.8, 2010);
			Long movieId = movie.getId();
			LocalDateTime startTime = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS);
			LocalDateTime endTime = startTime.plusHours(2).truncatedTo(ChronoUnit.SECONDS);

			// Add the first showtime.
			addTestShowtime(movieId, "Theater 1", startTime, endTime, 20.0);

			// Attempt to add a second showtime with overlapping times in the same theater.
			Map<String, Object> overlappingPayload = new HashMap<>();
			overlappingPayload.put("movieId", movieId);
			overlappingPayload.put("theater", "Theater 1");
			overlappingPayload.put("price", 25.0);
			overlappingPayload.put("startTime", startTime.plusMinutes(30));
			overlappingPayload.put("endTime", endTime.plusHours(1));

			mockMvc.perform(post("/showtimes")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(overlappingPayload)))
					.andExpect(status().isBadRequest())
					.andExpect(content().string("Showtime overlaps with an existing showtime in the same theater."));
		}

		/**
		 * Test deletion of a showtime by details using edge cases:
		 * <ul>
		 *     <li>Invalid start time format</li>
		 *     <li>Non-existent movie title</li>
		 *     <li>Successful deletion</li>
		 * </ul>
		 *
		 * @throws Exception if an API call fails.
		 */
		@Test
		public void testDeleteShowtimeByDetailsEdgeCases() throws Exception {
			// Create a movie and a showtime.
			Movie movie = addTestMovie("Inception", "Sci-Fi", 148, 8.8, 2010);
			Long movieId = movie.getId();
			LocalDateTime startTime = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS);
			LocalDateTime endTime = startTime.plusHours(2).truncatedTo(ChronoUnit.SECONDS);
			addTestShowtime(movieId, "Theater 1", startTime, endTime, 20.0);

			// Edge Case 1: Invalid startTime format.
			mockMvc.perform(delete("/showtimes/by-details")
							.param("movieTitle", movie.getTitle())
							.param("theater", "Theater 1")
							.param("startTime", "invalid-format"))
					.andExpect(status().isBadRequest())
					.andExpect(content().string(containsString("Invalid startTime format")));

			// Edge Case 2: Non-existing movie title.
			mockMvc.perform(delete("/showtimes/by-details")
							.param("movieTitle", "NonExistent")
							.param("theater", "Theater 1")
							.param("startTime", startTime.toString()))
					.andExpect(status().isBadRequest())
					.andExpect(content().string(containsString("Movie not found")));

			// Valid deletion by details.
			mockMvc.perform(delete("/showtimes/by-details")
							.param("movieTitle", movie.getTitle())
							.param("theater", "Theater 1")
							.param("startTime", startTime.toString()))
					.andExpect(status().isOk());
		}
	}

	// --------------------------
	// Integration Tests for Booking API
	// --------------------------
	@Nested
	class BookingControllerTests {

		/**
		 * Test booking a ticket successfully and then attempt to book the same seat to check for duplicates.
		 *
		 * @throws Exception if an API call fails.
		 */
		@Test
		public void testBookTicketSuccessAndDuplicateEdgeCase() throws Exception {
			// Create a movie and a showtime.
			Movie movie = addTestMovie("Inception", "Sci-Fi", 148, 8.8, 2010);
			Long movieId = movie.getId();
			LocalDateTime startTime = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS);
			LocalDateTime endTime = startTime.plusHours(2).truncatedTo(ChronoUnit.SECONDS);
			Map<String, Object> showtimeResponse = addTestShowtime(movieId, "Theater 1", startTime, endTime, 20.0);
			Integer showtimeId = (Integer) showtimeResponse.get("id");

			// Build a booking request.
			Map<String, Object> bookingRequest = new HashMap<>();
			bookingRequest.put("showtimeId", showtimeId);
			bookingRequest.put("seatNumber", 10);
			bookingRequest.put("userId", "user-123");

			// First booking should succeed.
			mockMvc.perform(post("/bookings")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(bookingRequest)))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.bookingId", notNullValue()));

			// Duplicate booking (same seat) should fail.
			mockMvc.perform(post("/bookings")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(bookingRequest)))
					.andExpect(status().isBadRequest())
					.andExpect(content().string(containsString("Seat already booked for this showtime")));
		}

		/**
		 * Test booking a ticket with an invalid (non-existent) showtime ID.
		 *
		 * @throws Exception if an API call fails.
		 */
		@Test
		public void testBookTicketInvalidShowtimeEdgeCase() throws Exception {
			// Build a booking request with a non-existent showtimeId.
			Map<String, Object> bookingRequest = new HashMap<>();
			bookingRequest.put("showtimeId", 9999);
			bookingRequest.put("seatNumber", 10);
			bookingRequest.put("userId", "user-123");

			mockMvc.perform(post("/bookings")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(bookingRequest)))
					.andExpect(status().isBadRequest())
					.andExpect(content().string(containsString("Invalid showtimeId")));
		}

		/**
		 * Test booking tickets for different seats to ensure that multiple bookings can coexist.
		 *
		 * @throws Exception if an API call fails.
		 */
		@Test
		public void testMultipleBookingsDifferentSeats() throws Exception {
			// Create a movie and a showtime.
			Movie movie = addTestMovie("Inception", "Sci-Fi", 148, 8.8, 2010);
			Long movieId = movie.getId();
			LocalDateTime startTime = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS);
			LocalDateTime endTime = startTime.plusHours(2).truncatedTo(ChronoUnit.SECONDS);
			Map<String, Object> showtimeResponse = addTestShowtime(movieId, "Theater 1", startTime, endTime, 20.0);
			Integer showtimeId = (Integer) showtimeResponse.get("id");

			// Booking for seat 10.
			Map<String, Object> bookingRequest1 = buildBookingPayload(showtimeId, 10, "user-123");
			// Booking for seat 11.
			Map<String, Object> bookingRequest2 = buildBookingPayload(showtimeId, 11, "user-456");

			mockMvc.perform(post("/bookings")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(bookingRequest1)))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.bookingId", notNullValue()));

			mockMvc.perform(post("/bookings")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(bookingRequest2)))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.bookingId", notNullValue()));
		}
	}
}
