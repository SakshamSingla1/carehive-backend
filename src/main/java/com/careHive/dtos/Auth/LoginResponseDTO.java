package com.careHive.dtos.Auth;

import com.careHive.dtos.ColorTheme.ColorThemeResponseDTO;
import com.careHive.entities.ColorTheme;
import com.careHive.entities.NavLink;
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

    private List<ColorTheme> themes;       // ← all themes for this role
    private ColorTheme defaultTheme;        // ← default theme for usage

    private List<NavLink> navLinks;
}
