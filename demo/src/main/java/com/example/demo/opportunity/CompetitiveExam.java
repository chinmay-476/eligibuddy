package com.example.demo.opportunity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "competitive_exams")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompetitiveExam {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private String type; // Civil Services, Staff Selection, Banking, Railway, Engineering, Medical, Defense, Teaching, Law, Management
    
    @Column(nullable = false)
    private String examDate;
    
    @Column(nullable = false)
    private String applicationFee;
    
    // Eligibility criteria stored as JSON strings
    @Column(columnDefinition = "TEXT")
    private String qualificationCriteria; // JSON array of qualifications
    
    @Column(columnDefinition = "TEXT")
    private String fieldCriteria; // JSON array of fields
    
    @Column(columnDefinition = "TEXT")
    private String genderCriteria; // JSON array of genders
    
    @Column(columnDefinition = "TEXT")
    private String ageRelaxationCriteria; // JSON object for age relaxation
    
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


