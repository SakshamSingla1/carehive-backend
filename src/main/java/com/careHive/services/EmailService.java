package com.careHive.services;

import com.careHive.dtos.Otp.OtpRequestDTO;

public interface EmailService {
    void sendOtpEmail(String to, String otp);
    void sendPasswordResetEmail(String to, String name, String resetLink);
}
