package com.careHive.dtos.Booking;

import com.careHive.enums.BookingStatusEnum;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingRequestDTO {
    private String serviceId;
    private String elderId;
    private String caretakerId;
    private LocalDateTime startTime;
    private BookingStatusEnum status;
}
