package com.careHive.dtos.Rating;

import lombok.Data;

@Data
public class RatingRequestDTO {
    private String bookingId;
    private double rating;
    private String comment;
}
