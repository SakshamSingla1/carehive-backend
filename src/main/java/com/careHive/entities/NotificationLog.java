package com.careHive.entities;

import com.careHive.enums.NotificationStatusEnum;
import com.careHive.enums.NotificationTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "notification_logs")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationLog {
    @Id
    private String id;
    private String userId;
    private NotificationTypeEnum type;
    private String event;
    private String message;
    private NotificationStatusEnum status;
    private LocalDateTime createdAt;
}
