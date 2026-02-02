package com.example.demo.opportunity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "scholarships")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Scholarship {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private String type; // Merit-based, Category-based, Gender-based, Field-specific, Social Support
    
    @Column(nullable = false)
    private String amount;
    
    @Column(nullable = false)
    private LocalDate deadline;
    
    // Eligibility criteria stored as JSON strings
    @Column(columnDefinition = "TEXT")
    private String qualificationCriteria; // JSON array of qualifications
    
    @Column(columnDefinition = "TEXT")
    private String incomeCriteria; // JSON array of income ranges
    
    @Column(columnDefinition = "TEXT")
    private String categoryCriteria; // JSON array of categories
    
    @Column(columnDefinition = "TEXT")
    private String fieldCriteria; // JSON array of fields
    
    @Column(columnDefinition = "TEXT")
    private String genderCriteria; // JSON array of genders
    
    @Column(columnDefinition = "TEXT")
    private String stateCriteria; // JSON array of states
    
    @Column
    private Integer minAge;
    
    @Column
    private Integer maxAge;
    
    @Column(nullable = false)
    private boolean active = true;
    
    @Column
    private LocalDate createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
    }
}


