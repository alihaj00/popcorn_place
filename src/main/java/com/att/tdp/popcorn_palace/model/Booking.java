package com.att.tdp.popcorn_palace.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

/**
 * Represents a booking record for a showtime.
 * <p>
 * This entity contains details such as the associated showtime, the seat number, and the user ID.
 * </p>
 */
@Data
@Entity
@Table(name = "bookings")
public class Booking {

    /**
     * The unique identifier for the booking.
     * Generated as a UUID.
     */
    @Id
    @GeneratedValue
    private UUID bookingId;

    /**
     * The showtime associated with this booking.
     * This is a many-to-one relationship as multiple bookings can be linked to a single showtime.
     * Uses LAZY fetching to defer loading until needed.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showtime_id", nullable = false)
    private Showtime showtime;

    /**
     * The seat number reserved for this booking.
     * This field is required.
     */
    @NotNull(message = "Seat number is required")
    private Integer seatNumber;

    /**
     * The identifier for the user who made the booking.
     * This field is required.
     */
    @NotNull(message = "User ID is required")
    private String userId;
}
