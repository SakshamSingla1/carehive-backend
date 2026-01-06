package com.careHive.dtos.ColorTheme;

import com.careHive.entities.ColorPalette;
import com.careHive.enums.RoleEnum;
import com.careHive.enums.StatusEnum;
import lombok.Data;

@Data
public class ColorThemeRequestDTO {
    private RoleEnum role;
    private String themeName;
    private ColorPaletteDTO palette;
    private StatusEnum status;
}
