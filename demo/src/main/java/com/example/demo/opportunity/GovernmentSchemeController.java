package com.example.demo.opportunity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/schemes")
public class GovernmentSchemeController {
    
    @Autowired
    private GovernmentSchemeRepository schemeRepository;
    
    @GetMapping
    public ResponseEntity<List<GovernmentScheme>> getAllSchemes() {
        List<GovernmentScheme> schemes = schemeRepository.findByActiveTrue();
        return ResponseEntity.ok(schemes);
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<GovernmentScheme>> getAllSchemesForAdmin() {
        return ResponseEntity.ok(schemeRepository.findAll());
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<GovernmentScheme> getSchemeByIdForAdmin(@PathVariable Long id) {
        return schemeRepository.findById(id)
                   .map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<GovernmentScheme> getSchemeById(@PathVariable Long id) {
        Optional<GovernmentScheme> scheme = schemeRepository.findById(id)
                .filter(GovernmentScheme::isActive);
        return scheme.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<GovernmentScheme>> getActiveSchemes() {
        List<GovernmentScheme> schemes = schemeRepository.findByActiveTrue();
        return ResponseEntity.ok(schemes);
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<List<GovernmentScheme>> getSchemesByType(@PathVariable String type) {
        List<GovernmentScheme> schemes = schemeRepository.findByTypeAndActiveTrue(type);
        return ResponseEntity.ok(schemes);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<GovernmentScheme>> searchSchemes(@RequestParam String name) {
        List<GovernmentScheme> schemes = schemeRepository.findByNameContainingIgnoreCaseAndActiveTrue(name);
        return ResponseEntity.ok(schemes);
    }
    
    @PostMapping
    public ResponseEntity<GovernmentScheme> createScheme(@RequestBody GovernmentScheme scheme) {
        normalizeScheme(scheme);
        if (isInvalidScheme(scheme)) {
            return ResponseEntity.badRequest().build();
        }
        scheme.setActive(true);
        GovernmentScheme savedScheme = schemeRepository.save(scheme);
        return ResponseEntity.ok(savedScheme);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<GovernmentScheme> updateScheme(@PathVariable Long id, @RequestBody GovernmentScheme scheme) {
        Optional<GovernmentScheme> existingScheme = schemeRepository.findById(id);
        if (existingScheme.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        normalizeScheme(scheme);
        if (isInvalidScheme(scheme)) {
            return ResponseEntity.badRequest().build();
        }
        GovernmentScheme existing = existingScheme.get();
        scheme.setId(id);
        scheme.setActive(existing.isActive());
        scheme.setCreatedAt(existing.getCreatedAt());
        GovernmentScheme updatedScheme = schemeRepository.save(scheme);
        return ResponseEntity.ok(updatedScheme);
    }
    
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<GovernmentScheme> deactivateScheme(@PathVariable Long id) {
        Optional<GovernmentScheme> schemeOpt = schemeRepository.findById(id);
        if (schemeOpt.isPresent()) {
            GovernmentScheme scheme = schemeOpt.get();
            scheme.setActive(false);
            GovernmentScheme updatedScheme = schemeRepository.save(scheme);
            return ResponseEntity.ok(updatedScheme);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<GovernmentScheme> activateScheme(@PathVariable Long id) {
        Optional<GovernmentScheme> schemeOpt = schemeRepository.findById(id);
        if (schemeOpt.isPresent()) {
            GovernmentScheme scheme = schemeOpt.get();
            scheme.setActive(true);
            GovernmentScheme updatedScheme = schemeRepository.save(scheme);
            return ResponseEntity.ok(updatedScheme);
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScheme(@PathVariable Long id) {
        if (!schemeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        schemeRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    private void normalizeScheme(GovernmentScheme scheme) {
        if (scheme == null) {
            return;
        }
        scheme.setName(trimToNull(scheme.getName()));
        scheme.setDescription(trimToNull(scheme.getDescription()));
        scheme.setType(trimToNull(scheme.getType()));
        scheme.setBenefit(trimToNull(scheme.getBenefit()));
        scheme.setDeadline(trimToNull(scheme.getDeadline()));
        scheme.setQualificationCriteria(trimToNull(scheme.getQualificationCriteria()));
        scheme.setIncomeCriteria(trimToNull(scheme.getIncomeCriteria()));
        scheme.setCategoryCriteria(trimToNull(scheme.getCategoryCriteria()));
        scheme.setFieldCriteria(trimToNull(scheme.getFieldCriteria()));
        scheme.setGenderCriteria(trimToNull(scheme.getGenderCriteria()));
        scheme.setStateCriteria(trimToNull(scheme.getStateCriteria()));
        scheme.setDisabilityCriteria(trimToNull(scheme.getDisabilityCriteria()));
    }

    private boolean isInvalidScheme(GovernmentScheme scheme) {
        return scheme == null
                || scheme.getName() == null
                || scheme.getType() == null
                || scheme.getBenefit() == null
                || scheme.getDeadline() == null
                || hasInvalidAgeRange(scheme.getMinAge(), scheme.getMaxAge());
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


