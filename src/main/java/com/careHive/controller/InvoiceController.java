package com.careHive.controller;

import com.careHive.entities.Invoice;
import com.careHive.entities.Payment;
import com.careHive.enums.ExceptionCodeEnum;
import com.careHive.exceptions.CarehiveException;
import com.careHive.payload.ApiResponse;
import com.careHive.payload.ResponseModel;
import com.careHive.repositories.InvoiceRepository;
import com.careHive.repositories.PaymentRepository;
import com.careHive.serviceImpl.InvoiceServiceImpl;
import com.careHive.services.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final PaymentRepository paymentRepository;

    @PostMapping("/{paymentId}/generate")
    public ResponseEntity<ResponseModel<String>> generateInvoice(@PathVariable String paymentId) throws CarehiveException {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PAYMENT_NOT_FOUND, "Payment not found"));
        String invoiceUrl = invoiceService.generateInvoice(payment);
        if (invoiceUrl == null || invoiceUrl.isEmpty()) {
            throw new CarehiveException(ExceptionCodeEnum.PAYMENT_FAILED, "Failed to generate invoice");
        }
        return ApiResponse.respond(
                invoiceUrl,
                "Invoice generated successfully",
                "Failed to generate invoice"
        );
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<ResponseModel<String>> getInvoice(@PathVariable String paymentId) throws CarehiveException {
        String invoice = invoiceService.getInvoiceByPaymentId(paymentId);
        return ApiResponse.respond(
                invoice,
                "Invoice fetched successfully",
                "Failed to fetch invoice"
        );
    }

    @GetMapping("/{paymentId}/download")
    public ResponseEntity<ResponseModel<String>> downloadInvoice(@PathVariable String paymentId) throws CarehiveException {
        String invoice = invoiceService.getInvoiceByPaymentId(paymentId);
        return ApiResponse.respond(
                invoice,
                "Invoice download link retrieved successfully",
                "Failed to fetch invoice link"
        );
    }
}
