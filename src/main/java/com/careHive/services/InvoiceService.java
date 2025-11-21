package com.careHive.services;

import com.careHive.entities.Payment;
import com.careHive.exceptions.CarehiveException;

public interface InvoiceService {
    String generateInvoice(Payment payment) throws CarehiveException;
    String getInvoiceByPaymentId(String paymentId);
}
