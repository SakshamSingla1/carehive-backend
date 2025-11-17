package com.careHive.repositories;

import com.careHive.entities.ColorTheme;
import com.careHive.enums.RoleEnum;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ColorThemeRepository extends MongoRepository<ColorTheme, String> {
    List<ColorTheme> findByRole(RoleEnum role);
    Optional<ColorTheme> findByRoleAndThemeName(RoleEnum role, String themeName);
}
