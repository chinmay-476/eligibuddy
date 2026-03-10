package com.example.demo.opportunity;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.Objects;

@RestController
@RequestMapping("/api/scholarships")
public class ScholarshipController {

    private final ScholarshipService scholarshipService;
    private final OpportunityCriteriaNormalizer criteriaNormalizer;

    public ScholarshipController(
            ScholarshipService scholarshipService,
            OpportunityCriteriaNormalizer criteriaNormalizer
    ) {
        this.scholarshipService = scholarshipService;
        this.criteriaNormalizer = criteriaNormalizer;
    }
    
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
    public ResponseEntity<Scholarship> getScholarshipByIdForAdmin(@PathVariable @NonNull Long id) {
        return scholarshipService.getScholarshipById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Scholarship> getScholarshipById(@PathVariable @NonNull Long id) {
        return scholarshipService.getScholarshipById(id)
                .filter(Scholarship::isActive)
                .map(ResponseEntity::ok)
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
    public ResponseEntity<Scholarship> createScholarship(@RequestBody @Nullable Scholarship scholarship) {
        normalizeScholarship(scholarship);
        if (isInvalidScholarship(scholarship)) {
            return ResponseEntity.badRequest().build();
        }
        Scholarship validScholarship = Objects.requireNonNull(scholarship);
        validScholarship.setActive(true);
        Scholarship savedScholarship = scholarshipService.saveScholarship(validScholarship);
        return ResponseEntity.ok(savedScholarship);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Scholarship> updateScholarship(@PathVariable @NonNull Long id, @RequestBody @Nullable Scholarship scholarship) {
        Optional<Scholarship> existingScholarship = scholarshipService.getScholarshipById(id);
        if (existingScholarship.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        normalizeScholarship(scholarship);
        if (isInvalidScholarship(scholarship)) {
            return ResponseEntity.badRequest().build();
        }

        Scholarship existing = existingScholarship.orElseThrow();
        Scholarship validScholarship = Objects.requireNonNull(scholarship);
        validScholarship.setId(id);
        validScholarship.setActive(existing.isActive());
        validScholarship.setCreatedAt(existing.getCreatedAt());
        Scholarship updatedScholarship = scholarshipService.saveScholarship(validScholarship);
        return ResponseEntity.ok(updatedScholarship);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScholarship(@PathVariable @NonNull Long id) {
        scholarshipService.deleteScholarship(id);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateScholarship(@PathVariable @NonNull Long id) {
        if (scholarshipService.getScholarshipById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        scholarshipService.deactivateScholarship(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activateScholarship(@PathVariable @NonNull Long id) {
        if (scholarshipService.getScholarshipById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        scholarshipService.activateScholarship(id);
        return ResponseEntity.ok().build();
    }

    private void normalizeScholarship(@Nullable Scholarship scholarship) {
        if (scholarship == null) {
            return;
        }
        scholarship.setName(trimToNull(scholarship.getName()));
        scholarship.setDescription(trimToNull(scholarship.getDescription()));
        scholarship.setType(trimToNull(scholarship.getType()));
        scholarship.setAmount(trimToNull(scholarship.getAmount()));
        scholarship.setQualificationCriteria(criteriaNormalizer.normalizeListCriteria(scholarship.getQualificationCriteria()));
        scholarship.setIncomeCriteria(criteriaNormalizer.normalizeListCriteria(scholarship.getIncomeCriteria()));
        scholarship.setCategoryCriteria(criteriaNormalizer.normalizeListCriteria(scholarship.getCategoryCriteria()));
        scholarship.setFieldCriteria(criteriaNormalizer.normalizeListCriteria(scholarship.getFieldCriteria()));
        scholarship.setGenderCriteria(criteriaNormalizer.normalizeListCriteria(scholarship.getGenderCriteria()));
        scholarship.setStateCriteria(criteriaNormalizer.normalizeListCriteria(scholarship.getStateCriteria()));
    }

    private boolean isInvalidScholarship(@Nullable Scholarship scholarship) {
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

    @Nullable
    private String trimToNull(@Nullable String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}


