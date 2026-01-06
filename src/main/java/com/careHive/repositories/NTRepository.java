package com.careHive.repositories;

import com.careHive.entities.NotificationTemplate;
import com.careHive.enums.NotificationTypeEnum;
import com.careHive.enums.StatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NTRepository extends MongoRepository<NotificationTemplate, String> {

    Optional<NotificationTemplate> findByName(String name);

    boolean existsByNameAndType(String name, NotificationTypeEnum type);

    Page<NotificationTemplate> findAll(Pageable pageable);

    Page<NotificationTemplate> findByStatus(StatusEnum status, Pageable pageable);

    @Query("""
    {
      $or: [
        { "name": { $regex: ?0, $options: "i" } },
      ]
    }
    """)
    Page<NotificationTemplate> SearchByText(String search, Pageable pageable);

    @Query("""
    {
      $and: [
        {
          $or: [
            { "name": { $regex: ?0, $options: "i" } },
          ]
        },
        { "status": ?1 }
      ]
    }
    """)
    Page<NotificationTemplate> searchByStatusAndSearch(String search, StatusEnum status, Pageable pageable);
}
