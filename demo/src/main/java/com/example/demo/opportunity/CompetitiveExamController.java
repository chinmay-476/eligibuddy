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
        List<CompetitiveExam> exams = examRepository.findAll();
        return ResponseEntity.ok(exams);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CompetitiveExam> getExamById(@PathVariable Long id) {
        Optional<CompetitiveExam> exam = examRepository.findById(id);
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
        exam.setActive(true);
        CompetitiveExam savedExam = examRepository.save(exam);
        return ResponseEntity.ok(savedExam);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CompetitiveExam> updateExam(@PathVariable Long id, @RequestBody CompetitiveExam exam) {
        if (!examRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        exam.setId(id);
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
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExam(@PathVariable Long id) {
        if (!examRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        examRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}


