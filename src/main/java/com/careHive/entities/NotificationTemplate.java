package com.careHive.entities;

import com.careHive.enums.NotificationTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notification-templates")
public class NotificationTemplate {
    @Id
    private String id;
    private String name;
    private String subject;
    private String body;
    private NotificationTypeEnum type;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
