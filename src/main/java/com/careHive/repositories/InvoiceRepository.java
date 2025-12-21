package com.careHive.repositories;

import com.careHive.entities.Invoice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceRepository extends MongoRepository<Invoice, String> {
    Optional<Invoice> findByPaymentId(String paymentId);
}
