package com.example.demo.opportunity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ScholarshipService {
    
    @Autowired
    private ScholarshipRepository scholarshipRepository;
    
    public List<Scholarship> getAllScholarships() {
        return scholarshipRepository.findByActiveTrue();
    }
    
    public List<Scholarship> getScholarshipsByType(String type) {
        return scholarshipRepository.findByTypeAndActiveTrue(type);
    }
    
    public List<Scholarship> getExpiringScholarships() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(30);
        return scholarshipRepository.findExpiringSoon(startDate, endDate);
    }
    
    public List<Scholarship> searchScholarshipsByName(String name) {
        return scholarshipRepository.findByNameContainingIgnoreCaseAndActiveTrue(name);
    }
    
    public Optional<Scholarship> getScholarshipById(Long id) {
        return scholarshipRepository.findById(id);
    }
    
    public Scholarship saveScholarship(Scholarship scholarship) {
        return scholarshipRepository.save(scholarship);
    }
    
    public void deleteScholarship(Long id) {
        scholarshipRepository.deleteById(id);
    }
    
    public void deactivateScholarship(Long id) {
        Optional<Scholarship> scholarship = scholarshipRepository.findById(id);
        if (scholarship.isPresent()) {
            Scholarship s = scholarship.get();
            s.setActive(false);
            scholarshipRepository.save(s);
        }
    }
    
    public List<Scholarship> getScholarshipsByCriteria(String type, Integer minAmount) {
        return scholarshipRepository.findByCriteria(type, minAmount);
    }
    
    public List<Scholarship> getAvailableScholarships() {
        return scholarshipRepository.findByDeadlineAfterAndActiveTrue(LocalDate.now());
    }
}


