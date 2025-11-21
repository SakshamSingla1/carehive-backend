package com.careHive.serviceImpl;

import com.careHive.dtos.NavLinks.NavLinkRequestDTO;
import com.careHive.dtos.NavLinks.NavLinkResponseDTO;
import com.careHive.entities.NavLink;
import com.careHive.enums.ExceptionCodeEnum;
import com.careHive.enums.RoleEnum;
import com.careHive.exceptions.CarehiveException;
import com.careHive.repositories.NavLinkRepository;
import com.careHive.services.NavLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NavLinkServiceImpl implements NavLinkService {

    @Autowired
    private NavLinkRepository navLinkRepository;

    @Override
    public NavLinkResponseDTO createNavLink(NavLinkRequestDTO request) throws CarehiveException {
        if (navLinkRepository.existsByRoleCodeAndIndex(request.getRoleCode(), request.getIndex())) {
            throw new CarehiveException(ExceptionCodeEnum.NAV_LINK_ALREADY_EXISTS,"NavLink already exists for this role and index");
        }

        if (navLinkRepository.existsByRoleCodeAndPath(request.getRoleCode(), request.getPath())) {
            throw new CarehiveException(ExceptionCodeEnum.NAV_LINK_ALREADY_EXISTS,"Path already exists for this role");
        }
        NavLink navLink = NavLink.builder()
                .roleCode(request.getRoleCode())
                .index(request.getIndex())
                .name(request.getName())
                .path(request.getPath())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        navLinkRepository.save(navLink);

        return toResponseDTO(navLink);
    }

    @Override
    public NavLinkResponseDTO updateNavLink(String roleIndex, RoleEnum role, NavLinkRequestDTO request) throws CarehiveException {

        NavLink existing = navLinkRepository.findByRoleCodeAndIndex(role, roleIndex)
                .orElseThrow(() -> new CarehiveException(
                        ExceptionCodeEnum.NAV_LINK_NOT_FOUND, "Nav Link not found"
                ));
        NavLink pathConflict = navLinkRepository.findByRoleCodeAndPath(role, request.getPath())
                .orElse(null);
        if (pathConflict != null && !pathConflict.getId().equals(existing.getId())) {
            throw new CarehiveException(
                    ExceptionCodeEnum.NAV_LINK_ALREADY_EXISTS,
                    "Path already exists for this role"
            );
        }
        existing.setName(request.getName());
        existing.setPath(request.getPath());
        existing.setUpdatedAt(LocalDateTime.now());
        navLinkRepository.save(existing);
        return toResponseDTO(existing);
    }

    @Override
    public void deleteNavLink(String roleIndex, RoleEnum role) throws CarehiveException {
        NavLink navLink = navLinkRepository.findByRoleCodeAndIndex(role, roleIndex)
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.NAV_LINK_NOT_FOUND, "Nav Link not found"));
        navLinkRepository.delete(navLink);
    }

    @Override
    public List<NavLinkResponseDTO> getNavLinks(RoleEnum role) {
        List<NavLink> navLinks = navLinkRepository.findByRoleCode(role);
        return navLinks.stream().map(this::toResponseDTO).toList();
    }

    @Override
    public List<NavLinkResponseDTO> getAllNavLinks() {
        List<NavLink> navLinks = navLinkRepository.findAll();
        return navLinks.stream().map(this::toResponseDTO).toList();
    }

    @Override
    public NavLinkResponseDTO getNavLink(RoleEnum role, String roleIndex) throws CarehiveException {
        NavLink navLink = navLinkRepository.findByRoleCodeAndIndex(role, roleIndex)
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.NAV_LINK_NOT_FOUND, "Nav Link not found"));
        return toResponseDTO(navLink);
    }

    private NavLinkResponseDTO toResponseDTO(NavLink navLink) {
        return NavLinkResponseDTO.builder()
                .id(navLink.getId())
                .roleCode(navLink.getRoleCode())
                .index(navLink.getIndex())
                .name(navLink.getName())
                .path(navLink.getPath())
                .createdAt(navLink.getCreatedAt())
                .updatedAt(navLink.getUpdatedAt())
                .build();
    }
}
