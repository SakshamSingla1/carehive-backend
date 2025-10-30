package com.careHive.dtos.Password;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PasswordResetRequestDTO {
    private String email;
}
