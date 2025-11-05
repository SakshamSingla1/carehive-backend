package com.careHive.services;

import com.careHive.dtos.Payment.PaymentRequestDTO;
import com.careHive.dtos.Payment.PaymentResponseDTO;
import com.careHive.exceptions.CarehiveException;

import java.util.List;

public interface PaymentService {
    PaymentResponseDTO createPayment(PaymentRequestDTO dto) throws CarehiveException;
    PaymentResponseDTO verifyPayment(String paymentId, String transactionId, boolean success) throws CarehiveException;
    PaymentResponseDTO getPayment(String paymentId) throws CarehiveException;
    List<PaymentResponseDTO> getPaymentsByUser(String userId);
    List<PaymentResponseDTO> getPaymentsByCaretaker(String caretakerId);

}
