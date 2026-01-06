package com.careHive.controller;

import com.careHive.dtos.CaretakerServices.CSRequestDTO;
import com.careHive.dtos.CaretakerServices.CSResponseDTO;
import com.careHive.dtos.Service.ServiceRequestDTO;
import com.careHive.dtos.Service.ServiceResponseDTO;
import com.careHive.enums.StatusEnum;
import com.careHive.exceptions.CarehiveException;
import com.careHive.payload.ApiResponse;
import com.careHive.payload.ResponseModel;
import com.careHive.services.ServiceService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    @Operation(summary = "Create a new service")
    @PostMapping
    public ResponseEntity<ResponseModel<ServiceResponseDTO>> createService(
            @RequestBody ServiceRequestDTO serviceRequestDTO) throws CarehiveException {

        ServiceResponseDTO response = serviceService.createService(serviceRequestDTO);
        return ApiResponse.respond(response,
                "Service created successfully",
                "Failed to create service");
    }

    @Operation(summary = "Update an existing service")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<ServiceResponseDTO>> updateService(
            @PathVariable String id,
            @RequestBody ServiceRequestDTO serviceRequestDTO) throws CarehiveException {

        ServiceResponseDTO response = serviceService.updateService(id, serviceRequestDTO);
        return ApiResponse.respond(response,
                "Service updated successfully",
                "Failed to update service");
    }

    @Operation(summary = "Delete a service by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<String>> deleteService(
            @PathVariable String id) throws CarehiveException {

        String message = serviceService.deleteService(id);
        return ApiResponse.respond(message,
                "Service deleted successfully",
                "Failed to delete service");
    }

    @Operation(summary = "Get service details by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<ServiceResponseDTO>> getService(
            @PathVariable String id) throws CarehiveException {

        ServiceResponseDTO response = serviceService.getService(id);
        return ApiResponse.respond(response,
                "Service fetched successfully",
                "Failed to fetch service");
    }

    @Operation(summary = "Get all services with pagination, search and sorting")
    @GetMapping
    public ResponseEntity<ResponseModel<Page<ServiceResponseDTO>>> getAllServices(
            Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDir,
            @RequestParam(required = false) StatusEnum status) throws CarehiveException {

        Page<ServiceResponseDTO> response =
                serviceService.getAllServices(pageable, search, sortBy, sortDir, status);

        return ApiResponse.respond(response,
                "Services fetched successfully",
                "Failed to fetch services");
    }

    @Operation(summary = "Assign one or more services to a caretaker")
    @PutMapping("/assign/{caretakerId}")
    public ResponseEntity<ResponseModel<List<CSResponseDTO>>> assignServicesToCaretaker(
            @PathVariable String caretakerId,
            @RequestBody CSRequestDTO requestDTO) throws CarehiveException {

        List<CSResponseDTO> responseDTOS = serviceService.assignServicesToCaretaker(caretakerId,requestDTO);
        return ApiResponse.respond(responseDTOS,
                "Services assigned successfully",
                "Failed to assign services");
    }
}
