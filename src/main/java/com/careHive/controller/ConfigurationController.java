package com.careHive.controller;

import com.careHive.dtos.Configuration.ConfigurationRequestDTO;
import com.careHive.dtos.Configuration.ConfigurationResponseDTO;
import com.careHive.exceptions.CarehiveException;
import com.careHive.payload.ApiResponse;
import com.careHive.payload.ResponseModel;
import com.careHive.services.ConfigurationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/configurations")
@RequiredArgsConstructor
public class ConfigurationController {
    private final ConfigurationService configurationService;

    @PostMapping
    public ResponseEntity<ResponseModel<ConfigurationResponseDTO>> createConfiguration(@RequestBody ConfigurationRequestDTO dto) throws CarehiveException {
        ConfigurationResponseDTO responseDTO = configurationService.create(dto);
        return ApiResponse.respond(responseDTO,"Configuration added successfully","Failed to create configuration");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<ConfigurationResponseDTO>> updateConfiguration(@PathVariable String id, @RequestBody ConfigurationRequestDTO dto) throws CarehiveException {
        ConfigurationResponseDTO responseDTO = configurationService.update(id,dto);
        return ApiResponse.respond(responseDTO,"Configuration updated successfully","Failed to update configuration");
    }

    @GetMapping("/{context}")
    public ResponseEntity<ResponseModel<ConfigurationResponseDTO>> getByContext(@PathVariable String context) throws CarehiveException {
        ConfigurationResponseDTO responseDTO = configurationService.getByContext(context);
        return ApiResponse.respond(responseDTO,"Configuration fetched successfully","Failed to fetch configuration");
    }

    @GetMapping
    public ResponseEntity<ResponseModel<Page<ConfigurationResponseDTO>>> getAll(
            Pageable pageable,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDir,
            @RequestParam(required = false) String search
    ) {
        Page<ConfigurationResponseDTO> responseList =
                configurationService.getAllPaginated(pageable, sortBy, sortDir, search);

        return ApiResponse.respond(
                responseList,
                "Configurations fetched successfully",
                "Failed to fetch configurations"
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<String>> deleteConfiguration(@PathVariable String id) throws CarehiveException {
        configurationService.deleteById(id);
        return ApiResponse.respond(null,"Configurations deleted successfully","Failed to delete configurations");
    }

}
