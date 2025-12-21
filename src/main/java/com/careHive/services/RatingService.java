package com.careHive.services;

import com.careHive.dtos.Rating.RatingRequestDTO;
import com.careHive.dtos.Rating.RatingResponseDTO;
import com.careHive.exceptions.CarehiveException;

import java.util.List;

public interface RatingService {
    RatingResponseDTO create(RatingRequestDTO ratingRequestDTO) throws CarehiveException;
    RatingResponseDTO getById(String id) throws CarehiveException;
    List<RatingResponseDTO> getByUserID(String rid) throws CarehiveException;
    List<RatingResponseDTO> getByCaretakerID(String caretakerId) throws CarehiveException;
}
