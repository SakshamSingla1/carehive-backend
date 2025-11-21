package com.careHive.repositories;

import com.careHive.entities.NotificationTemplate;
import com.careHive.enums.NotificationTypeEnum;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NTRepository extends MongoRepository<NotificationTemplate, String> {
    Optional<NotificationTemplate> findByName(String name);
    boolean existsByNameAndType(String name, NotificationTypeEnum type);
}
