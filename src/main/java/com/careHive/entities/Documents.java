package com.careHive.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "documents")
public class Documents {
    @Id
    private String id;
    private String caretakerId;
    private String fileName;
    private String fileUrl;
    private String publicId;
    private boolean isPrivate;
    private LocalDateTime uploadedAt;
}
