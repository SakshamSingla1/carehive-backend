package com.careHive.repositories;

import com.careHive.entities.Users;
import com.careHive.enums.RoleEnum;
import com.careHive.enums.VerificationStatusEnum;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<Users, String> {
    Optional<Users> findByEmail(String email);
    Optional<Users> findByUsername(String username);
    Optional<Users> findByPhone(String phoneNumber);
    Optional<Users> findByIdAndRoleCode(String id, RoleEnum roleCode);
    long countByRoleCode(RoleEnum roleCode);
    List<Users> findAllByRoleCode(RoleEnum roleCode);
}
