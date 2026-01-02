package com.careHive.services;

import com.careHive.dtos.Configuration.ConfigurationRequestDTO;
import com.careHive.dtos.Configuration.ConfigurationResponseDTO;
import com.careHive.exceptions.CarehiveException;
import org.springframework.data.domain.Page;

public interface ConfigurationService {
    ConfigurationResponseDTO create(ConfigurationRequestDTO dto) throws CarehiveException;
    ConfigurationResponseDTO update(String id, ConfigurationRequestDTO dto) throws CarehiveException;
    ConfigurationResponseDTO getByContext(String context) throws CarehiveException;
    Page<ConfigurationResponseDTO> getAllPaginated(int page, int size);
    void deleteById(String id) throws CarehiveException;
}
