package com.example.demo.opportunity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScholarshipRepository extends JpaRepository<Scholarship, Long> {
    
    // Find all active scholarships
    List<Scholarship> findByActiveTrue();
    
    // Find scholarships by type
    List<Scholarship> findByTypeAndActiveTrue(String type);
    
    // Find scholarships with deadline after a specific date
    List<Scholarship> findByDeadlineAfterAndActiveTrue(LocalDate date);
    
    // Find scholarships by name (case-insensitive)
    List<Scholarship> findByNameContainingIgnoreCaseAndActiveTrue(String name);
    
    // Find scholarships expiring soon (within next 30 days)
    @Query("SELECT s FROM Scholarship s WHERE s.deadline BETWEEN :startDate AND :endDate AND s.active = true")
    List<Scholarship> findExpiringSoon(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Find scholarships by multiple criteria
    @Query("SELECT s FROM Scholarship s WHERE s.active = true AND " +
           "(:type IS NULL OR s.type = :type) AND " +
           "(:minAmount IS NULL OR CAST(REPLACE(REPLACE(s.amount, '₹', ''), ',', '') AS INTEGER) >= :minAmount)")
    List<Scholarship> findByCriteria(@Param("type") String type, @Param("minAmount") Integer minAmount);
}


