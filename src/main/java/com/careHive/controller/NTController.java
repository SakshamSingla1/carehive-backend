package com.careHive.controller;

import com.careHive.dtos.NotificationTemplate.NTRequestDTO;
import com.careHive.dtos.NotificationTemplate.NTResponseDTO;
import com.careHive.enums.ExceptionCodeEnum;
import com.careHive.exceptions.CarehiveException;
import com.careHive.payload.ApiResponse;
import com.careHive.payload.ResponseModel;
import com.careHive.services.NTService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<ResponseModel<List<NTResponseDTO>>> getAllTemplates() {
        List<NTResponseDTO> templates = ntService.findAll();
        return ApiResponse.respond(
                templates,
                "Notification templates fetched successfully",
                "Failed to fetch notification templates"
        );
    }
}
