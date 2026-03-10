package com.example.demo.opportunity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/exams")
public class CompetitiveExamController {
    
    @Autowired
    private CompetitiveExamRepository examRepository;
    
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
    public ResponseEntity<CompetitiveExam> getExamByIdForAdmin(@PathVariable Long id) {
        return examRepository.findById(id)
                  .map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CompetitiveExam> getExamById(@PathVariable Long id) {
        Optional<CompetitiveExam> exam = examRepository.findById(id)
                .filter(CompetitiveExam::isActive);
        return exam.map(ResponseEntity::ok)
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
    public ResponseEntity<CompetitiveExam> createExam(@RequestBody CompetitiveExam exam) {
        normalizeExam(exam);
        if (isInvalidExam(exam)) {
            return ResponseEntity.badRequest().build();
        }
        exam.setActive(true);
        CompetitiveExam savedExam = examRepository.save(exam);
        return ResponseEntity.ok(savedExam);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CompetitiveExam> updateExam(@PathVariable Long id, @RequestBody CompetitiveExam exam) {
        Optional<CompetitiveExam> existingExam = examRepository.findById(id);
        if (existingExam.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        normalizeExam(exam);
        if (isInvalidExam(exam)) {
            return ResponseEntity.badRequest().build();
        }
        CompetitiveExam existing = existingExam.get();
        exam.setId(id);
        exam.setActive(existing.isActive());
        exam.setCreatedAt(existing.getCreatedAt());
        CompetitiveExam updatedExam = examRepository.save(exam);
        return ResponseEntity.ok(updatedExam);
    }
    
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<CompetitiveExam> deactivateExam(@PathVariable Long id) {
        Optional<CompetitiveExam> examOpt = examRepository.findById(id);
        if (examOpt.isPresent()) {
            CompetitiveExam exam = examOpt.get();
            exam.setActive(false);
            CompetitiveExam updatedExam = examRepository.save(exam);
            return ResponseEntity.ok(updatedExam);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<CompetitiveExam> activateExam(@PathVariable Long id) {
        Optional<CompetitiveExam> examOpt = examRepository.findById(id);
        if (examOpt.isPresent()) {
            CompetitiveExam exam = examOpt.get();
            exam.setActive(true);
            CompetitiveExam updatedExam = examRepository.save(exam);
            return ResponseEntity.ok(updatedExam);
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExam(@PathVariable Long id) {
        if (!examRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        examRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    private void normalizeExam(CompetitiveExam exam) {
        if (exam == null) {
            return;
        }
        exam.setName(trimToNull(exam.getName()));
        exam.setDescription(trimToNull(exam.getDescription()));
        exam.setType(trimToNull(exam.getType()));
        exam.setExamDate(trimToNull(exam.getExamDate()));
        exam.setApplicationFee(trimToNull(exam.getApplicationFee()));
        exam.setQualificationCriteria(trimToNull(exam.getQualificationCriteria()));
        exam.setFieldCriteria(trimToNull(exam.getFieldCriteria()));
        exam.setGenderCriteria(trimToNull(exam.getGenderCriteria()));
        exam.setAgeRelaxationCriteria(trimToNull(exam.getAgeRelaxationCriteria()));
    }

    private boolean isInvalidExam(CompetitiveExam exam) {
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

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}


