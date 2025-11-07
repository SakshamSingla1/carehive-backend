package com.careHive.dtos.NotificationTemplate;

import com.careHive.enums.NotificationTypeEnum;
import lombok.Data;

@Data
public class NTRequestDTO {
    private String name;
    private String subject;
    private String body;
    private NotificationTypeEnum type;
    private boolean active;
}
