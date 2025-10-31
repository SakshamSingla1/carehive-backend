package com.careHive.services;

import com.careHive.dtos.Booking.BookingRequestDTO;
import com.careHive.dtos.Booking.BookingResponseDTO;
import com.careHive.exceptions.CarehiveException;

import java.util.List;

public interface BookingService {
    BookingResponseDTO create(BookingRequestDTO bookingRequestDTO) throws CarehiveException;
    BookingResponseDTO update(String id,BookingRequestDTO bookingRequestDTO) throws CarehiveException;
    BookingResponseDTO getBooking(String id) throws CarehiveException;
    String delete(String id) throws CarehiveException;
    List<BookingResponseDTO> getAll() throws CarehiveException;
}
