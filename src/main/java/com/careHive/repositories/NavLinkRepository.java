package com.careHive.repositories;

import com.careHive.entities.NavLink;
import com.careHive.enums.RoleEnum;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NavLinkRepository extends MongoRepository<NavLink, String> {
    boolean existsByRoleCodeAndIndex(RoleEnum roleCode, String index);
    boolean existsByRoleCodeAndPath(RoleEnum roleCode, String path);
    Optional<NavLink> findByRoleCodeAndIndex(RoleEnum roleCode, String index);
    Optional<NavLink> findByRoleCodeAndPath(RoleEnum roleCode, String path);
    List<NavLink> findByRoleCode(RoleEnum roleCode);
}
