package com.careHive.serviceImpl;

import com.careHive.services.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    public void sendOtpEmail(String toEmail, String otp) {
        String subject = "CareHive - Verify Your Email";
        String htmlContent = loadTemplate("OtpEmailTemplate.html")
                .replace("{{OTP_CODE}}", otp);

        sendHtmlEmail(toEmail, subject, htmlContent);
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String name, String token) {
        String resetLink = frontendUrl + "/reset-password?token=" + token;
        String subject = "CareHive - Password Reset Request";
        String htmlContent = loadTemplate("PasswordResetEmailTemplate.html")
                .replace("{{USER_NAME}}", name)
                .replace("{{RESET_LINK}}", resetLink);

        sendHtmlEmail(toEmail, subject, htmlContent);
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private String loadTemplate(String templateName) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("templates/" + templateName)) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Email template not found: " + templateName, e);
        }
    }
}
