package com.careHive.repositories;

import com.careHive.entities.Elder;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ElderRepository extends MongoRepository<Elder, String> {
    Optional<Elder> findByUserId(String userId);
    void deleteByUserId(String userId);
}
