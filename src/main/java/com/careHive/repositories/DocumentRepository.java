package com.careHive.repositories;

import com.careHive.entities.Documents;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DocumentRepository extends MongoRepository<Documents, String> {
    Optional<Documents> findByCaretakerId(String caretakerId);
}
