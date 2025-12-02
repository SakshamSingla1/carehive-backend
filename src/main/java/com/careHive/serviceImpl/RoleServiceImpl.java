package com.careHive.serviceImpl;

import com.careHive.dtos.Role.RoleRequestDTO;
import com.careHive.dtos.Role.RoleResponseDTO;
import com.careHive.entities.Role;
import com.careHive.repositories.RoleRepository;
import com.careHive.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public RoleResponseDTO createRole(RoleRequestDTO roleRequestDTO) {
        Role role = Role.builder()
                .name(roleRequestDTO.getName())
                .enumCode(roleRequestDTO.getEnumCode())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Role savedRole = roleRepository.save(role);

        return mapToResponseDTO(savedRole);
    }

    @Override
    public RoleResponseDTO updateRole(String roleId, RoleRequestDTO roleRequestDTO) {
        Role existingRole = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));

        existingRole.setName(roleRequestDTO.getName());
        existingRole.setEnumCode(roleRequestDTO.getEnumCode());
        existingRole.setUpdatedAt(LocalDateTime.now());

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
        dto.setCreatedAt(role.getCreatedAt());
        dto.setUpdatedAt(role.getUpdatedAt());
        return dto;
    }
}
