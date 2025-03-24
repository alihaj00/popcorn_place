package com.att.tdp.popcorn_palace.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Represents a movie entity with basic attributes such as title, genre, duration, rating, and release year.
 * <p>
 * This entity is mapped to the "movies" table in the database. The title field is unique to prevent duplicate entries.
 * </p>
 */
@Data
@Entity
@Table(name = "movies")
public class Movie {

    /**
     * The unique identifier for the movie.
     * Generated automatically using the IDENTITY strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The title of the movie.
     * This field is required and must be unique.
     */
    @NotBlank(message = "Title is required")
    @Column(unique = true)
    private String title;

    /**
     * The genre of the movie.
     * This field is required.
     */
    @NotBlank(message = "Genre is required")
    private String genre;

    /**
     * The duration of the movie in minutes.
     * This field is required.
     */
    @NotNull(message = "Duration is required")
    private int duration;

    /**
     * The rating of the movie.
     * This field is required.
     */
    @NotNull(message = "Rating is required")
    private double rating;

    /**
     * The release year of the movie.
     * This field is required.
     */
    @NotNull(message = "Release year is required")
    private int releaseYear;
}
