package com.example.demo.opportunity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/scholarships")
public class ScholarshipController {
    
    @Autowired
    private ScholarshipService scholarshipService;
    
    @GetMapping
    public ResponseEntity<List<Scholarship>> getAllScholarships() {
        List<Scholarship> scholarships = scholarshipService.getAllScholarships();
        return ResponseEntity.ok(scholarships);
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<Scholarship>> getAllScholarshipsForAdmin() {
        return ResponseEntity.ok(scholarshipService.getAllScholarshipsForAdmin());
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<Scholarship> getScholarshipByIdForAdmin(@PathVariable Long id) {
        return scholarshipService.getScholarshipById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Scholarship> getScholarshipById(@PathVariable Long id) {
        Optional<Scholarship> scholarship = scholarshipService.getScholarshipById(id)
                .filter(Scholarship::isActive);
        return scholarship.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Scholarship>> getScholarshipsByType(@PathVariable String type) {
        List<Scholarship> scholarships = scholarshipService.getScholarshipsByType(type);
        return ResponseEntity.ok(scholarships);
    }
    
    @GetMapping("/expiring")
    public ResponseEntity<List<Scholarship>> getExpiringScholarships() {
        List<Scholarship> scholarships = scholarshipService.getExpiringScholarships();
        return ResponseEntity.ok(scholarships);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Scholarship>> searchScholarships(@RequestParam String name) {
        List<Scholarship> scholarships = scholarshipService.searchScholarshipsByName(name);
        return ResponseEntity.ok(scholarships);
    }
    
    @GetMapping("/available")
    public ResponseEntity<List<Scholarship>> getAvailableScholarships() {
        List<Scholarship> scholarships = scholarshipService.getAvailableScholarships();
        return ResponseEntity.ok(scholarships);
    }
    
    @PostMapping
    public ResponseEntity<Scholarship> createScholarship(@RequestBody Scholarship scholarship) {
        normalizeScholarship(scholarship);
        if (isInvalidScholarship(scholarship)) {
            return ResponseEntity.badRequest().build();
        }
        scholarship.setActive(true);
        Scholarship savedScholarship = scholarshipService.saveScholarship(scholarship);
        return ResponseEntity.ok(savedScholarship);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Scholarship> updateScholarship(@PathVariable Long id, @RequestBody Scholarship scholarship) {
        Optional<Scholarship> existingScholarship = scholarshipService.getScholarshipById(id);
        if (existingScholarship.isPresent()) {
            normalizeScholarship(scholarship);
            if (isInvalidScholarship(scholarship)) {
                return ResponseEntity.badRequest().build();
            }
            Scholarship existing = existingScholarship.get();
            scholarship.setId(id);
            scholarship.setActive(existing.isActive());
            scholarship.setCreatedAt(existing.getCreatedAt());
            Scholarship updatedScholarship = scholarshipService.saveScholarship(scholarship);
            return ResponseEntity.ok(updatedScholarship);
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScholarship(@PathVariable Long id) {
        scholarshipService.deleteScholarship(id);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateScholarship(@PathVariable Long id) {
        if (scholarshipService.getScholarshipById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        scholarshipService.deactivateScholarship(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activateScholarship(@PathVariable Long id) {
        if (scholarshipService.getScholarshipById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        scholarshipService.activateScholarship(id);
        return ResponseEntity.ok().build();
    }

    private void normalizeScholarship(Scholarship scholarship) {
        if (scholarship == null) {
            return;
        }
        scholarship.setName(trimToNull(scholarship.getName()));
        scholarship.setDescription(trimToNull(scholarship.getDescription()));
        scholarship.setType(trimToNull(scholarship.getType()));
        scholarship.setAmount(trimToNull(scholarship.getAmount()));
        scholarship.setQualificationCriteria(trimToNull(scholarship.getQualificationCriteria()));
        scholarship.setIncomeCriteria(trimToNull(scholarship.getIncomeCriteria()));
        scholarship.setCategoryCriteria(trimToNull(scholarship.getCategoryCriteria()));
        scholarship.setFieldCriteria(trimToNull(scholarship.getFieldCriteria()));
        scholarship.setGenderCriteria(trimToNull(scholarship.getGenderCriteria()));
        scholarship.setStateCriteria(trimToNull(scholarship.getStateCriteria()));
    }

    private boolean isInvalidScholarship(Scholarship scholarship) {
        return scholarship == null
                || scholarship.getName() == null
                || scholarship.getType() == null
                || scholarship.getAmount() == null
                || scholarship.getDeadline() == null
                || hasInvalidAgeRange(scholarship.getMinAge(), scholarship.getMaxAge());
    }

    private boolean hasInvalidAgeRange(Integer minAge, Integer maxAge) {
        return minAge != null && maxAge != null && minAge > maxAge;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}


