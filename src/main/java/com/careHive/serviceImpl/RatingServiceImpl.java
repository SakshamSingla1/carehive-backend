package com.careHive.serviceImpl;

import com.careHive.dtos.Rating.RatingRequestDTO;
import com.careHive.dtos.Rating.RatingResponseDTO;
import com.careHive.entities.Booking;
import com.careHive.entities.Rating;
import com.careHive.entities.User;
import com.careHive.enums.ExceptionCodeEnum;
import com.careHive.exceptions.CarehiveException;
import com.careHive.repositories.BookingRepository;
import com.careHive.repositories.RatingRepository;
import com.careHive.repositories.UserRepository;
import com.careHive.services.NTService;
import com.careHive.services.RatingService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RatingServiceImpl implements RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private NTService ntService;

    @Override
    public RatingResponseDTO create(RatingRequestDTO dto) throws CarehiveException {
        Booking booking = bookingRepository.findById(dto.getBookingId())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.BOOKING_NOT_FOUND, "Booking not found"));

        User elder = userRepository.findById(booking.getElderId())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Elder user not found"));

        User caretaker = userRepository.findById(booking.getCaretakerId())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Caretaker not found"));

        Rating rating = Rating.builder()
                .userId(elder.getId())
                .caretakerId(caretaker.getId())
                .bookingId(booking.getId())
                .rating(dto.getRating())
                .comment(dto.getComment())
                .createdAt(LocalDateTime.now())
                .build();

        Rating saved = ratingRepository.save(rating);

        // ✅ Send Notification (non-blocking)
        try {
            Map<String, Object> variables = Map.of(
                    "caretakerName", caretaker.getName(),
                    "elderName", elder.getName(),
                    "rating", dto.getRating(),
                    "comment", dto.getComment()
            );
            ntService.sendNotification("REVIEW-ADDED-CARETAKER", variables, caretaker.getEmail());
        } catch (Exception e) {
            // Log and continue - do not break rating creation
            System.err.println("⚠️ Failed to send notification: " + e.getMessage());
        }

        return mapToResponse(saved, booking, elder, caretaker);
    }

    @Override
    public RatingResponseDTO getById(String id) throws CarehiveException {
        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.REVIEW_NOT_FOUND, "Rating not found"));
        return mapToResponse(rating);
    }

    @Override
    public List<RatingResponseDTO> getByCaretakerID(String caretakerId) throws CarehiveException {
        List<Rating> ratings = ratingRepository.findByCaretakerId(caretakerId);
        return mapToResponseList(ratings);
    }

    @Override
    public List<RatingResponseDTO> getByUserID(String userId) throws CarehiveException {
        List<Rating> ratings = ratingRepository.findByUserId(userId);
        return mapToResponseList(ratings);
    }

    private List<RatingResponseDTO> mapToResponseList(List<Rating> ratings) throws CarehiveException {
        if (ratings.isEmpty()) return List.of();
        List<String> bookingIds = ratings.stream().map(Rating::getBookingId).toList();
        Map<String, Booking> bookingMap = bookingRepository.findAllById(bookingIds)
                .stream().collect(Collectors.toMap(Booking::getId, b -> b));
        List<String> userIds = bookingMap.values().stream()
                .flatMap(b -> List.of(b.getElderId(), b.getCaretakerId()).stream())
                .distinct().toList();
        Map<String, User> userMap = userRepository.findAllById(userIds)
                .stream().collect(Collectors.toMap(User::getId, u -> u));
        return ratings.stream()
                .map(r -> {
                    Booking booking = bookingMap.get(r.getBookingId());
                    User elder = userMap.get(booking.getElderId());
                    User caretaker = userMap.get(booking.getCaretakerId());
                    return mapToResponseSafe(r, booking, elder, caretaker);
                })
                .collect(Collectors.toList());
    }

    private RatingResponseDTO mapToResponse(Rating rating) throws CarehiveException {
        Booking booking = bookingRepository.findById(rating.getBookingId())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.BOOKING_NOT_FOUND, "Booking not found"));

        User elder = userRepository.findById(booking.getElderId())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found"));

        User caretaker = userRepository.findById(booking.getCaretakerId())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Caretaker not found"));

        return mapToResponse(rating, booking, elder, caretaker);
    }

    private RatingResponseDTO mapToResponse(Rating rating, Booking booking, User elder, User caretaker) {
        return RatingResponseDTO.builder()
                .id(rating.getId())
                .userName(elder.getName())
                .caretakerName(caretaker.getName())
                .bookingId(rating.getBookingId())
                .rating(rating.getRating())
                .comment(rating.getComment())
                .createdAt(rating.getCreatedAt())
                .build();
    }

    private RatingResponseDTO mapToResponseSafe(Rating rating, Booking booking, User elder, User caretaker) {
        try {
            return mapToResponse(rating, booking, elder, caretaker);
        } catch (Exception e) {
            System.err.println("⚠️ Error mapping rating: " + e.getMessage());
            return RatingResponseDTO.builder()
                    .id(rating.getId())
                    .bookingId(rating.getBookingId())
                    .rating(rating.getRating())
                    .comment(rating.getComment())
                    .build();
        }
    }
}
