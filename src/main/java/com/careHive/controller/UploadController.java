package com.careHive.controller;

import com.careHive.entities.DocumentInfo;
import com.careHive.exceptions.CarehiveException;
import com.careHive.payload.ApiResponse;
import com.careHive.payload.ResponseModel;
import com.careHive.services.CloudinaryService;
import com.careHive.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/upload")
@RequiredArgsConstructor
public class UploadController {

    private final CloudinaryService cloudinaryService;
    private final UserService userService;

    /**
     * Upload multiple files for a specific user.
     */
    @PostMapping("/{userId}")
    public ResponseEntity<ResponseModel<List<DocumentInfo>>> uploadMultipleDocuments(
            @PathVariable String userId,
            @RequestParam("files") List<MultipartFile> files
    ) throws Exception {

        List<DocumentInfo> uploadedDocuments = new ArrayList<>();

        for (MultipartFile file : files) {
            Map uploadResult = cloudinaryService.uploadFile(file);

            DocumentInfo documentInfo = DocumentInfo.builder()
                    .documentId(uploadResult.get("public_id").toString())
                    .url(uploadResult.get("secure_url").toString())
                    .name(file.getOriginalFilename())
                    .type(file.getContentType())
                    .size(file.getSize())
                    .uploadedBy(userId)
                    .uploadedAt(LocalDateTime.now())
                    .build();

            // Save each document to user's list
            userService.addDocument(userId, documentInfo);
            uploadedDocuments.add(documentInfo);
        }

        return ApiResponse.respond(uploadedDocuments,
                "All files uploaded successfully",
                "Failed to upload files");
    }

    /**
     * Fetch all uploaded documents of a user.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ResponseModel<List<DocumentInfo>>> getUserDocuments(@PathVariable String userId) throws CarehiveException {
        return ApiResponse.respond(
                userService.getUserDocuments(userId),
                "Documents fetched successfully",
                "Failed to fetch documents"
        );
    }

    /**
     * Delete document both from Cloudinary and user record.
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<ResponseModel<String>> deleteDocument(
            @PathVariable String userId,
            @RequestParam String documentId
    ) throws Exception {

        cloudinaryService.deleteFile(documentId);
        userService.deleteDocument(userId, documentId);

        return ApiResponse.respond(
                "Document deleted successfully",
                "Document deleted successfully",
                "Failed to delete document"
        );
    }
}
