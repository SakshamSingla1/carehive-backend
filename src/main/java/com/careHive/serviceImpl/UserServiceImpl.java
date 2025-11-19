package com.careHive.serviceImpl;

import com.careHive.entities.DocumentInfo;
import com.careHive.entities.User;
import com.careHive.enums.ExceptionCodeEnum;
import com.careHive.exceptions.CarehiveException;
import com.careHive.repositories.UserRepository;
import com.careHive.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User addDocument(String userId, DocumentInfo document) throws CarehiveException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found"));

        if (user.getDocuments() == null) {
            user.setDocuments(new ArrayList<>());
        }

        user.getDocuments().add(document);
        return userRepository.save(user);
    }

    @Override
    public List<DocumentInfo> getUserDocuments(String userId) throws CarehiveException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found"));

        return user.getDocuments() != null ? user.getDocuments() : new ArrayList<>();
    }

    @Override
    public void deleteDocument(String userId, String documentId) throws CarehiveException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found"));

        if (user.getDocuments() != null && !user.getDocuments().isEmpty()) {
            boolean removed = user.getDocuments().removeIf(doc -> doc.getDocumentId().equals(documentId));
            if (removed) {
                userRepository.save(user);
            } else {
                throw new CarehiveException(ExceptionCodeEnum.BAD_REQUEST, "Document not found for this user");
            }
        }
    }

}
