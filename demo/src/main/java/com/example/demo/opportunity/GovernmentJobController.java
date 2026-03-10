package com.example.demo.opportunity;

import com.example.demo.config.MongoSequenceService;
import java.time.LocalDate;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/jobs")
public class GovernmentJobController {

    private static final String JOB_SEQUENCE = "government_jobs_sequence";

    private final GovernmentJobRepository jobRepository;
    private final MongoSequenceService mongoSequenceService;
    private final OpportunityCriteriaNormalizer criteriaNormalizer;

    public GovernmentJobController(
            GovernmentJobRepository jobRepository,
            MongoSequenceService mongoSequenceService,
            OpportunityCriteriaNormalizer criteriaNormalizer
    ) {
        this.jobRepository = jobRepository;
        this.mongoSequenceService = mongoSequenceService;
        this.criteriaNormalizer = criteriaNormalizer;
    }
    
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
    public ResponseEntity<GovernmentJob> getJobByIdForAdmin(@PathVariable @NonNull Long id) {
        return jobRepository.findById(id)
                 .map(ResponseEntity::ok)
                 .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<GovernmentJob> getJobById(@PathVariable @NonNull Long id) {
        return jobRepository.findById(id)
                .filter(GovernmentJob::isActive)
                .map(ResponseEntity::ok)
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
    public ResponseEntity<GovernmentJob> createJob(@RequestBody @Nullable GovernmentJob job) {
        normalizeJob(job);
        if (isInvalidJob(job)) {
            return ResponseEntity.badRequest().build();
        }
        GovernmentJob validJob = Objects.requireNonNull(job);
        validJob.setId(mongoSequenceService.generateSequence(JOB_SEQUENCE));
        validJob.setActive(true);
        if (validJob.getCreatedAt() == null) {
            validJob.setCreatedAt(LocalDate.now());
        }
        GovernmentJob savedJob = jobRepository.save(validJob);
        return ResponseEntity.ok(savedJob);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<GovernmentJob> updateJob(@PathVariable @NonNull Long id, @RequestBody @Nullable GovernmentJob job) {
        Optional<GovernmentJob> existingJob = jobRepository.findById(id);
        if (existingJob.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        normalizeJob(job);
        if (isInvalidJob(job)) {
            return ResponseEntity.badRequest().build();
        }

        GovernmentJob existing = existingJob.orElseThrow();
        GovernmentJob validJob = Objects.requireNonNull(job);
        validJob.setId(id);
        validJob.setActive(existing.isActive());
        validJob.setCreatedAt(existing.getCreatedAt());
        GovernmentJob updatedJob = jobRepository.save(validJob);
        return ResponseEntity.ok(updatedJob);
    }
    
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<GovernmentJob> deactivateJob(@PathVariable @NonNull Long id) {
        return jobRepository.findById(id)
                .map(job -> {
                    job.setActive(false);
                    GovernmentJob updatedJob = jobRepository.save(job);
                    return ResponseEntity.ok(updatedJob);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<GovernmentJob> activateJob(@PathVariable @NonNull Long id) {
        return jobRepository.findById(id)
                .map(job -> {
                    job.setActive(true);
                    GovernmentJob updatedJob = jobRepository.save(job);
                    return ResponseEntity.ok(updatedJob);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable @NonNull Long id) {
        if (!jobRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        jobRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    private void normalizeJob(@Nullable GovernmentJob job) {
        if (job == null) {
            return;
        }
        job.setName(trimToNull(job.getName()));
        job.setDescription(trimToNull(job.getDescription()));
        job.setType(trimToNull(job.getType()));
        job.setSalary(trimToNull(job.getSalary()));
        job.setVacancies(trimToNull(job.getVacancies()));
        job.setApplicationDeadline(trimToNull(job.getApplicationDeadline()));
        job.setQualificationCriteria(criteriaNormalizer.normalizeListCriteria(job.getQualificationCriteria()));
        job.setFieldCriteria(criteriaNormalizer.normalizeListCriteria(job.getFieldCriteria()));
        job.setGenderCriteria(criteriaNormalizer.normalizeListCriteria(job.getGenderCriteria()));
        job.setAgeRelaxationCriteria(criteriaNormalizer.normalizeMapCriteria(job.getAgeRelaxationCriteria()));
    }

    private boolean isInvalidJob(@Nullable GovernmentJob job) {
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

    @Nullable
    private String trimToNull(@Nullable String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}


