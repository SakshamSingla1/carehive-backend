package com.careHive.services;

import com.careHive.dtos.NavLinks.NavLinkRequestDTO;
import com.careHive.dtos.NavLinks.NavLinkResponseDTO;
import com.careHive.enums.RoleEnum;
import com.careHive.exceptions.CarehiveException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NavLinkService {
    NavLinkResponseDTO createNavLink(NavLinkRequestDTO request) throws CarehiveException;
    NavLinkResponseDTO updateNavLink(String roleIndex, RoleEnum role, NavLinkRequestDTO request) throws CarehiveException;
    void deleteNavLink(String roleIndex, RoleEnum role) throws CarehiveException;
    List<NavLinkResponseDTO> getNavLinks(RoleEnum role);
    Page<NavLinkResponseDTO> getAllNavLinks(Pageable pageable, RoleEnum role, String search, String sortBy, String sortDir);
    NavLinkResponseDTO getNavLink(RoleEnum role, String roleIndex) throws CarehiveException;
}

