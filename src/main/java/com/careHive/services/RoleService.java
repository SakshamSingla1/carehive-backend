package com.careHive.services;

import com.careHive.dtos.Role.RoleRequestDTO;
import com.careHive.dtos.Role.RoleResponseDTO;

import java.util.List;

public interface RoleService {

    RoleResponseDTO createRole(RoleRequestDTO roleRequestDTO);

    RoleResponseDTO updateRole(String roleId, RoleRequestDTO roleRequestDTO);

    RoleResponseDTO getRoleById(String roleId);

    List<RoleResponseDTO> getAllRoles();

    void deleteRole(String roleId);
}
