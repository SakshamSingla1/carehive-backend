package com.careHive.repositories;

import com.careHive.entities.Rating;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends MongoRepository<Rating, String> {
    List<Rating> findByCaretakerId(String caretakerId);
    List<Rating> findByUserId(String userId);
}
