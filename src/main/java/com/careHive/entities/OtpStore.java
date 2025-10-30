package com.careHive.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "otp_store")
public class OtpStore {
    @Id
    private String id;
    private String email;
    private String otp;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
