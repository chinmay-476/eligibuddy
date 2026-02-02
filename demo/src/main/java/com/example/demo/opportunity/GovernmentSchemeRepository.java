package com.example.demo.opportunity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GovernmentSchemeRepository extends JpaRepository<GovernmentScheme, Long> {
    
    // Find all active schemes
    List<GovernmentScheme> findByActiveTrue();
    
    // Find schemes by type
    List<GovernmentScheme> findByTypeAndActiveTrue(String type);
    
    // Find schemes by name (case-insensitive)
    List<GovernmentScheme> findByNameContainingIgnoreCaseAndActiveTrue(String name);
}


