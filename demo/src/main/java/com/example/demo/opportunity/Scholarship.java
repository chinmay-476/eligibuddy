package com.example.demo.opportunity;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "scholarships")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Scholarship {

    @Id
    private Long id;

    private String name;

    private String description;

    private String type;

    private String amount;

    private LocalDate deadline;

    private String qualificationCriteria;

    private String incomeCriteria;

    private String categoryCriteria;

    private String fieldCriteria;

    private String genderCriteria;

    private String stateCriteria;

    private Integer minAge;

    private Integer maxAge;

    private boolean active = true;

    private LocalDate createdAt = LocalDate.now();
}


