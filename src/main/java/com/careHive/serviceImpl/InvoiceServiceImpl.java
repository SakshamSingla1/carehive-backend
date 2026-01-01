package com.careHive.serviceImpl;

import com.careHive.dtos.Payment.PaymentResponseDTO;
import com.careHive.entities.*;
import com.careHive.enums.ExceptionCodeEnum;
import com.careHive.exceptions.CarehiveException;
import com.careHive.repositories.*;
import com.careHive.services.InvoiceService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final TemplateEngine templateEngine;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final BookingRepository bookingRepository;
    private final Cloudinary cloudinary;
    private final InvoiceRepository invoiceRepository;

    @Override
    public String generateInvoice(Payment payment) throws CarehiveException {
        try {
            Booking booking = bookingRepository.findById(payment.getBookingId())
                    .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.BOOKING_NOT_FOUND, "Booking not found"));

            Users elder = userRepository.findById(payment.getElderId())
                    .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Elder not found"));

            Users caretaker = userRepository.findById(payment.getCaretakerId())
                    .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Caretaker not found"));

            Services service = serviceRepository.findById(booking.getServiceId())
                    .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.SERVICE_NOT_FOUND, "Service not found"));

            double totalHours = booking.getDurationHours();
            int hours = (int) totalHours;
            int minutes = (int) Math.round((totalHours - hours) * 60);
            String durationFormatted = String.format("%d hr %02d min", hours, minutes);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            String formattedCreatedAt = payment.getCreatedAt() != null ? payment.getCreatedAt().format(formatter) : "N/A";
            String formattedStartTime = booking.getStartTime() != null ? booking.getStartTime().format(formatter) : "N/A";
            String formattedEndTime = booking.getEndTime() != null ? booking.getEndTime().format(formatter) : "N/A";
            Context context = new Context();
            context.setVariable("payment", payment);
            context.setVariable("booking", booking);
            context.setVariable("elder", elder);
            context.setVariable("caretaker", caretaker);
            context.setVariable("service", service);
            context.setVariable("durationFormatted", durationFormatted);
            context.setVariable("formattedCreatedAt", formattedCreatedAt);
            context.setVariable("formattedStartTime", formattedStartTime);
            context.setVariable("formattedEndTime", formattedEndTime);
            String html = templateEngine.process("payment-invoice", context);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(outputStream);
            builder.run();

            byte[] pdfBytes = outputStream.toByteArray();
            Map uploadResult = cloudinary.uploader().upload(
                    pdfBytes,
                    ObjectUtils.asMap(
                            "resource_type", "raw",        // raw → for PDFs
                            "folder", "invoices/",
                            "public_id", "invoice_" + payment.getId(),
                            "overwrite", true
                    )
            );

            String cloudinaryUrl = (String) uploadResult.get("secure_url");
            Invoice invoice = Invoice.builder()
                    .paymentId(payment.getId())
                    .invoiceUrl(cloudinaryUrl)
                    .build();

            invoiceRepository.save(invoice);

            return cloudinaryUrl;

        } catch (Exception e) {
            e.printStackTrace();
            throw new CarehiveException(ExceptionCodeEnum.PAYMENT_FAILED, "Error generating invoice: " + e.getMessage());
        }
    }


    @Override
    public String getInvoiceByPaymentId(String paymentId) {
        return invoiceRepository.findByPaymentId(paymentId)
                .map(Invoice::getInvoiceUrl)
                .orElse("Invoice not found for Payment ID: " + paymentId);
    }
}
