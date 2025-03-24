package com.att.tdp.popcorn_palace.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ShowtimeRequest {
    @NotNull(message = "Movie ID is required")
    private Long movieId;

    @NotNull(message = "Price is required")
    private Double price;

    @NotNull(message = "Theater is required")
    private String theater;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;
}
