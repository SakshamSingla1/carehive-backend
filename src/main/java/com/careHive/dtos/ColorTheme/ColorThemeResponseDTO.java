package com.careHive.dtos.ColorTheme;

import com.careHive.enums.RoleEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ColorThemeResponseDTO {
    private String id;
    private RoleEnum role;
    private String themeName;
    private ColorPaletteDTO palette;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
