package com.careHive.dtos.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {

    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private String username;

    private String roleCode;
    private String roleName;

    private boolean isVerified;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
