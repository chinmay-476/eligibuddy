package com.example.demo.auth;

import com.example.demo.config.MongoSequenceService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final String USER_SEQUENCE = "users_sequence";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MongoSequenceService mongoSequenceService;

    public DataInitializer(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            MongoSequenceService mongoSequenceService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mongoSequenceService = mongoSequenceService;
    }
    
    @Override
    public void run(String... args) throws Exception {
        // Create default users if they don't exist
        createDefaultUsers();
        System.out.println("Database initialized with default users.");
        System.out.println("Default credentials:");
        System.out.println("Admin - Username: admin, Password: admin123");
        System.out.println("User - Username: user, Password: user123");
    }
    
    private void createDefaultUsers() {
        // Create admin user if it doesn't exist
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setId(mongoSequenceService.generateSequence(USER_SEQUENCE));
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(User.Role.ADMIN);
            userRepository.save(admin);
            System.out.println("Admin user created successfully");
        }
        
        // Create regular user if it doesn't exist
        if (!userRepository.existsByUsername("user")) {
            User user = new User();
            user.setId(mongoSequenceService.generateSequence(USER_SEQUENCE));
            user.setUsername("user");
            user.setEmail("user@example.com");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRole(User.Role.USER);
            userRepository.save(user);
            System.out.println("User created successfully");
        }
    }
} 

