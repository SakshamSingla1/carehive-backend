package com.careHive.dtos.Booking;

import com.careHive.enums.BookingStatusEnum;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingResponseDTO {
    private String id;
    private String serviceId;
    private String serviceName;
    private String elderId;
    private String elderName;
    private String caretakerId;
    private String caretakerName;

    private LocalDateTime bookingTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private BookingStatusEnum status;
}
