package com.careHive.repositories;

import com.careHive.entities.NavLink;
import com.careHive.enums.RoleEnum;
import com.careHive.enums.StatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NavLinkRepository extends MongoRepository<NavLink, String> {

    boolean existsByRoleAndIndex(RoleEnum role, String index);
    boolean existsByRoleAndPath(RoleEnum role, String path);

    Optional<NavLink> findByRoleAndIndex(RoleEnum role, String index);
    Optional<NavLink> findByRoleAndPath(RoleEnum role, String path);

    List<NavLink> findByRole(RoleEnum role);

    Page<NavLink> findAll(Pageable pageable);

    Page<NavLink> findByStatus(StatusEnum status, Pageable pageable);

    Page<NavLink> findByRole(RoleEnum role, Pageable pageable);

    Page<NavLink> findByStatusAndRole(StatusEnum status, RoleEnum role, Pageable pageable);

    @Query("""
    {
      $or: [
        { "label": { $regex: ?0, $options: "i" } },
        { "path": { $regex: ?0, $options: "i" } },
      ]
    }
    """)
    Page<NavLink> search(String search, Pageable pageable);

    @Query("""
    {
      $and: [
        {
          $or: [
            { "label": { $regex: ?0, $options: "i" } },
            { "path": { $regex: ?0, $options: "i" } },
          ]
        },
        { "status": ?1 }
      ]
    }
    """)
    Page<NavLink> searchByStatus(String search, StatusEnum status, Pageable pageable);

    @Query("""
    {
      $and: [
        {
          $or: [
            { "label": { $regex: ?0, $options: "i" } },
            { "path": { $regex: ?0, $options: "i" } },
          ]
        },
        { "role": ?1 }
      ]
    }
    """)
    Page<NavLink> searchByRole(String search, RoleEnum role, Pageable pageable);

    @Query("""
    {
      $and: [
        {
          $or: [
            { "label": { $regex: ?0, $options: "i" } },
            { "path": { $regex: ?0, $options: "i" } },
          ]
        },
        { "status": ?1 },
        { "role": ?2 }
      ]
    }
    """)
    Page<NavLink> searchByRoleAndStatus(
            String search,
            StatusEnum status,
            RoleEnum role,
            Pageable pageable
    );
}
