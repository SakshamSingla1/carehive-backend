package com.careHive.dtos.User;

import com.careHive.entities.DocumentInfo;
import com.careHive.entities.Services;
import com.careHive.enums.VerificationStatusEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class UserProfileDTO {
    private String id;
    private String name;
    private String username;
    private String email;
    private String roleCode;
    private String roleName;
    private String phoneNumber;
    private VerificationStatusEnum verified;
    private VerificationStatusEnum caretakerStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
