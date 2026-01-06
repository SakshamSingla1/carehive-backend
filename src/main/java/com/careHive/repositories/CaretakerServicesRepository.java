package com.careHive.repositories;

import com.careHive.entities.CaretakerServices;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CaretakerServicesRepository extends MongoRepository<CaretakerServices, String> {
    Optional<CaretakerServices> findByCaretakerIdAndServiceId(String caretakerId, String serviceId);
    Page<CaretakerServices> findByServiceId(String serviceId, Pageable pageable);
    List<CaretakerServices> findByCaretakerIdIn(List<String> caretakerIds);
}
