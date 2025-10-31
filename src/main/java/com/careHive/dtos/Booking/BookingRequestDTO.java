package com.careHive.dtos.Booking;

import com.careHive.enums.BookingStatusEnum;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class BookingRequestDTO {
    private String serviceId;
    private String elderId;
    private String caretakerId;
    private Timestamp bookingTime;
    private BookingStatusEnum status;
}
