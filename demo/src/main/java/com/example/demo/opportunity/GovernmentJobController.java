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
        List<GovernmentJob> jobs = jobRepository.findByActiveTrue();
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<GovernmentJob>> getAllJobsForAdmin() {
        return ResponseEntity.ok(jobRepository.findAll());
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<GovernmentJob> getJobByIdForAdmin(@PathVariable Long id) {
        return jobRepository.findById(id)
                 .map(ResponseEntity::ok)
                 .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<GovernmentJob> getJobById(@PathVariable Long id) {
        Optional<GovernmentJob> job = jobRepository.findById(id)
                .filter(GovernmentJob::isActive);
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
        normalizeJob(job);
        if (isInvalidJob(job)) {
            return ResponseEntity.badRequest().build();
        }
        job.setActive(true);
        GovernmentJob savedJob = jobRepository.save(job);
        return ResponseEntity.ok(savedJob);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<GovernmentJob> updateJob(@PathVariable Long id, @RequestBody GovernmentJob job) {
        Optional<GovernmentJob> existingJob = jobRepository.findById(id);
        if (existingJob.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        normalizeJob(job);
        if (isInvalidJob(job)) {
            return ResponseEntity.badRequest().build();
        }
        GovernmentJob existing = existingJob.get();
        job.setId(id);
        job.setActive(existing.isActive());
        job.setCreatedAt(existing.getCreatedAt());
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

    @PutMapping("/{id}/activate")
    public ResponseEntity<GovernmentJob> activateJob(@PathVariable Long id) {
        Optional<GovernmentJob> jobOpt = jobRepository.findById(id);
        if (jobOpt.isPresent()) {
            GovernmentJob job = jobOpt.get();
            job.setActive(true);
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

    private void normalizeJob(GovernmentJob job) {
        if (job == null) {
            return;
        }
        job.setName(trimToNull(job.getName()));
        job.setDescription(trimToNull(job.getDescription()));
        job.setType(trimToNull(job.getType()));
        job.setSalary(trimToNull(job.getSalary()));
        job.setVacancies(trimToNull(job.getVacancies()));
        job.setQualificationCriteria(trimToNull(job.getQualificationCriteria()));
        job.setFieldCriteria(trimToNull(job.getFieldCriteria()));
        job.setGenderCriteria(trimToNull(job.getGenderCriteria()));
        job.setAgeRelaxationCriteria(trimToNull(job.getAgeRelaxationCriteria()));
    }

    private boolean isInvalidJob(GovernmentJob job) {
        return job == null
                || job.getName() == null
                || job.getType() == null
                || job.getSalary() == null
                || job.getVacancies() == null
                || hasInvalidAgeRange(job.getMinAge(), job.getMaxAge());
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


