package com.careHive.dtos.Otp;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class OtpResponseDTO {
    private String email;
    private String message;
    private LocalDateTime expiresAt;
}
