package com.careHive.controller;

import com.careHive.dtos.Booking.BookingRequestDTO;
import com.careHive.dtos.Booking.BookingResponseDTO;
import com.careHive.exceptions.CarehiveException;
import com.careHive.payload.ApiResponse;
import com.careHive.payload.ResponseModel;
import com.careHive.services.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    // ✅ Create a new booking
    @PostMapping
    public ResponseEntity<ResponseModel<BookingResponseDTO>> createBooking(
            @RequestBody BookingRequestDTO bookingRequestDTO) throws CarehiveException {
        BookingResponseDTO response = bookingService.create(bookingRequestDTO);
        return ApiResponse.respond(response, "Booking created successfully ✅", "Failed to create booking ❌");
    }

    // ✅ Update a booking (e.g., caretaker accepts/rejects)
    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<BookingResponseDTO>> updateBooking(
            @PathVariable String id,
            @RequestBody BookingRequestDTO bookingRequestDTO) throws CarehiveException {
        BookingResponseDTO response = bookingService.update(id, bookingRequestDTO);
        return ApiResponse.respond(response, "Booking updated successfully ✅", "Failed to update booking ❌");
    }

    // ✅ Get a booking by ID
    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<BookingResponseDTO>> getBooking(@PathVariable String id) throws CarehiveException {
        BookingResponseDTO response = bookingService.getBooking(id);
        return ApiResponse.respond(response, "Booking fetched successfully ✅", "Failed to fetch booking ❌");
    }

    // ✅ Get all bookings
    @GetMapping
    public ResponseEntity<ResponseModel<List<BookingResponseDTO>>> getAllBookings() throws CarehiveException {
        List<BookingResponseDTO> response = bookingService.getAll();
        return ApiResponse.respond(response, "Bookings fetched successfully ✅", "Failed to fetch bookings ❌");
    }

    // ✅ Delete a booking
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<String>> deleteBooking(@PathVariable String id) throws CarehiveException {
        String response = bookingService.delete(id);
        return ApiResponse.respond(response, "Booking deleted successfully ✅", "Failed to delete booking ❌");
    }
}
