package com.careHive.dtos.Rating;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RatingResponseDTO {
    private String id;
    private String bookingId;
    private String userName;
    private String caretakerName;
    private double rating;
    private String comment;
    private LocalDateTime createdAt;
}