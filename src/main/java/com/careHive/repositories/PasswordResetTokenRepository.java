package com.careHive.repositories;

import com.careHive.entities.PasswordResetToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends MongoRepository<PasswordResetToken, String> {
    void deleteByEmail(String email);
    Optional<PasswordResetToken> findByToken(String token);
}
