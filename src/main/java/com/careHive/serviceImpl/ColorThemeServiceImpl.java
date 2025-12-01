package com.careHive.serviceImpl;

import com.careHive.dtos.ColorTheme.ColorGroupDTO;
import com.careHive.dtos.ColorTheme.ColorShadeDTO;
import com.careHive.dtos.ColorTheme.ColorPaletteDTO;
import com.careHive.dtos.ColorTheme.ColorThemeRequestDTO;
import com.careHive.dtos.ColorTheme.ColorThemeResponseDTO;
import com.careHive.entities.ColorGroup;
import com.careHive.entities.ColorShade;
import com.careHive.entities.ColorPalette;
import com.careHive.entities.ColorTheme;
import com.careHive.enums.ExceptionCodeEnum;
import com.careHive.enums.RoleEnum;
import com.careHive.exceptions.CarehiveException;
import com.careHive.repositories.ColorThemeRepository;
import com.careHive.services.ColorThemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ColorThemeServiceImpl implements ColorThemeService {

    private final ColorThemeRepository repository;

    // ---------------------------
    // CREATE THEME
    // ---------------------------
    @Override
    public ColorThemeResponseDTO createTheme(ColorThemeRequestDTO dto) throws CarehiveException {

        String themeName = dto.getThemeName();

        if (repository.findByRoleAndThemeName(dto.getRole(), themeName).isPresent()) {
            throw new CarehiveException(ExceptionCodeEnum.COLOR_THEME_ALREADY_EXISTS,
                    "Theme already exists for role '" + dto.getRole().name() + "' and themeName '" + themeName + "'");
        }

        ColorTheme theme = ColorTheme.builder()
                .role(dto.getRole())
                .themeName(themeName)
                .palette(mapPaletteDtoToEntity(dto.getPalette()))
                .updatedAt(LocalDateTime.now())
                .updatedBy("admin") // replace with actual user from security context if available
                .build();

        repository.save(theme);

        return mapToResponse(theme);
    }

    // ---------------------------
    // UPDATE THEME
    // ---------------------------
    @Override
    public ColorThemeResponseDTO updateTheme(String id, ColorThemeRequestDTO dto) throws CarehiveException {

        ColorTheme theme = repository.findById(id)
                .orElseThrow(() ->
                        new CarehiveException(ExceptionCodeEnum.COLOR_THEME_NOT_FOUND, "Theme not found"));

        // Update only palette and metadata (you may allow updating role/themeName if needed)
        theme.setPalette(mapPaletteDtoToEntity(dto.getPalette()));
        theme.setUpdatedAt(LocalDateTime.now());
        theme.setUpdatedBy("admin"); // replace with actual user

        repository.save(theme);

        return mapToResponse(theme);
    }

    // ---------------------------
    // GET BY ROLE + THEME NAME
    // ---------------------------
    @Override
    public ColorThemeResponseDTO getThemeByRoleAndName(RoleEnum role, String themeName) throws CarehiveException {
        if (role == null) {
            throw new CarehiveException(ExceptionCodeEnum.BAD_REQUEST, "Role must be provided");
        }

        ColorTheme theme = repository.findByRoleAndThemeName(role, themeName)
                .orElseThrow(() ->
                        new CarehiveException(ExceptionCodeEnum.COLOR_THEME_NOT_FOUND,
                                "Theme not found for role " + role.name() + " and theme " + themeName));

        return mapToResponse(theme);
    }

    // ---------------------------
    // GET ALL THEMES BY ROLE
    // ---------------------------
    @Override
    public List<ColorThemeResponseDTO> getThemesByRole(RoleEnum role) {
        if (role == null) {
            return List.of();
        }

        return repository.findByRole(role)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ---------------------------
    // GET ALL THEMES
    // ---------------------------
    @Override
    public Page<ColorThemeResponseDTO> getAllThemes(Pageable pageable) {
        Page<ColorTheme> colorThemes= repository.findAll(pageable);
        return colorThemes.map(this::mapToResponse);
    }

    // ---------------------------
    // DELETE THEME
    // ---------------------------
    @Override
    public String deleteTheme(String id) throws CarehiveException {

        ColorTheme theme = repository.findById(id)
                .orElseThrow(() ->
                        new CarehiveException(ExceptionCodeEnum.COLOR_THEME_NOT_FOUND, "Theme not found"));

        repository.delete(theme);

        return "Theme deleted successfully";
    }

    // ============================================================
    // 🔄 DTO → ENTITY MAPPERS
    // ============================================================
    private ColorPalette mapPaletteDtoToEntity(ColorPaletteDTO dto) {
        if (dto == null) return null;

        ColorPalette palette = new ColorPalette();
        List<ColorGroup> groups = dto.getColorGroups() == null ? List.of() :
                dto.getColorGroups().stream().map(this::mapGroupDtoToEntity).collect(Collectors.toList());
        palette.setColorGroups(groups);
        return palette;
    }

    private ColorGroup mapGroupDtoToEntity(ColorGroupDTO dto) {
        if (dto == null) return null;
        ColorGroup group = new ColorGroup();
        group.setGroupName(dto.getGroupName());
        List<ColorShade> levels = dto.getColorShades() == null ? List.of() :
                dto.getColorShades().stream().map(this::mapLevelDtoToEntity).collect(Collectors.toList());
        group.setColorShades(levels);
        return group;
    }

    private ColorShade mapLevelDtoToEntity(ColorShadeDTO dto) {
        if (dto == null) return null;
        ColorShade level = new ColorShade();
        level.setColorName(dto.getColorName());
        level.setColorCode(dto.getColorCode());
        return level;
    }

    // ============================================================
    // 🔄 ENTITY → DTO MAPPERS
    // ============================================================
    private ColorThemeResponseDTO mapToResponse(ColorTheme theme) {
        ColorPaletteDTO paletteDTO = new ColorPaletteDTO();
        paletteDTO.setColorGroups(
                theme.getPalette() == null ? List.of() :
                        theme.getPalette().getColorGroups()
                                .stream()
                                .map(this::mapGroupEntityToDto)
                                .collect(Collectors.toList())
        );

        return ColorThemeResponseDTO.builder()
                .id(theme.getId())
                .role(theme.getRole())
                .themeName(theme.getThemeName())
                .palette(paletteDTO)
                .updatedAt(theme.getUpdatedAt())
                .updatedBy(theme.getUpdatedBy())
                .build();
    }

    private ColorGroupDTO mapGroupEntityToDto(ColorGroup group) {
        ColorGroupDTO dto = new ColorGroupDTO();
        dto.setGroupName(group.getGroupName());
        dto.setColorShades(
                group.getColorShades() == null ? List.of() :
                        group.getColorShades()
                                .stream()
                                .map(this::mapLevelEntityToDto)
                                .collect(Collectors.toList())
        );
        return dto;
    }

    private ColorShadeDTO mapLevelEntityToDto(ColorShade level) {
        ColorShadeDTO dto = new ColorShadeDTO();
        dto.setColorName(level.getColorName());
        dto.setColorCode(level.getColorCode());
        return dto;
    }
}
