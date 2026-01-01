package com.careHive.dtos.Documents;

import lombok.Data;

@Data
public class DocumentRequestDTO {
    private String caretakerId;
    private String fileName;
    private String fileUrl;
    private String publicId;
    private boolean isPrivate;
}
