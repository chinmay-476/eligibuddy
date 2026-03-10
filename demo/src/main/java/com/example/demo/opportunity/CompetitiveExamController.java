package com.example.demo.opportunity;

import com.example.demo.config.MongoSequenceService;
import java.time.LocalDate;
import java.util.Objects;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/exams")
public class CompetitiveExamController {

    private static final String EXAM_SEQUENCE = "competitive_exams_sequence";

    private final CompetitiveExamRepository examRepository;
    private final MongoSequenceService mongoSequenceService;
    private final OpportunityCriteriaNormalizer criteriaNormalizer;

    public CompetitiveExamController(
            CompetitiveExamRepository examRepository,
            MongoSequenceService mongoSequenceService,
            OpportunityCriteriaNormalizer criteriaNormalizer
    ) {
        this.examRepository = examRepository;
        this.mongoSequenceService = mongoSequenceService;
        this.criteriaNormalizer = criteriaNormalizer;
    }
    
    @GetMapping
    public ResponseEntity<List<CompetitiveExam>> getAllExams() {
        List<CompetitiveExam> exams = examRepository.findByActiveTrue();
        return ResponseEntity.ok(exams);
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<CompetitiveExam>> getAllExamsForAdmin() {
        return ResponseEntity.ok(examRepository.findAll());
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<CompetitiveExam> getExamByIdForAdmin(@PathVariable @NonNull Long id) {
        return examRepository.findById(id)
                  .map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CompetitiveExam> getExamById(@PathVariable @NonNull Long id) {
        return examRepository.findById(id)
                .filter(CompetitiveExam::isActive)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<CompetitiveExam>> getActiveExams() {
        List<CompetitiveExam> exams = examRepository.findByActiveTrue();
        return ResponseEntity.ok(exams);
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<List<CompetitiveExam>> getExamsByType(@PathVariable String type) {
        List<CompetitiveExam> exams = examRepository.findByTypeAndActiveTrue(type);
        return ResponseEntity.ok(exams);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<CompetitiveExam>> searchExams(@RequestParam String name) {
        List<CompetitiveExam> exams = examRepository.findByNameContainingIgnoreCaseAndActiveTrue(name);
        return ResponseEntity.ok(exams);
    }
    
    @PostMapping
    public ResponseEntity<CompetitiveExam> createExam(@RequestBody @Nullable CompetitiveExam exam) {
        normalizeExam(exam);
        if (isInvalidExam(exam)) {
            return ResponseEntity.badRequest().build();
        }
        CompetitiveExam validExam = Objects.requireNonNull(exam);
        validExam.setId(mongoSequenceService.generateSequence(EXAM_SEQUENCE));
        validExam.setActive(true);
        if (validExam.getCreatedAt() == null) {
            validExam.setCreatedAt(LocalDate.now());
        }
        CompetitiveExam savedExam = examRepository.save(validExam);
        return ResponseEntity.ok(savedExam);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CompetitiveExam> updateExam(@PathVariable @NonNull Long id, @RequestBody @Nullable CompetitiveExam exam) {
        Optional<CompetitiveExam> existingExam = examRepository.findById(id);
        if (existingExam.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        normalizeExam(exam);
        if (isInvalidExam(exam)) {
            return ResponseEntity.badRequest().build();
        }

        CompetitiveExam existing = existingExam.orElseThrow();
        CompetitiveExam validExam = Objects.requireNonNull(exam);
        validExam.setId(id);
        validExam.setActive(existing.isActive());
        validExam.setCreatedAt(existing.getCreatedAt());
        CompetitiveExam updatedExam = examRepository.save(validExam);
        return ResponseEntity.ok(updatedExam);
    }
    
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<CompetitiveExam> deactivateExam(@PathVariable @NonNull Long id) {
        return examRepository.findById(id)
                .map(exam -> {
                    exam.setActive(false);
                    CompetitiveExam updatedExam = examRepository.save(exam);
                    return ResponseEntity.ok(updatedExam);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<CompetitiveExam> activateExam(@PathVariable @NonNull Long id) {
        return examRepository.findById(id)
                .map(exam -> {
                    exam.setActive(true);
                    CompetitiveExam updatedExam = examRepository.save(exam);
                    return ResponseEntity.ok(updatedExam);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExam(@PathVariable @NonNull Long id) {
        if (!examRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        examRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    private void normalizeExam(@Nullable CompetitiveExam exam) {
        if (exam == null) {
            return;
        }
        exam.setName(trimToNull(exam.getName()));
        exam.setDescription(trimToNull(exam.getDescription()));
        exam.setType(trimToNull(exam.getType()));
        exam.setExamDate(trimToNull(exam.getExamDate()));
        exam.setApplicationFee(trimToNull(exam.getApplicationFee()));
        exam.setQualificationCriteria(criteriaNormalizer.normalizeListCriteria(exam.getQualificationCriteria()));
        exam.setFieldCriteria(criteriaNormalizer.normalizeListCriteria(exam.getFieldCriteria()));
        exam.setGenderCriteria(criteriaNormalizer.normalizeListCriteria(exam.getGenderCriteria()));
        exam.setAgeRelaxationCriteria(criteriaNormalizer.normalizeMapCriteria(exam.getAgeRelaxationCriteria()));
    }

    private boolean isInvalidExam(@Nullable CompetitiveExam exam) {
        return exam == null
                || exam.getName() == null
                || exam.getType() == null
                || exam.getExamDate() == null
                || exam.getApplicationFee() == null
                || hasInvalidAgeRange(exam.getMinAge(), exam.getMaxAge());
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


