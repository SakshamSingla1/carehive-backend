package com.careHive.dtos.Configuration;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConfigurationRequestDTO {
    private String context;
    private String data;
}
