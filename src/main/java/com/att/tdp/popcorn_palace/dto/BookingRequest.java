package com.att.tdp.popcorn_palace.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingRequest {
    @NotNull(message = "Showtime ID is required")
    private Long showtimeId;

    @NotNull(message = "Seat number is required")
    private Integer seatNumber;

    @NotNull(message = "User ID is required")
    private String userId;
}
