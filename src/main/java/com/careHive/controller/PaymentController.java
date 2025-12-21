package com.careHive.controller;

import com.careHive.dtos.Payment.PaymentRequestDTO;
import com.careHive.dtos.Payment.PaymentResponseDTO;
import com.careHive.enums.ExceptionCodeEnum;
import com.careHive.exceptions.CarehiveException;
import com.careHive.payload.ApiResponse;
import com.careHive.payload.ResponseModel;
import com.careHive.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * ✅ Create a new payment (triggered after booking completion)
     */
    @PostMapping
    public ResponseEntity<ResponseModel<PaymentResponseDTO>> createPayment(@RequestBody PaymentRequestDTO dto) throws CarehiveException {
        try {
            PaymentResponseDTO response = paymentService.createPayment(dto);
            return ApiResponse.respond(
                    response,
                    "Payment created successfully",
                    "Unable to create payment"
            );
        } catch (CarehiveException e) {
            throw new CarehiveException(ExceptionCodeEnum.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * ✅ Verify a payment transaction
     */
    @PostMapping("/{paymentId}/verify")
    public ResponseEntity<ResponseModel<PaymentResponseDTO>> verifyPayment(
            @PathVariable String paymentId,
            @RequestParam String transactionId,
            @RequestParam boolean success) throws CarehiveException {
        try {
            PaymentResponseDTO response = paymentService.verifyPayment(paymentId, transactionId, success);
            return ApiResponse.respond(
                    response,
                    success ? "Payment verified successfully" : "Payment verification failed",
                    "Unable to verify payment"
            );
        } catch (CarehiveException e) {
            throw new CarehiveException(ExceptionCodeEnum.PAYMENT_NOT_FOUND, e.getMessage());
        }
    }

    /**
     * ✅ Get a single payment by its ID
     */
    @GetMapping("/{paymentId}")
    public ResponseEntity<ResponseModel<PaymentResponseDTO>> getPayment(@PathVariable String paymentId) throws CarehiveException {
        try {
            PaymentResponseDTO response = paymentService.getPayment(paymentId);
            return ApiResponse.respond(response, "Payment fetched successfully","Payment not found");
        } catch (CarehiveException e) {
            throw new CarehiveException(ExceptionCodeEnum.PAYMENT_NOT_FOUND, e.getMessage());
        }
    }

    /**
     * ✅ Get all payments associated with a specific user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseModel<List<PaymentResponseDTO>>> getPaymentsByUser(@PathVariable String userId) {
        List<PaymentResponseDTO> payments = paymentService.getPaymentsByUser(userId);
        return ApiResponse.respond(payments, "Payments fetched successfully","Payments not found");
    }

    /**
     * ✅ Get all payments associated with a specific caretaker
     */
    @GetMapping("/caretaker/{caretakerId}")
    public ResponseEntity<ResponseModel<List<PaymentResponseDTO>>> getPaymentsByCaretaker(@PathVariable String caretakerId) {
        List<PaymentResponseDTO> payments = paymentService.getPaymentsByCaretaker(caretakerId);
        return ApiResponse.respond(payments, "Payments fetched successfully","Payments not found");
    }
}
