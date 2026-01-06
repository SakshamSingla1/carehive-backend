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
import com.careHive.enums.StatusEnum;
import com.careHive.exceptions.CarehiveException;
import com.careHive.repositories.ColorThemeRepository;
import com.careHive.services.ColorThemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ColorThemeServiceImpl implements ColorThemeService {

    private final ColorThemeRepository repository;

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
                .status(StatusEnum.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .updatedBy(RoleEnum.ADMIN.name())
                .build();

        repository.save(theme);

        return mapToResponse(theme);
    }

    @Override
    public ColorThemeResponseDTO updateTheme(String id, ColorThemeRequestDTO dto) throws CarehiveException {

        ColorTheme theme = repository.findById(id)
                .orElseThrow(() ->
                        new CarehiveException(ExceptionCodeEnum.COLOR_THEME_NOT_FOUND, "Theme not found"));

        theme.setThemeName(dto.getThemeName());
        theme.setRole(dto.getRole());
        theme.setPalette(mapPaletteDtoToEntity(dto.getPalette()));
        theme.setStatus(dto.getStatus());
        theme.setUpdatedAt(LocalDateTime.now());
        theme.setUpdatedBy(RoleEnum.ADMIN.name());
        repository.save(theme);
        return mapToResponse(theme);
    }

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

    @Override
    public Page<ColorThemeResponseDTO> getAllThemes(
            String search,
            String sortBy,
            String sortDir,
            StatusEnum status,
            RoleEnum role,
            Pageable pageable
    ) {

        Sort sort = Sort.by(
                "desc".equalsIgnoreCase(sortDir)
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC,
                (sortBy != null && !sortBy.isBlank()) ? sortBy : "createdAt"
        );

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );

        boolean hasSearch = search != null && !search.isBlank();
        boolean hasRole = role != null;
        boolean hasStatus = status != null;

        Page<ColorTheme> colorThemes;

        if (hasSearch && hasRole && hasStatus) {
            colorThemes = repository.searchByThemeNameAndRoleAndStatus(
                    search, role, status, sortedPageable
            );
        } else if (hasRole && hasStatus) {
            colorThemes = repository.findByRoleAndStatus(
                    role, status, sortedPageable
            );
        } else if (hasRole) {
            colorThemes = repository.findByRole(
                    role, sortedPageable
            );
        } else if (hasStatus) {
            colorThemes = repository.findByStatus(
                    status, sortedPageable
            );
        } else if (hasSearch) {
            colorThemes = repository.searchByThemeName(
                    search, sortedPageable
            );
        } else {
            colorThemes = repository.findAll(sortedPageable);
        }

        return colorThemes.map(this::mapToResponse);
    }

    @Override
    public String deleteTheme(String id) throws CarehiveException {

        ColorTheme theme = repository.findById(id)
                .orElseThrow(() ->
                        new CarehiveException(ExceptionCodeEnum.COLOR_THEME_NOT_FOUND, "Theme not found"));

        repository.delete(theme);

        return "Theme deleted successfully";
    }

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
                .status(theme.getStatus())
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
