package com.careHive.dtos.Auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PhoneOtpRequestDTO {
    private String phoneNumber;
}
