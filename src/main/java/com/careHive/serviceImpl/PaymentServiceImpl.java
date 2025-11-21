package com.careHive.serviceImpl;

import com.careHive.dtos.Payment.PaymentRequestDTO;
import com.careHive.dtos.Payment.PaymentResponseDTO;
import com.careHive.entities.Booking;
import com.careHive.entities.Payment;
import com.careHive.entities.Services;
import com.careHive.entities.User;
import com.careHive.enums.ExceptionCodeEnum;
import com.careHive.enums.PaymentStatusEnum;
import com.careHive.exceptions.CarehiveException;
import com.careHive.repositories.BookingRepository;
import com.careHive.repositories.PaymentRepository;
import com.careHive.repositories.ServiceRepository;
import com.careHive.repositories.UserRepository;
import com.careHive.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Override
    public PaymentResponseDTO createPayment(PaymentRequestDTO dto) throws CarehiveException {
        if(paymentRepository.existsByBookingIdAndStatus(dto.getBookingId(), PaymentStatusEnum.PENDING)){
            throw new CarehiveException(ExceptionCodeEnum.PAYMENT_ALREADY_EXISTS,"Payment request already exists");
        }
        Booking booking = bookingRepository.findById(dto.getBookingId())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.BOOKING_NOT_FOUND, "Booking not found for ID: " + dto.getBookingId()));

        if (booking.getStartTime() == null || booking.getEndTime() == null) {
            throw new CarehiveException(ExceptionCodeEnum.BAD_REQUEST, "Booking start or end time missing");
        }
        Services service = serviceRepository.findById(booking.getServiceId())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.SERVICE_NOT_FOUND, "Service not found"));
        User elder = userRepository.findById(booking.getElderId())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Elder not found"));
        User caretaker = userRepository.findById(booking.getCaretakerId())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Caretaker not found"));

        double totalAmount = booking.getDurationHours() * service.getPricePerHour();

        // ✅ Create payment record
        Payment payment = Payment.builder()
                .bookingId(booking.getId())
                .caretakerId(caretaker.getId())
                .elderId(elder.getId())
                .hours(booking.getDurationHours())
                .hourlyRate(service.getPricePerHour())
                .totalAmount(totalAmount)
                .status(dto.getStatus() != null ? dto.getStatus() : PaymentStatusEnum.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);

        return mapToResponseDTO(payment, elder, caretaker, service);
    }

    @Override
    public PaymentResponseDTO verifyPayment(String paymentId, String transactionId, boolean success) throws CarehiveException {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.INVALID_CREDENTIALS, "Payment not found"));

        payment.setStatus(success ? PaymentStatusEnum.SUCCESS : PaymentStatusEnum.FAILED);
        paymentRepository.save(payment);

        Booking booking = bookingRepository.findById(payment.getBookingId())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.BOOKING_NOT_FOUND, "Booking not found"));

        Services service = serviceRepository.findById(booking.getServiceId())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.SERVICE_NOT_FOUND, "Service not found"));
        User elder = userRepository.findById(payment.getElderId())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Elder not found"));
        User caretaker = userRepository.findById(payment.getCaretakerId())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Caretaker not found"));

        return mapToResponseDTO(payment, elder, caretaker, service);
    }

    @Override
    public PaymentResponseDTO getPayment(String paymentId) throws CarehiveException {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PAYMENT_NOT_FOUND, "Payment not found"));

        Booking booking = bookingRepository.findById(payment.getBookingId())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.BOOKING_NOT_FOUND, "Booking not found"));
        Services service = serviceRepository.findById(booking.getServiceId())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.SERVICE_NOT_FOUND, "Service not found"));
        User elder = userRepository.findById(payment.getElderId())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Elder not found"));
        User caretaker = userRepository.findById(payment.getCaretakerId())
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Caretaker not found"));

        return mapToResponseDTO(payment, elder, caretaker, service);
    }

    @Override
    public List<PaymentResponseDTO> getPaymentsByUser(String userId) {
        return paymentRepository.findByElderId (userId).stream()
                .map(payment -> {
                    try {
                        Booking booking = bookingRepository.findById(payment.getBookingId()).orElse(null);
                        Services service = booking != null ? serviceRepository.findById(booking.getServiceId()).orElse(null) : null;
                        User elder = userRepository.findById(payment.getElderId()).orElse(null);
                        User caretaker = userRepository.findById(payment.getCaretakerId()).orElse(null);
                        return mapToResponseDTO(payment, elder, caretaker, service);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentResponseDTO> getPaymentsByCaretaker(String caretakerId) {
        return paymentRepository.findByCaretakerId(caretakerId).stream()
                .map(payment -> {
                    try {
                        Booking booking = bookingRepository.findById(payment.getBookingId()).orElse(null);
                        Services service = booking != null ? serviceRepository.findById(booking.getServiceId()).orElse(null) : null;
                        User elder = userRepository.findById(payment.getElderId()).orElse(null);
                        User caretaker = userRepository.findById(payment.getCaretakerId()).orElse(null);
                        return mapToResponseDTO(payment, elder, caretaker, service);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .collect(Collectors.toList());
    }

    private PaymentResponseDTO mapToResponseDTO(Payment payment, User elder, User caretaker, Services service) {
        double totalHours = payment.getHours();
        int hours = (int) totalHours;
        int minutes = (int) Math.round((totalHours - hours) * 60);

        String formattedTime = String.format("%d hr%s %02d min%s",
                hours, hours != 1 ? "s" : "",
                minutes, minutes != 1 ? "s" : "");
        return PaymentResponseDTO.builder()
                .id(payment.getId())
                .bookingId(payment.getBookingId())
                .userId(elder != null ? elder.getId() : null)
                .userName(elder != null ? elder.getName() : null)
                .caretakerId(caretaker != null ? caretaker.getId() : null)
                .caretakerName(caretaker != null ? caretaker.getName() : null)
                .serviceId(service != null ? service.getId() : null)
                .serviceName(service != null ? service.getName() : null)
                .durationHours(formattedTime)
                .totalAmount(payment.getTotalAmount())
                .status(payment.getStatus())
                .build();
    }
}
