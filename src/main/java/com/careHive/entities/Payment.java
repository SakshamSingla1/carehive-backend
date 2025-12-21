package com.careHive.entities;

import com.careHive.enums.PaymentStatusEnum;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "payments")
public class Payment {
    @Id
    private String id;
    private String bookingId;
    private String caretakerId;
    private String elderId;

    private double hours;
    private double hourlyRate;
    private double totalAmount;

    private PaymentStatusEnum status;
    private LocalDateTime createdAt;
}
