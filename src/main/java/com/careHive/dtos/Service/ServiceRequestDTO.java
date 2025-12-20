package com.careHive.dtos.Service;

import com.careHive.enums.StatusEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServiceRequestDTO {
    private String name;
    private String description;
    private double pricePerHour;
    private StatusEnum status;
}
