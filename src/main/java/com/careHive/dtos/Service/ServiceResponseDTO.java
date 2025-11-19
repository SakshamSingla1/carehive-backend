package com.careHive.dtos.Service;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServiceResponseDTO {
    private String id;
    private String name;
    private String description;
    private double pricePerHour;
    private Boolean status;
}
