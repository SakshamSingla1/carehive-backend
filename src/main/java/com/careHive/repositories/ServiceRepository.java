package com.careHive.repositories;

import com.careHive.entities.Services;
import com.careHive.enums.StatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends MongoRepository<Services, String> {
    Services findByName(String name);

    long countByStatus(boolean status);

    Page<Services> findAll(Pageable pageable);

    Page<Services> findByStatus(StatusEnum status, Pageable pageable);

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
    Page<Services> searchByTextAndStatus(String search, StatusEnum status, Pageable pageable);

    @Query("""
        {
          $or: [
            { "name": { $regex: ?0, $options: "i" } },
          ]
        }
    """)
    Page<Services> searchServices(String search, Pageable pageable);

    List<Services> findByIdIn(List<String> ids);
}
