package com.careHive.repositories;

import com.careHive.entities.ColorTheme;
import com.careHive.enums.RoleEnum;
import com.careHive.enums.StatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.awt.*;
import java.util.List;
import java.util.Optional;

@Repository
public interface ColorThemeRepository extends MongoRepository<ColorTheme, String> {

    List<ColorTheme> findByRole(RoleEnum role);

    Optional<ColorTheme> findByRoleAndThemeName(RoleEnum role, String themeName);

    Page<ColorTheme> findAll(Pageable pageable);

    Page<ColorTheme> findByRole(RoleEnum role, Pageable pageable);

    Page<ColorTheme> findByStatus(StatusEnum status, Pageable pageable);

    Page<ColorTheme> findByRoleAndStatus(RoleEnum role, StatusEnum status, Pageable pageable);

    @Query("""
    {
      $or: [
        { "themeName": { $regex: ?0, $options: "i" } }
      ]
    }
    """)
    Page<ColorTheme> searchByThemeName(String search, Pageable pageable);

    @Query("""
    {
      $and: [
        {
          $or: [
            { "themeName": { $regex: ?0, $options: "i" } }
          ]
        },
        { "role": ?1 },
        { "status": ?2 }
      ]
    }
    """)
    Page<ColorTheme> searchByThemeNameAndRoleAndStatus(
            String search,
            RoleEnum role,
            StatusEnum status,
            Pageable pageable
    );
}
