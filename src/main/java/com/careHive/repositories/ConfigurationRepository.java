package com.careHive.repositories;

import com.careHive.entities.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfigurationRepository extends MongoRepository<Configuration,String> {
    Optional<Configuration> findByContext(String context);
    boolean existsByContext(String context);
    Page<Configuration> findAll(Pageable pageable);
}
