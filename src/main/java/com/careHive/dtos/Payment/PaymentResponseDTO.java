package com.careHive.dtos.Payment;

import com.careHive.enums.PaymentStatusEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponseDTO {
    private String id;
    private String bookingId;
    private String userId;
    private String userName;
    private String caretakerId;
    private String caretakerName;
    private String serviceId;
    private String serviceName;
    private String durationHours;
    private String totalAmount;
    private PaymentStatusEnum status;
}
