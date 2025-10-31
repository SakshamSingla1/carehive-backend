package com.careHive.repositories;

import com.careHive.entities.Services;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends MongoRepository<Services, String> {
    Services findByName(String name);
}
