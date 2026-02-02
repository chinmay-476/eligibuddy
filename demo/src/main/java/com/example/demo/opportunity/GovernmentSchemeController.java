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
        List<GovernmentScheme> schemes = schemeRepository.findAll();
        return ResponseEntity.ok(schemes);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<GovernmentScheme> getSchemeById(@PathVariable Long id) {
        Optional<GovernmentScheme> scheme = schemeRepository.findById(id);
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
        if (scheme.getName() == null || scheme.getName().trim().isEmpty()
                || scheme.getType() == null || scheme.getType().trim().isEmpty()
                || scheme.getBenefit() == null || scheme.getBenefit().trim().isEmpty()
                || scheme.getDeadline() == null || scheme.getDeadline().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        scheme.setActive(true);
        GovernmentScheme savedScheme = schemeRepository.save(scheme);
        return ResponseEntity.ok(savedScheme);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<GovernmentScheme> updateScheme(@PathVariable Long id, @RequestBody GovernmentScheme scheme) {
        if (!schemeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        if (scheme.getName() == null || scheme.getName().trim().isEmpty()
                || scheme.getType() == null || scheme.getType().trim().isEmpty()
                || scheme.getBenefit() == null || scheme.getBenefit().trim().isEmpty()
                || scheme.getDeadline() == null || scheme.getDeadline().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        scheme.setId(id);
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
}


