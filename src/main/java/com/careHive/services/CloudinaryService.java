package com.careHive.services;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

public interface CloudinaryService {
    public Map uploadFile(MultipartFile file) throws IOException ;
    void deleteFile(String publicId) throws IOException ;
}

