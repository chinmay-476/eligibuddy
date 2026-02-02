package com.example.demo.opportunity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/jobs")
public class GovernmentJobController {
    
    @Autowired
    private GovernmentJobRepository jobRepository;
    
    @GetMapping
    public ResponseEntity<List<GovernmentJob>> getAllJobs() {
        List<GovernmentJob> jobs = jobRepository.findAll();
        return ResponseEntity.ok(jobs);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<GovernmentJob> getJobById(@PathVariable Long id) {
        Optional<GovernmentJob> job = jobRepository.findById(id);
        return job.map(ResponseEntity::ok)
                 .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<GovernmentJob>> getActiveJobs() {
        List<GovernmentJob> jobs = jobRepository.findByActiveTrue();
        return ResponseEntity.ok(jobs);
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<List<GovernmentJob>> getJobsByType(@PathVariable String type) {
        List<GovernmentJob> jobs = jobRepository.findByTypeAndActiveTrue(type);
        return ResponseEntity.ok(jobs);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<GovernmentJob>> searchJobs(@RequestParam String name) {
        List<GovernmentJob> jobs = jobRepository.findByNameContainingIgnoreCaseAndActiveTrue(name);
        return ResponseEntity.ok(jobs);
    }
    
    @PostMapping
    public ResponseEntity<GovernmentJob> createJob(@RequestBody GovernmentJob job) {
        job.setActive(true);
        GovernmentJob savedJob = jobRepository.save(job);
        return ResponseEntity.ok(savedJob);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<GovernmentJob> updateJob(@PathVariable Long id, @RequestBody GovernmentJob job) {
        if (!jobRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        job.setId(id);
        GovernmentJob updatedJob = jobRepository.save(job);
        return ResponseEntity.ok(updatedJob);
    }
    
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<GovernmentJob> deactivateJob(@PathVariable Long id) {
        Optional<GovernmentJob> jobOpt = jobRepository.findById(id);
        if (jobOpt.isPresent()) {
            GovernmentJob job = jobOpt.get();
            job.setActive(false);
            GovernmentJob updatedJob = jobRepository.save(job);
            return ResponseEntity.ok(updatedJob);
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        if (!jobRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        jobRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}


