package com.example.demo.opportunity;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GovernmentSchemeRepository extends MongoRepository<GovernmentScheme, Long> {
    
    // Find all active schemes
    List<GovernmentScheme> findByActiveTrue();
    
    // Find schemes by type
    List<GovernmentScheme> findByTypeAndActiveTrue(String type);
    
    // Find schemes by name (case-insensitive)
    List<GovernmentScheme> findByNameContainingIgnoreCaseAndActiveTrue(String name);
}


