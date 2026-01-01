package com.careHive.controller;

import com.careHive.dtos.Documents.DocumentRequestDTO;
import com.careHive.dtos.Documents.DocumentResponseDTO;
import com.careHive.exceptions.CarehiveException;
import com.careHive.payload.ApiResponse;
import com.careHive.payload.ResponseModel;
import com.careHive.services.CloudinaryService;
import com.careHive.services.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class UploadController {

    private final CloudinaryService cloudinaryService;
    private final DocumentService documentService;

    @PostMapping("/{caretakerId}")
    public ResponseEntity<ResponseModel<DocumentResponseDTO>> uploadDocument(
            @PathVariable String caretakerId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "false") boolean isPrivate
    ) throws Exception {

        Map uploadResult = cloudinaryService.uploadFile(file);

        DocumentRequestDTO dto = new DocumentRequestDTO();
        dto.setCaretakerId(caretakerId);
        dto.setFileName(file.getOriginalFilename());
        dto.setFileUrl(uploadResult.get("secure_url").toString());
        dto.setPublicId(uploadResult.get("public_id").toString());
        dto.setPrivate(isPrivate);

        DocumentResponseDTO response = documentService.addDocument(dto);

        return ApiResponse.respond(
                response,
                "Document uploaded successfully",
                "Failed to upload document"
        );
    }

    @GetMapping("/{caretakerId}")
    public ResponseEntity<ResponseModel<List<DocumentResponseDTO>>> getDocuments(
            @PathVariable String caretakerId
    ) throws CarehiveException {
        return ApiResponse.respond(
                documentService.getUserDocuments(caretakerId),
                "Documents fetched successfully",
                "Failed to fetch documents"
        );
    }

    @DeleteMapping("/{caretakerId}/{documentId}")
    public ResponseEntity<ResponseModel<String>> deleteDocument(
            @PathVariable String caretakerId,
            @PathVariable String documentId
    ) throws Exception {

        cloudinaryService.deleteFile(documentId);
        documentService.deleteDocument(caretakerId, documentId);

        return ApiResponse.respond(
                "Document deleted successfully",
                "Document deleted successfully",
                "Failed to delete document"
        );
    }
}
