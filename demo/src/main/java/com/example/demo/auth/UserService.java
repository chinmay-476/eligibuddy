package com.example.demo.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public boolean registerUser(String username, String email, String password) {
        System.out.println("Attempting to register user: " + username + " with email: " + email);
        
        if (userRepository.existsByUsername(username)) {
            System.out.println("Username already exists: " + username);
            return false;
        }
        
        if (userRepository.existsByEmail(email)) {
            System.out.println("Email already exists: " + email);
            return false;
        }
        
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(User.Role.USER);
        
        try {
            userRepository.save(user);
            System.out.println("User registered successfully: " + username);
            return true;
        } catch (Exception e) {
            System.out.println("Error registering user: " + e.getMessage());
            return false;
        }
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public boolean isAdmin(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.isPresent() && user.get().getRole() == User.Role.ADMIN;
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public void deleteAllUsers() {
        userRepository.deleteAll();
    }
    
    public boolean updateUserProfile(String username, String email, String newUsername) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Check if new username already exists (if changing username)
            if (!username.equals(newUsername) && userRepository.existsByUsername(newUsername)) {
                return false;
            }
            
            // Check if new email already exists (if changing email)
            if (!email.equals(user.getEmail()) && userRepository.existsByEmail(email)) {
                return false;
            }
            
            user.setUsername(newUsername);
            user.setEmail(email);
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            System.out.println("Error updating user profile: " + e.getMessage());
            return false;
        }
    }
    
    public boolean changePassword(String username, String currentPassword, String newPassword) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Verify current password
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                return false;
            }
            
            // Update password
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            System.out.println("Error changing password: " + e.getMessage());
            return false;
        }
    }
    
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
} 

