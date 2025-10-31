package com.careHive.serviceImpl;

import com.careHive.dtos.Booking.BookingRequestDTO;
import com.careHive.dtos.Booking.BookingResponseDTO;
import com.careHive.dtos.Service.ServiceResponseDTO;
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
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Override
    public BookingResponseDTO create(BookingRequestDTO bookingRequestDTO) throws CarehiveException {
        // ✅ Validate elder
        User elder = userRepository.findByIdAndRoleCode(bookingRequestDTO.getElderId(), RoleEnum.ELDER.name())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Elder not found"));

        // ✅ Validate caretaker
        User caretaker = userRepository.findByIdAndRoleCode(bookingRequestDTO.getCaretakerId(), RoleEnum.CARETAKER.name())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Caretaker not found"));

        // ✅ Validate service
        Services service = serviceRepository.findById(bookingRequestDTO.getServiceId())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.BAD_REQUEST, "Service not found"));

        // ✅ Prevent duplicate active booking between same elder and caretaker
        boolean existingBooking = bookingRepository.existsByElderIdAndCaretakerIdAndStatus(
                elder.getId(),
                caretaker.getId(),
                BookingStatusEnum.PENDING
        );
        if (existingBooking) {
            throw new CarehiveException(ExceptionCodeEnum.BAD_REQUEST, "A pending booking already exists for this caretaker and elder.");
        }

        // ✅ Create booking
        Booking booking = Booking.builder()
                .elderId(elder.getId())
                .caretakerId(caretaker.getId())
                .serviceId(service.getId())
                .status(BookingStatusEnum.PENDING)
                .bookingTime(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        bookingRepository.save(booking);
        return mapToResponseDTO(booking);
    }

    @Override
    public BookingResponseDTO update(String id, BookingRequestDTO bookingRequestDTO) throws CarehiveException {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.BOOKING_NOT_FOUND, "Booking not found"));

        // Optional updates
        if (bookingRequestDTO.getServiceId() != null) {
            Services service = serviceRepository.findById(bookingRequestDTO.getServiceId())
                    .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.BAD_REQUEST, "Service not found"));
            booking.setServiceId(service.getId());
        }

        if (bookingRequestDTO.getStatus() != null) {
            booking.setStatus(bookingRequestDTO.getStatus());
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
            BookingResponseDTO bookingResponseDTO = mapToResponseDTO(booking);
            list.add(bookingResponseDTO);
        }
        return list;
    }

    // ✅ Helper method
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
                .bookingTime(booking.getBookingTime())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }
}
