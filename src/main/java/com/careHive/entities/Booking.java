package com.careHive.entities;

import com.careHive.enums.BookingStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "bookings")
public class Booking {
    @Id
    private String id;
    private String bookingCode;
    private String serviceId;
    private String elderId;
    private String caretakerId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private double durationHours;
    private double pricePerHour;
    private double totalAmount;

    private BookingStatusEnum status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
