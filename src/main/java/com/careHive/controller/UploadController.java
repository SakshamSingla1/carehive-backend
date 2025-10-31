package com.careHive.controller;

import com.careHive.payload.ApiResponse;
import com.careHive.payload.ResponseModel;
import com.careHive.services.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/upload")
@RequiredArgsConstructor
public class UploadController {

    private final CloudinaryService cloudinaryService;

    @PostMapping
    public ResponseEntity<ResponseModel<Map>> upload(@RequestParam("file") MultipartFile file) throws Exception {
        Map data = cloudinaryService.uploadFile(file);
        return ApiResponse.respond(data, "Uploaded Successfully","Failed to Upload");
    }
}
