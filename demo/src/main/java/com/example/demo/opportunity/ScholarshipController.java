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
    
    @GetMapping("/{id}")
    public ResponseEntity<Scholarship> getScholarshipById(@PathVariable Long id) {
        Optional<Scholarship> scholarship = scholarshipService.getScholarshipById(id);
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
        if (scholarship.getName() == null || scholarship.getName().trim().isEmpty()
                || scholarship.getType() == null || scholarship.getType().trim().isEmpty()
                || scholarship.getAmount() == null || scholarship.getAmount().trim().isEmpty()
                || scholarship.getDeadline() == null) {
            return ResponseEntity.badRequest().build();
        }
        Scholarship savedScholarship = scholarshipService.saveScholarship(scholarship);
        return ResponseEntity.ok(savedScholarship);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Scholarship> updateScholarship(@PathVariable Long id, @RequestBody Scholarship scholarship) {
        Optional<Scholarship> existingScholarship = scholarshipService.getScholarshipById(id);
        if (existingScholarship.isPresent()) {
            if (scholarship.getName() == null || scholarship.getName().trim().isEmpty()
                    || scholarship.getType() == null || scholarship.getType().trim().isEmpty()
                    || scholarship.getAmount() == null || scholarship.getAmount().trim().isEmpty()
                    || scholarship.getDeadline() == null) {
                return ResponseEntity.badRequest().build();
            }
            scholarship.setId(id);
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
}


