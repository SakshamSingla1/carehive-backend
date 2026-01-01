package com.careHive.repositories;

import com.careHive.entities.Documents;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends MongoRepository<Documents, String> {
    List<Documents> findByCaretakerId(String caretakerId);
}
