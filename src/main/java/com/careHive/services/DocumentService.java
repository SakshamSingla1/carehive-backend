package com.careHive.services;

import com.careHive.dtos.Documents.DocumentRequestDTO;
import com.careHive.dtos.Documents.DocumentResponseDTO;
import com.careHive.exceptions.CarehiveException;

import java.util.List;

public interface DocumentService {
    DocumentResponseDTO addDocument(DocumentRequestDTO request) throws CarehiveException;

    List<DocumentResponseDTO> getUserDocuments(String caretakerId) throws CarehiveException;

    void deleteDocument(String caretakerId, String documentId) throws CarehiveException;
}
