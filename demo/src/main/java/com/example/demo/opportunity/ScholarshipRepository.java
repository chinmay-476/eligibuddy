package com.example.demo.opportunity;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScholarshipRepository extends MongoRepository<Scholarship, Long> {

    List<Scholarship> findByActiveTrue();

    List<Scholarship> findByTypeAndActiveTrue(String type);

    List<Scholarship> findByDeadlineAfterAndActiveTrue(LocalDate date);

    List<Scholarship> findByDeadlineBetweenAndActiveTrue(LocalDate startDate, LocalDate endDate);

    List<Scholarship> findByNameContainingIgnoreCaseAndActiveTrue(String name);
}
