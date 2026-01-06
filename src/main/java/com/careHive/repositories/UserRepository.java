package com.careHive.repositories;

import com.careHive.entities.Users;
import com.careHive.enums.RoleEnum;
import com.careHive.enums.StatusEnum;
import com.careHive.enums.VerificationStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<Users, String> {

    /* ---------- BASIC FINDERS ---------- */

    Optional<Users> findByEmail(String email);
    Optional<Users> findByUsername(String username);
    Optional<Users> findByPhone(String phone);
    Optional<Users> findByIdAndRoleCode(String id, RoleEnum roleCode);

    long countByRoleCode(RoleEnum roleCode);

    List<Users> findAllByRoleCode(RoleEnum roleCode);
    List<Users> findByIdIn(List<String> ids);

    /* ---------- PAGINATION ---------- */

    Page<Users> findAll(Pageable pageable);
    Page<Users> findByRoleCode(RoleEnum roleCode, Pageable pageable);
    Page<Users> findByStatus(StatusEnum status, Pageable pageable);
    Page<Users> findByRoleCodeAndStatus(RoleEnum roleCode, StatusEnum status, Pageable pageable);

    /* ---------- SEARCH (NAME / USERNAME / EMAIL / PHONE) ---------- */

    @Query("""
    {
      $or: [
        { "name":     { $regex: ?0, $options: "i" } },
        { "username": { $regex: ?0, $options: "i" } },
        { "email":    { $regex: ?0, $options: "i" } },
        { "phone":    { $regex: ?0, $options: "i" } }
      ]
    }
    """)
    Page<Users> searchUsers(String search, Pageable pageable);

    /* ---------- SEARCH + ROLE ---------- */

    @Query("""
    {
      $and: [
        {
          $or: [
            { "name":     { $regex: ?0, $options: "i" } },
            { "username": { $regex: ?0, $options: "i" } },
            { "email":    { $regex: ?0, $options: "i" } },
            { "phone":    { $regex: ?0, $options: "i" } }
          ]
        },
        { "roleCode": ?1 }
      ]
    }
    """)
    Page<Users> searchUsersByRole(String search, RoleEnum roleCode, Pageable pageable);

    /* ---------- SEARCH + ROLE + STATUS ---------- */

    @Query("""
    {
      $and: [
        {
          $or: [
            { "name":     { $regex: ?0, $options: "i" } },
            { "username": { $regex: ?0, $options: "i" } },
            { "email":    { $regex: ?0, $options: "i" } },
            { "phone":    { $regex: ?0, $options: "i" } }
          ]
        },
        { "roleCode": ?1 },
        { "status": ?2 }
      ]
    }
    """)
    Page<Users> searchUsers(
            String search,
            RoleEnum roleCode,
            StatusEnum status,
            Pageable pageable
    );
}
