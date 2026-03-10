package com.example.demo.opportunity;

import com.example.demo.config.MongoSequenceService;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schemes")
public class GovernmentSchemeController {

    private static final String SCHEME_SEQUENCE = "government_schemes_sequence";

    private final GovernmentSchemeRepository schemeRepository;
    private final MongoSequenceService mongoSequenceService;
    private final OpportunityCriteriaNormalizer criteriaNormalizer;

    public GovernmentSchemeController(
            GovernmentSchemeRepository schemeRepository,
            MongoSequenceService mongoSequenceService,
            OpportunityCriteriaNormalizer criteriaNormalizer
    ) {
        this.schemeRepository = schemeRepository;
        this.mongoSequenceService = mongoSequenceService;
        this.criteriaNormalizer = criteriaNormalizer;
    }
    
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
    public ResponseEntity<GovernmentScheme> getSchemeByIdForAdmin(@PathVariable @NonNull Long id) {
        return schemeRepository.findById(id)
                   .map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<GovernmentScheme> getSchemeById(@PathVariable @NonNull Long id) {
        return schemeRepository.findById(id)
                .filter(GovernmentScheme::isActive)
                .map(ResponseEntity::ok)
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
    public ResponseEntity<GovernmentScheme> createScheme(@RequestBody @Nullable GovernmentScheme scheme) {
        normalizeScheme(scheme);
        if (isInvalidScheme(scheme)) {
            return ResponseEntity.badRequest().build();
        }
        GovernmentScheme validScheme = Objects.requireNonNull(scheme);
        validScheme.setId(mongoSequenceService.generateSequence(SCHEME_SEQUENCE));
        validScheme.setActive(true);
        if (validScheme.getCreatedAt() == null) {
            validScheme.setCreatedAt(LocalDate.now());
        }
        GovernmentScheme savedScheme = schemeRepository.save(validScheme);
        return ResponseEntity.ok(savedScheme);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<GovernmentScheme> updateScheme(@PathVariable @NonNull Long id, @RequestBody @Nullable GovernmentScheme scheme) {
        Optional<GovernmentScheme> existingScheme = schemeRepository.findById(id);
        if (existingScheme.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        normalizeScheme(scheme);
        if (isInvalidScheme(scheme)) {
            return ResponseEntity.badRequest().build();
        }

        GovernmentScheme existing = existingScheme.orElseThrow();
        GovernmentScheme validScheme = Objects.requireNonNull(scheme);
        validScheme.setId(id);
        validScheme.setActive(existing.isActive());
        validScheme.setCreatedAt(existing.getCreatedAt());
        GovernmentScheme updatedScheme = schemeRepository.save(validScheme);
        return ResponseEntity.ok(updatedScheme);
    }
    
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<GovernmentScheme> deactivateScheme(@PathVariable @NonNull Long id) {
        return schemeRepository.findById(id)
                .map(scheme -> {
                    scheme.setActive(false);
                    GovernmentScheme updatedScheme = schemeRepository.save(scheme);
                    return ResponseEntity.ok(updatedScheme);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<GovernmentScheme> activateScheme(@PathVariable @NonNull Long id) {
        return schemeRepository.findById(id)
                .map(scheme -> {
                    scheme.setActive(true);
                    GovernmentScheme updatedScheme = schemeRepository.save(scheme);
                    return ResponseEntity.ok(updatedScheme);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScheme(@PathVariable @NonNull Long id) {
        if (!schemeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        schemeRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    private void normalizeScheme(@Nullable GovernmentScheme scheme) {
        if (scheme == null) {
            return;
        }
        scheme.setName(trimToNull(scheme.getName()));
        scheme.setDescription(trimToNull(scheme.getDescription()));
        scheme.setType(trimToNull(scheme.getType()));
        scheme.setBenefit(trimToNull(scheme.getBenefit()));
        scheme.setDeadline(trimToNull(scheme.getDeadline()));
        scheme.setQualificationCriteria(criteriaNormalizer.normalizeListCriteria(scheme.getQualificationCriteria()));
        scheme.setIncomeCriteria(criteriaNormalizer.normalizeListCriteria(scheme.getIncomeCriteria()));
        scheme.setCategoryCriteria(criteriaNormalizer.normalizeListCriteria(scheme.getCategoryCriteria()));
        scheme.setFieldCriteria(criteriaNormalizer.normalizeListCriteria(scheme.getFieldCriteria()));
        scheme.setGenderCriteria(criteriaNormalizer.normalizeListCriteria(scheme.getGenderCriteria()));
        scheme.setStateCriteria(criteriaNormalizer.normalizeListCriteria(scheme.getStateCriteria()));
        scheme.setDisabilityCriteria(criteriaNormalizer.normalizeListCriteria(scheme.getDisabilityCriteria()));
    }

    private boolean isInvalidScheme(@Nullable GovernmentScheme scheme) {
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

    @Nullable
    private String trimToNull(@Nullable String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}


