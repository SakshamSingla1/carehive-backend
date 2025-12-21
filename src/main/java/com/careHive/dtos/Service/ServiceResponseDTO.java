package com.careHive.dtos.Service;

import com.careHive.enums.StatusEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ServiceResponseDTO {
    private String id;
    private String name;
    private String description;
    private double pricePerHour;
    private StatusEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
