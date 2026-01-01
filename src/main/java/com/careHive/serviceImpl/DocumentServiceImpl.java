package com.careHive.serviceImpl;

import com.careHive.dtos.Documents.DocumentRequestDTO;
import com.careHive.dtos.Documents.DocumentResponseDTO;
import com.careHive.entities.Documents;
import com.careHive.enums.ExceptionCodeEnum;
import com.careHive.exceptions.CarehiveException;
import com.careHive.repositories.DocumentRepository;
import com.careHive.services.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;

    @Override
    public DocumentResponseDTO addDocument(DocumentRequestDTO dto) {

        Documents document = Documents.builder()
                .caretakerId(dto.getCaretakerId())
                .fileName(dto.getFileName())
                .fileUrl(dto.getFileUrl())
                .publicId(dto.getPublicId())
                .isPrivate(dto.isPrivate())
                .uploadedAt(LocalDateTime.now())
                .build();

        Documents saved = documentRepository.save(document);
        return toResponse(saved);
    }


    @Override
    public List<DocumentResponseDTO> getUserDocuments(String caretakerId) {

        return documentRepository.findByCaretakerId(caretakerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void deleteDocument(String caretakerId, String documentId) throws CarehiveException {

        Documents doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new CarehiveException(
                        ExceptionCodeEnum.BAD_REQUEST, "Document not found"));

        if (!doc.getCaretakerId().equals(caretakerId)) {
            throw new CarehiveException(
                    ExceptionCodeEnum.UNAUTHORIZED, "Access denied");
        }

        documentRepository.deleteById(documentId);
    }

    private DocumentResponseDTO toResponse(Documents doc){
        return DocumentResponseDTO.builder()
                .documentId(doc.getId())
                .caretakerId(doc.getCaretakerId())
                .fileName(doc.getFileName())
                .fileUrl(doc.getFileUrl())
                .isPrivate(doc.isPrivate())
                .uploadedAt(doc.getUploadedAt())
                .build();
    }
}
