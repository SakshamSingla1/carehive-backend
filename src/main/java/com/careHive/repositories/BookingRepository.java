package com.careHive.repositories;

import com.careHive.entities.Booking;
import com.careHive.enums.BookingStatusEnum;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends MongoRepository<Booking, String> {

    List<Booking> findByElderId(String elderId);

    List<Booking> findByCaretakerId(String caretakerId);

    // ✅ Custom method to check if a pending booking exists between elder and caretaker
    boolean existsByElderIdAndCaretakerIdAndStatus(String elderId, String caretakerId, BookingStatusEnum status);
}
