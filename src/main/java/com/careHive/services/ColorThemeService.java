package com.careHive.services;

import com.careHive.dtos.ColorTheme.ColorThemeRequestDTO;
import com.careHive.dtos.ColorTheme.ColorThemeResponseDTO;
import com.careHive.enums.RoleEnum;
import com.careHive.exceptions.CarehiveException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ColorThemeService {

    ColorThemeResponseDTO createTheme(ColorThemeRequestDTO dto) throws CarehiveException;

    ColorThemeResponseDTO updateTheme(String id, ColorThemeRequestDTO dto) throws CarehiveException;

    ColorThemeResponseDTO getThemeByRoleAndName(RoleEnum role, String themeName) throws CarehiveException;

    // ---------------------------
    // GET ALL THEMES BY ROLE
    // ---------------------------
    List<ColorThemeResponseDTO> getThemesByRole(RoleEnum role);

    Page<ColorThemeResponseDTO> getAllThemes(Pageable pageable);

    String deleteTheme(String id) throws CarehiveException;
}
