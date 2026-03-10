package com.example.demo.opportunity;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GovernmentJobRepository extends MongoRepository<GovernmentJob, Long> {
    
    // Find all active jobs
    List<GovernmentJob> findByActiveTrue();
    
    // Find jobs by type
    List<GovernmentJob> findByTypeAndActiveTrue(String type);
    
    // Find jobs by name (case-insensitive)
    List<GovernmentJob> findByNameContainingIgnoreCaseAndActiveTrue(String name);
}


