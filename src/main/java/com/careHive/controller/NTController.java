package com.careHive.controller;

import com.careHive.dtos.NavLinks.NavLinkResponseDTO;
import com.careHive.dtos.NotificationTemplate.NTRequestDTO;
import com.careHive.dtos.NotificationTemplate.NTResponseDTO;
import com.careHive.enums.ExceptionCodeEnum;
import com.careHive.enums.RoleEnum;
import com.careHive.enums.StatusEnum;
import com.careHive.exceptions.CarehiveException;
import com.careHive.payload.ApiResponse;
import com.careHive.payload.ResponseModel;
import com.careHive.services.NTService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/templates")
@RequiredArgsConstructor
public class NTController {

    private final NTService ntService;

    @PostMapping
    public ResponseEntity<ResponseModel<NTResponseDTO>> createTemplate(@RequestBody NTRequestDTO ntRequestDTO) throws CarehiveException {
        NTResponseDTO response = ntService.createNT(ntRequestDTO);
        return ApiResponse.respond(
                response,
                "Notification template created successfully",
                "Failed to create notification template"
        );
    }

    @PutMapping("/{name}")
    public ResponseEntity<ResponseModel<NTResponseDTO>> updateTemplate(
            @PathVariable String name,
            @RequestBody NTRequestDTO ntRequestDTO
    ) throws CarehiveException {
        NTResponseDTO response = ntService.updateNT(name, ntRequestDTO);
        return ApiResponse.respond(
                response,
                "Notification template updated successfully",
                "Failed to update notification template"
        );
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<ResponseModel<String>> deleteTemplate(@PathVariable String name) throws CarehiveException {
        String message = ntService.deleteNT(name);
        return ApiResponse.respond(
                message,
                "Notification template deleted successfully",
                "Failed to delete notification template"
        );
    }

    @GetMapping("/{name}")
    public ResponseEntity<ResponseModel<NTResponseDTO>> getTemplate(@PathVariable String name) throws CarehiveException {
        NTResponseDTO response = ntService.findNTBy(name);
        return ApiResponse.respond(
                response,
                "Notification template fetched successfully",
                "Failed to fetch notification template"
        );
    }

    @GetMapping
    public ResponseEntity<ResponseModel<Page<NTResponseDTO>>> getAllTemplates(
            Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "updatedAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir,
            @RequestParam(required = false)StatusEnum status
            ) {
        Page<NTResponseDTO> responseDTO = ntService.getAllNotificationTemplates(pageable, search, status, sortBy, sortDir);
        return ApiResponse.respond(responseDTO, "Notification Templates fetched successfully", "Failed to fetch Notification Templates");
    }
}
