package com.careHive.dtos.Auth;

import com.careHive.enums.RoleEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthRegisterDTO {
    private String name;
    private String username;
    private String email;
    private String password;
    private RoleEnum roleCode;
    private String phoneNumber;
}
