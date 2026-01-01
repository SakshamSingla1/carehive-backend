package com.careHive.dtos.User;

import com.careHive.entities.Address;
import com.careHive.entities.EmergencyContact;
import com.careHive.enums.GenderEnum;
import com.careHive.enums.RoleEnum;
import com.careHive.enums.StatusEnum;
import com.careHive.enums.VerificationStatusEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class UserProfileResponseDTO {
    private String id;
    private String name;
    private String username;
    private String email;
    private String phone;
    private RoleEnum roleCode;
    private String roleName;
    private StatusEnum status;
    private VerificationStatusEnum emailVerified;
    private VerificationStatusEnum phoneVerified;
    private LocalDate dateOfBirth;
    private GenderEnum gender;
    private Address address;
    private EmergencyContact emergencyContact;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
