package com.careHive.services;

import com.careHive.dtos.Otp.OtpRequestDTO;

public interface EmailService {
     void sendEmail(String to, String subject, String htmlContent);
}
