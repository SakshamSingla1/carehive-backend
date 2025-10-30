package com.careHive.serviceImpl;

import com.careHive.dtos.Role.RoleRequestDTO;
import com.careHive.dtos.Role.RoleResponseDTO;
import com.careHive.entities.Role;
import com.careHive.repositories.RoleRepository;
import com.careHive.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public RoleResponseDTO createRole(RoleRequestDTO roleRequestDTO) {
        Role role = new Role();
        role.setName(roleRequestDTO.getName());
        role.setEnumCode(roleRequestDTO.getEnumCode());

        Role savedRole = roleRepository.save(role);

        return mapToResponseDTO(savedRole);
    }

    @Override
    public RoleResponseDTO updateRole(String roleId, RoleRequestDTO roleRequestDTO) {
        Role existingRole = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));

        existingRole.setName(roleRequestDTO.getName());
        existingRole.setEnumCode(roleRequestDTO.getEnumCode());

        Role updatedRole = roleRepository.save(existingRole);

        return mapToResponseDTO(updatedRole);
    }

    @Override
    public RoleResponseDTO getRoleById(String roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));

        return mapToResponseDTO(role);
    }

    @Override
    public List<RoleResponseDTO> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteRole(String roleId) {
        if (!roleRepository.existsById(roleId)) {
            throw new RuntimeException("Role not found with id: " + roleId);
        }
        roleRepository.deleteById(roleId);
    }

    private RoleResponseDTO mapToResponseDTO(Role role) {
        RoleResponseDTO dto = new RoleResponseDTO();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setEnumCode(role.getEnumCode());
        return dto;
    }
}
