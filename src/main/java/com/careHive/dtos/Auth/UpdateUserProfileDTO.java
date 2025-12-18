package com.careHive.dtos.Auth;

import com.careHive.enums.VerificationStatusEnum;
import lombok.Data;

import java.util.List;

@Data
public class UpdateUserProfileDTO {
    private String name;
    private String username;
    private String email;
    private String phoneNumber;
    private List<String> serviceIds;
    private List<String> documentIds;
    private VerificationStatusEnum caretakerStatus;
}
