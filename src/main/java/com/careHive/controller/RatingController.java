package com.careHive.controller;

import com.careHive.dtos.Rating.RatingRequestDTO;
import com.careHive.dtos.Rating.RatingResponseDTO;
import com.careHive.exceptions.CarehiveException;
import com.careHive.payload.ApiResponse;
import com.careHive.payload.ResponseModel;
import com.careHive.services.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<ResponseModel<RatingResponseDTO>> create(@RequestBody RatingRequestDTO dto) throws CarehiveException {
        RatingResponseDTO response = ratingService.create(dto);
        return ApiResponse.respond(response, "Rating added successfully", "Failed to add rating");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<RatingResponseDTO>> getById(@PathVariable String id) throws CarehiveException {
        RatingResponseDTO response = ratingService.getById(id);
        return ApiResponse.respond(response, "Rating fetched successfully", "Failed to fetch rating");
    }

    @GetMapping("/caretaker/{id}")
    public ResponseEntity<ResponseModel<List<RatingResponseDTO>>> getByCaretakerId(@PathVariable String id) throws CarehiveException {
        List<RatingResponseDTO> response = ratingService.getByUserID(id);
        return ApiResponse.respond(response, "Caretaker ratings fetched successfully", "Failed to fetch ratings");
    }
}
