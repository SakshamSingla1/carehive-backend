package com.careHive.dtos.ColorTheme;

import lombok.Data;

import java.util.List;

@Data
public class ColorPaletteDTO {
    private List<ColorGroupDTO> colorGroups;
}
