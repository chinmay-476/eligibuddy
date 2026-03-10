package com.example.demo.opportunity;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "government_jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GovernmentJob {

    @Id
    private Long id;

    private String name;

    private String description;

    private String type;

    private String salary;

    private String vacancies;

    private String applicationDeadline;

    private String qualificationCriteria;

    private String fieldCriteria;

    private String genderCriteria;

    private String ageRelaxationCriteria;

    private Integer minAge;

    private Integer maxAge;

    private boolean active = true;

    private LocalDate createdAt = LocalDate.now();
}


