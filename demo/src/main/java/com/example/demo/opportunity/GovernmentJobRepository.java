package com.example.demo.opportunity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GovernmentJobRepository extends JpaRepository<GovernmentJob, Long> {
    
    // Find all active jobs
    List<GovernmentJob> findByActiveTrue();
    
    // Find jobs by type
    List<GovernmentJob> findByTypeAndActiveTrue(String type);
    
    // Find jobs by name (case-insensitive)
    List<GovernmentJob> findByNameContainingIgnoreCaseAndActiveTrue(String name);
}


