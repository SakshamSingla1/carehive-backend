package com.careHive.services;

import com.careHive.entities.DocumentInfo;
import com.careHive.entities.User;
import com.careHive.exceptions.CarehiveException;

import java.util.List;

public interface UserService {
    User addDocument(String userId, DocumentInfo document) throws CarehiveException;
    List<DocumentInfo> getUserDocuments(String userId) throws CarehiveException;
    void deleteDocument(String userId, String publicId) throws CarehiveException;
}
