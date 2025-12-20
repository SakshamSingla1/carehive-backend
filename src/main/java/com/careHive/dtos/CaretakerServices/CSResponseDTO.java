package com.careHive.dtos.CaretakerServices;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CSResponseDTO {
    private String id;
    private String caretakerId;
    private String caretakerName;
    private String serviceId;
    private String serviceName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
