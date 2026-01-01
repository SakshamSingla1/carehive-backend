package com.careHive.dtos.User;

import com.careHive.entities.DocumentInfo;
import com.careHive.entities.Services;
import com.careHive.enums.RoleEnum;
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
    private RoleEnum roleCode;
    private String roleName;
    private String phone;
    private VerificationStatusEnum emailVerified;
    private VerificationStatusEnum phoneVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
