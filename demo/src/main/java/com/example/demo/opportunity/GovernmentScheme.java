package com.example.demo.opportunity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "government_schemes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GovernmentScheme {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private String type; // Housing, Healthcare, Social Security, Business, Skill Development, Agriculture, Education, Insurance
    
    @Column(nullable = false)
    private String benefit;
    
    @Column(nullable = false)
    private String deadline; // "Ongoing" or specific date
    
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
    
    @Column(columnDefinition = "TEXT")
    private String disabilityCriteria; // JSON array for disability
    
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


