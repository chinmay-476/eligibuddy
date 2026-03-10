package com.example.demo.opportunity;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompetitiveExamRepository extends MongoRepository<CompetitiveExam, Long> {
    
    // Find all active exams
    List<CompetitiveExam> findByActiveTrue();
    
    // Find exams by type
    List<CompetitiveExam> findByTypeAndActiveTrue(String type);
    
    // Find exams by name (case-insensitive)
    List<CompetitiveExam> findByNameContainingIgnoreCaseAndActiveTrue(String name);
}


