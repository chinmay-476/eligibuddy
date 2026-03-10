package com.example.demo.auth;

import com.example.demo.validation.InputValidationUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if ("access_denied".equals(error)) {
            model.addAttribute("error", "Access denied! Please login to continue.");
        } else if ("true".equals(error)) {
            model.addAttribute("error", "Invalid username or password. Please try again.");
        }
        return "login";
    }
    
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }
    
    @PostMapping("/register")
    public String registerUser(@RequestParam String username, 
                              @RequestParam String email, 
                              @RequestParam String password,
                              @RequestParam String confirmPassword,
                              Model model) {

        username = trimToNull(username);
        email = trimToNull(email);
        model.addAttribute("usernameValue", username == null ? "" : username);
        model.addAttribute("emailValue", email == null ? "" : email);
        
        // Validate input
        if (username == null || username.isEmpty()) {
            model.addAttribute("error", "Username is required!");
            return "register";
        }

        if (!InputValidationUtils.isValidUsername(username)) {
            model.addAttribute("error", "Username must start with a letter and be 3-20 characters using letters, numbers, or underscores.");
            return "register";
        }
        
        if (email == null || email.isEmpty()) {
            model.addAttribute("error", "Email is required!");
            return "register";
        }

        if (!InputValidationUtils.isValidEmail(email)) {
            model.addAttribute("error", "Please enter a valid email address.");
            return "register";
        }
        
        if (password == null || password.trim().isEmpty()) {
            model.addAttribute("error", "Password is required!");
            return "register";
        }
        
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match!");
            return "register";
        }
        
        if (!InputValidationUtils.isValidPassword(password)) {
            model.addAttribute("error", "Password must be 8-64 characters and include uppercase, lowercase, and a number.");
            return "register";
        }
        
        if (userService.registerUser(username, email, password)) {
            model.addAttribute("success", "Registration successful! Please login with your credentials.");
            return "login";
        } else {
            model.addAttribute("error", "Username or email already exists!");
            return "register";
        }
    }
    
    @GetMapping("/debug-users")
    @ResponseBody
    public String debugUsers() {
        StringBuilder sb = new StringBuilder();
        sb.append("Registered Users:\n");
        try {
            List<User> users = userService.getAllUsers();
            for (User user : users) {
                sb.append("Username: ").append(user.getUsername())
                  .append(", Email: ").append(user.getEmail())
                  .append(", Role: ").append(user.getRole())
                  .append(", Password Hash: ").append(user.getPassword().substring(0, Math.min(20, user.getPassword().length()))).append("...")
                  .append("\n");
            }
        } catch (Exception e) {
            sb.append("Error: ").append(e.getMessage());
        }
        return sb.toString();
    }
    
    @GetMapping("/users-info")
    @ResponseBody
    public String usersInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Current Users in System ===\n\n");
        try {
            List<User> users = userService.getAllUsers();
            if (users.isEmpty()) {
                sb.append("No users found in the system.\n");
                sb.append("Default users should be created automatically on startup.\n");
            } else {
                sb.append("Found ").append(users.size()).append(" user(s):\n\n");
                for (User user : users) {
                    sb.append("• ").append(user.getUsername())
                      .append(" (").append(user.getRole()).append(")")
                      .append(" - ").append(user.getEmail())
                      .append("\n");
                }
                sb.append("\nYou can login with any of these accounts.\n");
            }
        } catch (Exception e) {
            sb.append("Error retrieving users: ").append(e.getMessage());
        }
        return sb.toString();
    }
    
    @GetMapping("/profile")
    public String profilePage(Model model) {
        Optional<String> authenticatedUsername = getAuthenticatedUsername();
        if (authenticatedUsername.isEmpty()) {
            return "redirect:/login";
        }

        populateProfileModel(model, authenticatedUsername.orElseThrow());
        return "profile";
    }
    
    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String email, 
                               @RequestParam String newUsername,
                               Model model) {
        Optional<String> authenticatedUsername = getAuthenticatedUsername();
        if (authenticatedUsername.isEmpty()) {
            return "redirect:/login";
        }

        String currentUsername = authenticatedUsername.orElseThrow();
        String trimmedEmail = trimToNull(email);
        String trimmedUsername = trimToNull(newUsername);

        if (!InputValidationUtils.isValidUsername(trimmedUsername)) {
            model.addAttribute("error", "Username must start with a letter and be 3-20 characters using letters, numbers, or underscores.");
        } else if (!InputValidationUtils.isValidEmail(trimmedEmail)) {
            model.addAttribute("error", "Please enter a valid email address.");
        } else if (userService.updateUserProfile(currentUsername, trimmedEmail, trimmedUsername)) {
            model.addAttribute("success", "Profile updated successfully!");
        } else {
            model.addAttribute("error", "Failed to update profile. Username or email may already exist.");
        }

        userService.getUserByUsername(trimmedUsername)
                .or(() -> userService.getUserByUsername(currentUsername))
                .ifPresent(user -> {
                    model.addAttribute("user", user);
                    model.addAttribute("username", user.getUsername());
                    model.addAttribute("isAdmin", userService.isAdmin(user.getUsername()));
                });

        return "profile";
    }
    
    @PostMapping("/profile/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                @RequestParam String newPassword,
                                @RequestParam String confirmPassword,
                                Model model) {
        Optional<String> authenticatedUsername = getAuthenticatedUsername();
        if (authenticatedUsername.isEmpty()) {
            return "redirect:/login";
        }

        String username = authenticatedUsername.orElseThrow();
        
        // Validate new password
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "New passwords do not match!");
        } else if (!InputValidationUtils.isValidPassword(newPassword)) {
            model.addAttribute("error", "Password must be 8-64 characters and include uppercase, lowercase, and a number.");
        } else if (userService.changePassword(username, currentPassword, newPassword)) {
            model.addAttribute("success", "Password changed successfully!");
        } else {
            model.addAttribute("error", "Current password is incorrect!");
        }
        
        populateProfileModel(model, username);
        return "profile";
    }

    private Optional<String> getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            return Optional.empty();
        }
        return Optional.of(authentication.getName());
    }

    private void populateProfileModel(Model model, String username) {
        userService.getUserByUsername(username).ifPresent(user -> {
            model.addAttribute("user", user);
            model.addAttribute("username", user.getUsername());
            model.addAttribute("isAdmin", userService.isAdmin(user.getUsername()));
        });
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

