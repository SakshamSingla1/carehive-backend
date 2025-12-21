package com.careHive.repositories;

import com.careHive.entities.CaretakerServices;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CaretakerServicesRepository extends MongoRepository<CaretakerServices, String> {
    Optional<CaretakerServices> findByCaretakerIdAndServiceId(String caretakerId, String serviceId);
}
