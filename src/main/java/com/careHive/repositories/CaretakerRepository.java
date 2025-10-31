package com.careHive.repositories;

import com.careHive.entities.Caretaker;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CaretakerRepository extends MongoRepository<Caretaker, String> {
}
