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
import com.careHive.services.NTService;
import com.careHive.utils.BookingCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final NTService ntService;

    @Override
    public BookingResponseDTO create(BookingRequestDTO dto) throws CarehiveException {
        User elder = userRepository
                .findByIdAndRoleCode(dto.getElderId(), RoleEnum.ELDER.name())
                .orElseThrow(() -> new CarehiveException(
                        ExceptionCodeEnum.PROFILE_NOT_FOUND, "Elder not found"));
        User caretaker = userRepository
                .findByIdAndRoleCode(dto.getCaretakerId(), RoleEnum.CARETAKER.name())
                .orElseThrow(() -> new CarehiveException(
                        ExceptionCodeEnum.PROFILE_NOT_FOUND, "Caretaker not found"));
        Services service = serviceRepository.findById(dto.getServiceId())
                .orElseThrow(() -> new CarehiveException(
                        ExceptionCodeEnum.BAD_REQUEST, "Service not found"));
        boolean exists = bookingRepository
                .existsByElderIdAndCaretakerIdAndStatus(
                        elder.getId(),
                        caretaker.getId(),
                        BookingStatusEnum.PENDING
                );
        if (exists) {
            throw new CarehiveException(
                    ExceptionCodeEnum.BAD_REQUEST,
                    "A pending booking already exists for this caretaker and elder"
            );
        }
        double durationHours =
                Duration.between(dto.getStartTime(), dto.getEndTime())
                        .toMinutes() / 60.0;
        String bookingCode = BookingCodeGenerator.generate(
                dto.getStartTime(),
                durationHours
        );
        Booking booking = Booking.builder()
                .bookingCode(bookingCode)
                .elderId(elder.getId())
                .caretakerId(caretaker.getId())
                .serviceId(service.getId())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .durationHours(durationHours)
                .status(BookingStatusEnum.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        bookingRepository.save(booking);

        sendBookingStatusEmail(booking);

        return mapToResponseDTO(booking);
    }

    @Override
    public BookingResponseDTO update(String id, BookingRequestDTO dto)
            throws CarehiveException {

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CarehiveException(
                        ExceptionCodeEnum.BOOKING_NOT_FOUND, "Booking not found"));

        if (dto.getStatus() != null) {
            validateStatusTransition(booking.getStatus(), dto.getStatus());
            booking.setStatus(dto.getStatus());

            if (dto.getStatus() == BookingStatusEnum.IN_PROGRESS
                    && booking.getStartTime() == null) {
                booking.setStartTime(LocalDateTime.now());
            }

            if (dto.getStatus() == BookingStatusEnum.COMPLETED) {
                booking.setEndTime(LocalDateTime.now());

                double hours =
                        Duration.between(
                                booking.getStartTime(),
                                booking.getEndTime()
                        ).toMinutes() / 60.0;

                booking.setDurationHours(hours);
            }
        }

        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);

        sendBookingStatusEmail(booking);

        return mapToResponseDTO(booking);
    }

    @Override
    public BookingResponseDTO getBooking(String id) throws CarehiveException {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CarehiveException(
                        ExceptionCodeEnum.BOOKING_NOT_FOUND, "Booking not found"));

        return mapToResponseDTO(booking);
    }

    @Override
    public String delete(String id) throws CarehiveException {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CarehiveException(
                        ExceptionCodeEnum.BOOKING_NOT_FOUND, "Booking not found"));

        bookingRepository.delete(booking);
        return "Booking deleted successfully";
    }

    @Override
    public List<BookingResponseDTO> getAll() throws CarehiveException {
        List<BookingResponseDTO> response = new ArrayList<>();
        for (Booking booking : bookingRepository.findAll()) {
            response.add(mapToResponseDTO(booking));
        }
        return response;
    }

    private void validateStatusTransition(BookingStatusEnum current, BookingStatusEnum next
    ) throws CarehiveException {
        if (current == BookingStatusEnum.COMPLETED) {
            throw new CarehiveException(
                    ExceptionCodeEnum.BAD_REQUEST,
                    "Completed booking cannot be updated"
            );
        }
    }

    private void sendBookingStatusEmail(Booking booking) throws CarehiveException {
        User elder = userRepository
                .findByIdAndRoleCode(booking.getElderId(), RoleEnum.ELDER.name())
                .orElseThrow(() -> new CarehiveException(
                        ExceptionCodeEnum.PROFILE_NOT_FOUND, "Elder not found"));

        User caretaker = userRepository
                .findByIdAndRoleCode(booking.getCaretakerId(), RoleEnum.CARETAKER.name())
                .orElseThrow(() -> new CarehiveException(
                        ExceptionCodeEnum.PROFILE_NOT_FOUND, "Caretaker not found"));

        Services service = serviceRepository.findById(booking.getServiceId())
                .orElseThrow(() -> new CarehiveException(
                        ExceptionCodeEnum.BAD_REQUEST, "Service not found"));

        Map<String, Object> vars = Map.of(
                "bookingCode", booking.getBookingCode(),
                "elderName", elder.getName(),
                "caretakerName", caretaker.getName(),
                "serviceName", service.getName(),
                "startTime", booking.getStartTime(),
                "endTime", booking.getEndTime(),
                "durationHours", booking.getDurationHours()
        );

        switch (booking.getStatus()) {
            case PENDING ->
                    ntService.sendNotification(
                            "BOOKING-PENDING-CARETAKER",
                            vars,
                            caretaker.getEmail()
                    );
            case CONFIRMED -> {
                ntService.sendNotification(
                        "BOOKING-CONFIRMATION-ELDER",
                        vars,
                        elder.getEmail()
                );
                ntService.sendNotification(
                        "BOOKING-CONFIRMATION-CARETAKER",
                        vars,
                        caretaker.getEmail()
                );
            }
            case IN_PROGRESS -> {
                ntService.sendNotification(
                        "BOOKING-PROGRESS-ELDER",
                        vars,
                        elder.getEmail()
                );
                ntService.sendNotification(
                        "BOOKING-PROGRESS-CARETAKER",
                        vars,
                        caretaker.getEmail()
                );
            }
            case REJECTED ->
                    ntService.sendNotification(
                            "BOOKING-REJECTED-ELDER",
                            vars,
                            elder.getEmail()
                    );
            case COMPLETED -> {
                ntService.sendNotification(
                        "BOOKING-COMPLETED-ELDER",
                        vars,
                        elder.getEmail()
                );
                ntService.sendNotification(
                        "BOOKING-COMPLETED-CARETAKER",
                        vars,
                        caretaker.getEmail()
                );
            }
        }
    }

    private BookingResponseDTO mapToResponseDTO(Booking booking)
            throws CarehiveException {
        User elder = userRepository
                .findByIdAndRoleCode(booking.getElderId(), RoleEnum.ELDER.name())
                .orElseThrow(() -> new CarehiveException(
                        ExceptionCodeEnum.PROFILE_NOT_FOUND, "Elder not found"));
        User caretaker = userRepository
                .findByIdAndRoleCode(booking.getCaretakerId(), RoleEnum.CARETAKER.name())
                .orElseThrow(() -> new CarehiveException(
                        ExceptionCodeEnum.PROFILE_NOT_FOUND, "Caretaker not found"));
        Services service = serviceRepository.findById(booking.getServiceId())
                .orElseThrow(() -> new CarehiveException(
                        ExceptionCodeEnum.BAD_REQUEST, "Service not found"));
        return BookingResponseDTO.builder()
                .id(booking.getId())
                .bookingCode(booking.getBookingCode())
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
