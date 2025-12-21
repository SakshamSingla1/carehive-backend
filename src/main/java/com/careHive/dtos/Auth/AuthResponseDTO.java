package com.careHive.dtos.Auth;

import com.careHive.enums.VerificationStatusEnum;
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
    private VerificationStatusEnum verified;
    private LocalDateTime createdAt;
    private String message;
}
