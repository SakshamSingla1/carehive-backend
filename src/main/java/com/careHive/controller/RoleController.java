package com.careHive.controller;

import com.careHive.dtos.Role.RoleRequestDTO;
import com.careHive.dtos.Role.RoleResponseDTO;
import com.careHive.exceptions.CarehiveException;
import com.careHive.payload.ApiResponse;
import com.careHive.payload.ResponseModel;
import com.careHive.services.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@Tag(name = "Role", description = "Endpoints for managing user roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Operation(summary = "Create Role", description = "Creates a new role record.")
    @PostMapping
    public ResponseEntity<ResponseModel<RoleResponseDTO>> createRole(@RequestBody RoleRequestDTO req) throws CarehiveException {
        RoleResponseDTO response = roleService.createRole(req);
        return ApiResponse.respond(response, "Role created successfully", "Failed to create role");
    }

    @Operation(summary = "Update Role", description = "Updates an existing role by ID.")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<RoleResponseDTO>> updateRole(
            @PathVariable String id,
            @RequestBody RoleRequestDTO req) throws CarehiveException {
        RoleResponseDTO response = roleService.updateRole(id, req);
        return ApiResponse.respond(response, "Role updated successfully", "Failed to update role");
    }

    @Operation(summary = "Get Role by ID", description = "Fetches a specific role by ID.")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<RoleResponseDTO>> getRoleById(@PathVariable String id) throws CarehiveException {
        RoleResponseDTO response = roleService.getRoleById(id);
        return ApiResponse.respond(response, "Role fetched successfully", "Failed to fetch role");
    }

    @Operation(summary = "Delete Role", description = "Deletes a role by ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<String>> deleteRole(@PathVariable String id) throws CarehiveException {
        roleService.deleteRole(id);
        return ApiResponse.respond(null,"Role deleted successfully", "Role deletion failed");
    }

    @Operation(summary = "Get All Roles", description = "Fetches all roles available in the system.")
    @GetMapping
    public ResponseEntity<ResponseModel<List<RoleResponseDTO>>> getAllRoles() {
        List<RoleResponseDTO> response = roleService.getAllRoles();
        return ApiResponse.respond(response, "Roles fetched successfully", "Failed to fetch roles");
    }
}
