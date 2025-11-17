package com.careHive.controller;

import com.careHive.dtos.ColorTheme.ColorThemeRequestDTO;
import com.careHive.dtos.ColorTheme.ColorThemeResponseDTO;
import com.careHive.enums.RoleEnum;
import com.careHive.exceptions.CarehiveException;
import com.careHive.payload.ApiResponse;
import com.careHive.payload.ResponseModel;
import com.careHive.services.ColorThemeService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/color-themes")
@RequiredArgsConstructor
public class ColorThemeController {

    private final ColorThemeService colorThemeService;

    // ---------------------------------------------------------
    // CREATE THEME
    // ---------------------------------------------------------
    @PostMapping
    public ResponseEntity<ResponseModel<ColorThemeResponseDTO>> createTheme(
            @RequestBody ColorThemeRequestDTO dto) throws CarehiveException {

        ColorThemeResponseDTO response = colorThemeService.createTheme(dto);

        return ApiResponse.respond(
                response,
                "Color theme created successfully",
                "Failed to create color theme"
        );
    }

    // ---------------------------------------------------------
    // UPDATE THEME
    // ---------------------------------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<ColorThemeResponseDTO>> updateTheme(
            @PathVariable String id,
            @RequestBody ColorThemeRequestDTO dto) throws CarehiveException {

        ColorThemeResponseDTO response = colorThemeService.updateTheme(id, dto);

        return ApiResponse.respond(
                response,
                "Color theme updated successfully",
                "Failed to update color theme"
        );
    }

    // ---------------------------------------------------------
    // GET ONE THEME BY ROLE + THEME NAME
    // ---------------------------------------------------------
    @GetMapping("/{role}/{themeName}")
    public ResponseEntity<ResponseModel<ColorThemeResponseDTO>> getThemeByRoleAndName(
            @PathVariable RoleEnum role,
            @PathVariable String themeName) throws CarehiveException {

        ColorThemeResponseDTO response = colorThemeService.getThemeByRoleAndName(role, themeName);

        return ApiResponse.respond(
                response,
                "Theme fetched successfully",
                "Failed to fetch theme"
        );
    }

    // ---------------------------------------------------------
    // GET ALL THEMES OF A ROLE
    // ---------------------------------------------------------
    @GetMapping("/role/{role}")
    public ResponseEntity<ResponseModel<List<ColorThemeResponseDTO>>> getThemesByRole(
            @PathVariable RoleEnum role) {

        List<ColorThemeResponseDTO> response = colorThemeService.getThemesByRole(role);

        return ApiResponse.respond(
                response,
                "Themes fetched successfully",
                "Failed to fetch themes"
        );
    }

    // ---------------------------------------------------------
    // GET ALL THEMES
    // ---------------------------------------------------------
    @GetMapping
    public ResponseEntity<ResponseModel<List<ColorThemeResponseDTO>>> getAllThemes() {

        List<ColorThemeResponseDTO> response = colorThemeService.getAllThemes();

        return ApiResponse.respond(
                response,
                "All color themes fetched successfully",
                "Failed to fetch color themes"
        );
    }

    // ---------------------------------------------------------
    // DELETE THEME
    // ---------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<String>> deleteTheme(
            @PathVariable String id) throws CarehiveException {

        String response = colorThemeService.deleteTheme(id);

        return ApiResponse.respond(
                response,
                "Color theme deleted successfully",
                "Failed to delete color theme"
        );
    }
}
