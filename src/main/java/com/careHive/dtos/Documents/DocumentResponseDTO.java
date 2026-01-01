package com.careHive.dtos.Documents;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DocumentResponseDTO {
    private String documentId;
    private String caretakerId;
    private String fileName;
    private String fileUrl;
    private boolean isPrivate;
    private LocalDateTime uploadedAt;
}