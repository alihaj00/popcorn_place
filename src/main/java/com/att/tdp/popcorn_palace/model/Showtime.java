package com.att.tdp.popcorn_palace.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Represents a showtime for a movie.
 * <p>
 * This entity maps to the "showtimes" table in the database and includes details such as price, theater,
 * start and end times, and the associated movie. The movie relationship is configured with LAZY fetching to
 * improve performance, and JSON serialization is managed to avoid issues with lazy-loaded properties.
 * </p>
 */
@Data
@Entity
@Table(name = "showtimes")
public class Showtime {

    /**
     * The unique identifier for the showtime.
     * Generated automatically using the IDENTITY strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The price of the showtime ticket.
     * This field is required.
     */
    @NotNull(message = "Price is required")
    private Double price;

    /**
     * The theater where the showtime is scheduled.
     * This field is required.
     */
    @NotBlank(message = "Theater is required")
    private String theater;

    /**
     * The start time of the showtime.
     * This field is required.
     */
    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    /**
     * The end time of the showtime.
     * This field is required.
     */
    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    /**
     * The movie associated with this showtime.
     * <p>
     * Configured with LAZY fetching to optimize performance and annotated with @JsonIgnoreProperties to avoid
     * issues during JSON serialization of the lazy-loaded association.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Movie movie;
}
