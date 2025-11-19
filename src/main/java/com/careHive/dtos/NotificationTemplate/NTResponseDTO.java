package com.careHive.dtos.NotificationTemplate;

import com.careHive.enums.NotificationTypeEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NTResponseDTO {
    private String id;
    private String name;
    private String subject;
    private String body;
    private NotificationTypeEnum type;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
