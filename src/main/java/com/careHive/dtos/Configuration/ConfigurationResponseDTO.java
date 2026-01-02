package com.careHive.dtos.Configuration;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ConfigurationResponseDTO {
    private String id;
    private String context;
    private String data;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
