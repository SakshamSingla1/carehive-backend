package com.careHive.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentInfo {
    private String documentId;
    private String url;
    private String name;
    private String type;
    private long size;
    private String uploadedBy;
    private java.time.LocalDateTime uploadedAt;
}

