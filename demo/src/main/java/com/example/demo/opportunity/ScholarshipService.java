package com.example.demo.opportunity;

import com.example.demo.config.MongoSequenceService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ScholarshipService {

    private static final String SCHOLARSHIP_SEQUENCE = "scholarships_sequence";

    private final ScholarshipRepository scholarshipRepository;
    private final MongoSequenceService mongoSequenceService;

    public ScholarshipService(
            ScholarshipRepository scholarshipRepository,
            MongoSequenceService mongoSequenceService
    ) {
        this.scholarshipRepository = scholarshipRepository;
        this.mongoSequenceService = mongoSequenceService;
    }
    
    public List<Scholarship> getAllScholarships() {
        return scholarshipRepository.findByActiveTrue();
    }

    public List<Scholarship> getAllScholarshipsForAdmin() {
        return scholarshipRepository.findAll();
    }
    
    public List<Scholarship> getScholarshipsByType(String type) {
        return scholarshipRepository.findByTypeAndActiveTrue(type);
    }
    
    public List<Scholarship> getExpiringScholarships() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(30);
        return scholarshipRepository.findByDeadlineBetweenAndActiveTrue(startDate, endDate);
    }
    
    public List<Scholarship> searchScholarshipsByName(String name) {
        return scholarshipRepository.findByNameContainingIgnoreCaseAndActiveTrue(name);
    }
    
    public Optional<Scholarship> getScholarshipById(@NonNull Long id) {
        return scholarshipRepository.findById(id);
    }
    
    public Scholarship saveScholarship(@NonNull Scholarship scholarship) {
        if (scholarship.getId() == null) {
            scholarship.setId(mongoSequenceService.generateSequence(SCHOLARSHIP_SEQUENCE));
        }
        if (scholarship.getCreatedAt() == null) {
            scholarship.setCreatedAt(LocalDate.now());
        }
        return scholarshipRepository.save(scholarship);
    }
    
    public void deleteScholarship(@NonNull Long id) {
        scholarshipRepository.deleteById(id);
    }
    
    public void deactivateScholarship(@NonNull Long id) {
        scholarshipRepository.findById(id).ifPresent(s -> {
            s.setActive(false);
            scholarshipRepository.save(s);
        });
    }

    public void activateScholarship(@NonNull Long id) {
        scholarshipRepository.findById(id).ifPresent(s -> {
            s.setActive(true);
            scholarshipRepository.save(s);
        });
    }
    
    public List<Scholarship> getScholarshipsByCriteria(String type, Integer minAmount) {
        return scholarshipRepository.findByActiveTrue().stream()
                .filter(scholarship -> type == null || type.equalsIgnoreCase(scholarship.getType()))
                .filter(scholarship -> minAmount == null || extractAmountValue(scholarship.getAmount()) >= minAmount)
                .toList();
    }
    
    public List<Scholarship> getAvailableScholarships() {
        return scholarshipRepository.findByDeadlineAfterAndActiveTrue(LocalDate.now());
    }

    private int extractAmountValue(String amount) {
        if (amount == null) {
            return 0;
        }
        String digitsOnly = amount.replaceAll("[^\\d]", "");
        if (digitsOnly.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(digitsOnly);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }
}


