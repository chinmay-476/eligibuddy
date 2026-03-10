package com.example.demo.contact;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends MongoRepository<Contact, Long> {
    List<Contact> findAllByOrderByIdDesc();
    List<Contact> findBySubmittedByOrderByIdDesc(String submittedBy);
} 

