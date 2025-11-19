package com.careHive.repositories;

import com.careHive.entities.Role;
import com.careHive.entities.User;
import com.careHive.enums.RoleEnum;
import com.careHive.enums.VerificationStatusEnum;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByIdAndRoleCode(String id, String roleCode);

    long countByRoleCode(String roleCode);

    long countByRoleCodeAndCaretakerStatus(String roleCode, VerificationStatusEnum caretakerStatus);
}
