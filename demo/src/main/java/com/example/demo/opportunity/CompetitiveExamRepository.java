package com.example.demo.opportunity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CompetitiveExamRepository extends JpaRepository<CompetitiveExam, Long> {
    
    // Find all active exams
    List<CompetitiveExam> findByActiveTrue();
    
    // Find exams by type
    List<CompetitiveExam> findByTypeAndActiveTrue(String type);
    
    // Find exams by name (case-insensitive)
    List<CompetitiveExam> findByNameContainingIgnoreCaseAndActiveTrue(String name);
}


