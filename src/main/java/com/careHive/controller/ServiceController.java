package com.careHive.controller;

import com.careHive.dtos.Service.ServiceRequestDTO;
import com.careHive.dtos.Service.ServiceResponseDTO;
import com.careHive.exceptions.CarehiveException;
import com.careHive.payload.ApiResponse;
import com.careHive.payload.ResponseModel;
import com.careHive.services.ServiceService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    // ✅ CREATE SERVICE
    @Operation(summary = "Create a new service")
    @PostMapping
    public ResponseEntity<ResponseModel<ServiceResponseDTO>> createService(
            @RequestBody ServiceRequestDTO serviceRequestDTO) throws CarehiveException {
        ServiceResponseDTO response = serviceService.createService(serviceRequestDTO);
        return ApiResponse.respond(response, "Service created successfully", "Failed to create service");
    }

    // ✅ UPDATE SERVICE
    @Operation(summary = "Update an existing service")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<ServiceResponseDTO>> updateService(
            @PathVariable String id,
            @RequestBody ServiceRequestDTO serviceRequestDTO) throws CarehiveException {
        ServiceResponseDTO response = serviceService.updateService(id, serviceRequestDTO);
        return ApiResponse.respond(response, "Service updated successfully", "Failed to update service");
    }

    // ✅ DELETE SERVICE
    @Operation(summary = "Delete a service by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<String>> deleteService(@PathVariable String id) throws CarehiveException {
        String message = serviceService.deleteService(id);
        return ApiResponse.respond(message, "Service deleted successfully", "Failed to delete service");
    }

    // ✅ GET SINGLE SERVICE
    @Operation(summary = "Get service details by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<ServiceResponseDTO>> getService(@PathVariable String id)
            throws CarehiveException {
        ServiceResponseDTO response = serviceService.getService(id);
        return ApiResponse.respond(response, "Service fetched successfully", "Failed to fetch service");
    }

    // ✅ GET ALL SERVICES
    @Operation(summary = "Get list of all services")
    @GetMapping
    public ResponseEntity<ResponseModel<List<ServiceResponseDTO>>> getAllServices() throws CarehiveException {
        List<ServiceResponseDTO> response = serviceService.getAllServices();
        return ApiResponse.respond(response, "All services fetched successfully", "Failed to fetch services");
    }

    @Operation(summary = "Assign one or more services to a caretaker")
    @PutMapping("/assign/{caretakerId}")
    public ResponseEntity<ResponseModel<String>> assignServicesToCaretaker(
            @PathVariable String caretakerId,
            @RequestBody List<String> serviceIds) throws CarehiveException {
        serviceService.assignServicesToCaretaker(caretakerId,serviceIds);
        return ApiResponse.respond("Services assigned successfully",
                "Services assigned successfully", "Failed to assign services");
    }
}
