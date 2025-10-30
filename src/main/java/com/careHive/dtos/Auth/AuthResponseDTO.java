package com.careHive.dtos.Auth;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class AuthResponseDTO {
    private String id;
    private String name;
    private String username;
    private String email;
    private String phoneNumber;
    private String roleName;
    private boolean isVerified;
    private LocalDateTime createdAt;
    private String message;
}
