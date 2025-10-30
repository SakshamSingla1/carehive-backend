package com.careHive.dtos.Auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthLoginDTO {
    private String username;
    private String email;
    private String phoneNumber;
    private String otp;
    private String password;
}
