package com.careHive.repositories;

import com.careHive.entities.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {
    List<Payment> findByElderId(String elderId);
    List<Payment> findByCaretakerId(String caretakerId);
}
