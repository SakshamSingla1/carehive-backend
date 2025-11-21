package com.careHive.services;

import com.careHive.dtos.NavLinks.NavLinkRequestDTO;
import com.careHive.dtos.NavLinks.NavLinkResponseDTO;
import com.careHive.enums.RoleEnum;
import com.careHive.exceptions.CarehiveException;

import java.util.List;

public interface NavLinkService {
    NavLinkResponseDTO createNavLink(NavLinkRequestDTO request) throws CarehiveException;
    NavLinkResponseDTO updateNavLink(String roleIndex, RoleEnum role, NavLinkRequestDTO request) throws CarehiveException;
    void deleteNavLink(String roleIndex, RoleEnum role) throws CarehiveException;
    List<NavLinkResponseDTO> getNavLinks(RoleEnum role);
    List<NavLinkResponseDTO> getAllNavLinks();
    NavLinkResponseDTO getNavLink(RoleEnum role, String roleIndex) throws CarehiveException;
}

