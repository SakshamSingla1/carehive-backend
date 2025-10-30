package com.careHive.dtos.Otp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OtpRequestDTO {
    private String email;
    private String otp;
}
