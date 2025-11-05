package com.careHive.dtos.Payment;

import com.careHive.enums.PaymentStatusEnum;
import lombok.Data;

@Data
public class PaymentRequestDTO {
    private String bookingId;
    private PaymentStatusEnum status;
}
