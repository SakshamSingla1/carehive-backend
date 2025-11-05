package com.careHive.serviceImpl;

import com.careHive.dtos.Booking.BookingRequestDTO;
import com.careHive.dtos.Booking.BookingResponseDTO;
import com.careHive.entities.Booking;
import com.careHive.entities.Services;
import com.careHive.entities.User;
import com.careHive.enums.BookingStatusEnum;
import com.careHive.enums.ExceptionCodeEnum;
import com.careHive.enums.RoleEnum;
import com.careHive.exceptions.CarehiveException;
import com.careHive.repositories.BookingRepository;
import com.careHive.repositories.ServiceRepository;
import com.careHive.repositories.UserRepository;
import com.careHive.services.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;

    @Override
    public BookingResponseDTO create(BookingRequestDTO dto) throws CarehiveException {
        User elder = userRepository.findByIdAndRoleCode(dto.getElderId(), RoleEnum.ELDER.name())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Elder not found"));

        User caretaker = userRepository.findByIdAndRoleCode(dto.getCaretakerId(), RoleEnum.CARETAKER.name())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Caretaker not found"));

        Services service = serviceRepository.findById(dto.getServiceId())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.BAD_REQUEST, "Service not found"));

        boolean existingBooking = bookingRepository.existsByElderIdAndCaretakerIdAndStatus(
                elder.getId(), caretaker.getId(), BookingStatusEnum.PENDING
        );
        if (existingBooking) {
            throw new CarehiveException(ExceptionCodeEnum.BAD_REQUEST,
                    "A pending booking already exists for this caretaker and elder.");
        }

        Booking booking = Booking.builder()
                .elderId(elder.getId())
                .caretakerId(caretaker.getId())
                .serviceId(service.getId())
                .status(BookingStatusEnum.PENDING)
                .startTime(dto.getStartTime())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        bookingRepository.save(booking);
        return mapToResponseDTO(booking);
    }

    @Override
    public BookingResponseDTO update(String id, BookingRequestDTO dto) throws CarehiveException {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.BOOKING_NOT_FOUND, "Booking not found"));

        // ✅ Update status logic
        if (dto.getStatus() != null) {
            booking.setStatus(dto.getStatus());

            if (dto.getStatus() == BookingStatusEnum.IN_PROGRESS) {
                // Mark start time if not already
                if (booking.getStartTime() == null) {
                    booking.setStartTime(LocalDateTime.now());
                }
            } else if (dto.getStatus() == BookingStatusEnum.COMPLETED) {
                // Set end time
                booking.setEndTime(LocalDateTime.now());

                // ✅ Calculate duration (only now that endTime exists)
                if (booking.getStartTime() != null && booking.getEndTime() != null) {
                    double hours = Duration.between(booking.getStartTime(), booking.getEndTime()).toMinutes() / 60.0;
                    booking.setDurationHours(hours);
                }
            }
        }

        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);

        return mapToResponseDTO(booking);
    }

    @Override
    public BookingResponseDTO getBooking(String id) throws CarehiveException {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.BOOKING_NOT_FOUND, "Booking not found"));
        return mapToResponseDTO(booking);
    }

    @Override
    public String delete(String id) throws CarehiveException {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.BOOKING_NOT_FOUND, "Booking not found"));
        bookingRepository.delete(booking);
        return "Booking deleted successfully.";
    }

    @Override
    public List<BookingResponseDTO> getAll() throws CarehiveException {
        List<Booking> bookings = bookingRepository.findAll();
        List<BookingResponseDTO> list = new ArrayList<>();
        for (Booking booking : bookings) {
            list.add(mapToResponseDTO(booking));
        }
        return list;
    }

    private BookingResponseDTO mapToResponseDTO(Booking booking) throws CarehiveException {
        User elder = userRepository.findByIdAndRoleCode(booking.getElderId(), RoleEnum.ELDER.name())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Elder not found"));

        User caretaker = userRepository.findByIdAndRoleCode(booking.getCaretakerId(), RoleEnum.CARETAKER.name())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Caretaker not found"));

        Services service = serviceRepository.findById(booking.getServiceId())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.BAD_REQUEST, "Service not found"));

        return BookingResponseDTO.builder()
                .id(booking.getId())
                .elderId(elder.getId())
                .elderName(elder.getName())
                .caretakerId(caretaker.getId())
                .caretakerName(caretaker.getName())
                .serviceId(service.getId())
                .serviceName(service.getName())
                .status(booking.getStatus())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .durationHours(booking.getDurationHours())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }
}
