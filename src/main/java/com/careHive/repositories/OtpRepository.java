package com.careHive.repositories;

import com.careHive.entities.OtpStore;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends MongoRepository<OtpStore, String> {
    Optional<OtpStore> findByEmail(String email);
    void deleteByEmail(String email);
}
