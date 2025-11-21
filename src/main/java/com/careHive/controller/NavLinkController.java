package com.careHive.controller;

import com.careHive.dtos.NavLinks.NavLinkRequestDTO;
import com.careHive.dtos.NavLinks.NavLinkResponseDTO;
import com.careHive.enums.RoleEnum;
import com.careHive.exceptions.CarehiveException;
import com.careHive.payload.ApiResponse;
import com.careHive.payload.ResponseModel;
import com.careHive.services.NavLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/nav-link")
public class NavLinkController {

    @Autowired
    private NavLinkService navLinkService;

    // CREATE
    @PostMapping
    public ResponseEntity<ResponseModel<NavLinkResponseDTO>> createNavLink(
            @RequestBody NavLinkRequestDTO navLinkRequestDTO) throws CarehiveException {

        NavLinkResponseDTO responseDTO = navLinkService.createNavLink(navLinkRequestDTO);
        return ApiResponse.respond(responseDTO, "Nav Link created successfully", "Failed to create nav link");
    }

    // UPDATE
    @PutMapping("/{role}/{index}")
    public ResponseEntity<ResponseModel<NavLinkResponseDTO>> updateNavLink(
            @PathVariable RoleEnum role,
            @PathVariable String index,
            @RequestBody NavLinkRequestDTO navLinkRequestDTO) throws CarehiveException {

        NavLinkResponseDTO responseDTO = navLinkService.updateNavLink(index, role, navLinkRequestDTO);
        return ApiResponse.respond(responseDTO, "Nav Link updated successfully", "Failed to update nav link");
    }

    // DELETE
    @DeleteMapping("/{role}/{index}")
    public ResponseEntity<ResponseModel<String>> deleteNavLink(
            @PathVariable RoleEnum role,
            @PathVariable String index) throws CarehiveException {

        navLinkService.deleteNavLink(index, role);

        return ApiResponse.respond(
                "OK",
                "Nav Link deleted successfully",
                "Failed to delete nav link"
        );
    }

    // GET ALL BY ROLE
    @GetMapping("/role/{role}")
    public ResponseEntity<ResponseModel<List<NavLinkResponseDTO>>> getNavLinks(@PathVariable RoleEnum role) {
        List<NavLinkResponseDTO> responseDTO = navLinkService.getNavLinks(role);
        return ApiResponse.respond(responseDTO, "Nav Links fetched successfully", "Failed to fetch nav links");
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<ResponseModel<List<NavLinkResponseDTO>>> getAllNavLinks() {
        List<NavLinkResponseDTO> responseDTO = navLinkService.getAllNavLinks();
        return ApiResponse.respond(responseDTO, "Nav Links fetched successfully", "Failed to fetch nav links");
    }

    // GET SINGLE
    @GetMapping("/{role}/{index}")
    public ResponseEntity<ResponseModel<NavLinkResponseDTO>> getNavLink(
            @PathVariable RoleEnum role,
            @PathVariable String index) throws CarehiveException {

        NavLinkResponseDTO responseDTO = navLinkService.getNavLink(role, index);
        return ApiResponse.respond(responseDTO, "Nav Link fetched successfully", "Failed to fetch nav link");
    }
}
