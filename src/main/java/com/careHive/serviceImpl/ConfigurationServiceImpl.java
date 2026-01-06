package com.careHive.serviceImpl;

import com.careHive.dtos.Configuration.ConfigurationRequestDTO;
import com.careHive.dtos.Configuration.ConfigurationResponseDTO;
import com.careHive.entities.Configuration;
import com.careHive.enums.ExceptionCodeEnum;
import com.careHive.exceptions.CarehiveException;
import com.careHive.repositories.ConfigurationRepository;
import com.careHive.services.ConfigurationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfigurationServiceImpl implements ConfigurationService {

    private final ConfigurationRepository configurationRepository;

    @Override
    public ConfigurationResponseDTO create(ConfigurationRequestDTO dto) throws CarehiveException {
        if (configurationRepository.existsByContext(dto.getContext())) {
            throw new CarehiveException(
                    ExceptionCodeEnum.DUPLICATE_RESOURCE,
                    "Configuration already exists for context"
            );
        }
        Configuration config = Configuration.builder()
                .context(dto.getContext())
                .data(dto.getData())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return toDTO(configurationRepository.save(config));
    }

    @Override
    public ConfigurationResponseDTO update(String id, ConfigurationRequestDTO dto) throws CarehiveException {
        Configuration config = configurationRepository.findById(id)
                .orElseThrow(() -> new CarehiveException(
                        ExceptionCodeEnum.RESOURCE_NOT_FOUND,
                        "Configuration not found"
                ));

        if (dto.getContext() != null) config.setContext(dto.getContext());
        if (dto.getData() != null) config.setData(dto.getData());

        config.setUpdatedAt(LocalDateTime.now());
        return toDTO(configurationRepository.save(config));
    }

    @Override
    public ConfigurationResponseDTO getByContext(String context) throws CarehiveException {
        return configurationRepository.findByContext(context)
                .map(this::toDTO)
                .orElseThrow(() -> new CarehiveException(
                        ExceptionCodeEnum.RESOURCE_NOT_FOUND,
                        "Configuration not found"
                ));
    }

    @Override
    public Page<ConfigurationResponseDTO> getAllPaginated(
            Pageable pageable,
            String sortBy,
            String sortDir,
            String search
    ) {

        Sort sort = Sort.by(
                "desc".equalsIgnoreCase(sortDir)
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC,
                (sortBy != null && !sortBy.isBlank()) ? sortBy : "createdAt"
        );

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );

        Page<Configuration> configurations;

        if (search != null && !search.isBlank()) {
            configurations = configurationRepository.search(search, sortedPageable);
        } else {
            configurations = configurationRepository.findAll(sortedPageable);
        }
        return configurations.map(this::toDTO);
    }

    @Override
    public void deleteById(String id) throws CarehiveException {
        if (!configurationRepository.existsById(id)) {
            throw new CarehiveException(
                    ExceptionCodeEnum.RESOURCE_NOT_FOUND,
                    "Configuration not found"
            );
        }
        configurationRepository.deleteById(id);
    }

    private ConfigurationResponseDTO toDTO(Configuration config) {
        return ConfigurationResponseDTO.builder()
                .id(config.getId())
                .context(config.getContext())
                .data(config.getData())
                .createdAt(config.getCreatedAt())
                .updatedAt(config.getUpdatedAt())
                .build();
    }
}
