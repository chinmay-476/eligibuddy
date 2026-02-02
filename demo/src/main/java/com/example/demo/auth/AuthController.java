package com.example.demo.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
public class AuthController {
    
    @Autowired
    private UserService userService;
    
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
        
        // Validate input
        if (username == null || username.trim().isEmpty()) {
            model.addAttribute("error", "Username is required!");
            return "register";
        }
        
        if (email == null || email.trim().isEmpty()) {
            model.addAttribute("error", "Email is required!");
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
        
        if (password.length() < 6) {
            model.addAttribute("error", "Password must be at least 6 characters long!");
            return "register";
        }
        
        if (userService.registerUser(username.trim(), email.trim(), password)) {
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return "redirect:/login";
        }
        
        User user = userService.getUserByUsername(auth.getName());
        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("username", user.getUsername());
            model.addAttribute("isAdmin", userService.isAdmin(user.getUsername()));
        }
        
        return "profile";
    }
    
    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String email, 
                               @RequestParam String newUsername,
                               Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return "redirect:/login";
        }
        
        String currentUsername = auth.getName();
        
        if (userService.updateUserProfile(currentUsername, email, newUsername)) {
            model.addAttribute("success", "Profile updated successfully!");
        } else {
            model.addAttribute("error", "Failed to update profile. Username or email may already exist.");
        }
        
        User user = userService.getUserByUsername(newUsername);
        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("username", user.getUsername());
            model.addAttribute("isAdmin", userService.isAdmin(user.getUsername()));
        }
        
        return "profile";
    }
    
    @PostMapping("/profile/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                @RequestParam String newPassword,
                                @RequestParam String confirmPassword,
                                Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return "redirect:/login";
        }
        
        String username = auth.getName();
        
        // Validate new password
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "New passwords do not match!");
        } else if (newPassword.length() < 6) {
            model.addAttribute("error", "Password must be at least 6 characters long!");
        } else if (userService.changePassword(username, currentPassword, newPassword)) {
            model.addAttribute("success", "Password changed successfully!");
        } else {
            model.addAttribute("error", "Current password is incorrect!");
        }
        
        User user = userService.getUserByUsername(username);
        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("username", user.getUsername());
            model.addAttribute("isAdmin", userService.isAdmin(user.getUsername()));
        }
        
        return "profile";
    }
} 

