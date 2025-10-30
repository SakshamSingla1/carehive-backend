package com.careHive.dtos.Password;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class ChangePasswordDTO {
    private String userId;        // Or you can derive this from JWT/session instead of sending in request
    private String oldPassword;   // Current password (for verification)
    private String newPassword;   // New password user wants to set
    private String confirmPassword; // Optional — for frontend confirmation check
}
