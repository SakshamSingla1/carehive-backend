package com.careHive.dtos.Auth;

import com.careHive.dtos.ColorTheme.ColorThemeResponseDTO;
import com.careHive.entities.ColorTheme;
import com.careHive.entities.NavLink;
import com.careHive.enums.VerificationStatusEnum;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LoginResponseDTO {
    private String id;
    private String name;
    private String username;
    private String phone;
    private String email;
    private String role;
    private String token;
    private VerificationStatusEnum emailVerified;
    private VerificationStatusEnum phoneVerified;
    private List<ColorTheme> themes;
    private ColorTheme defaultTheme;
    private List<NavLink> navLinks;
}
