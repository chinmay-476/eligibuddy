package com.example.demo.web;

import com.example.demo.auth.UserService;
import com.example.demo.contact.Contact;
import com.example.demo.contact.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;        
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class HelloController {
    
    @Autowired
    private ContactService contactService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            model.addAttribute("username", auth.getName());
            model.addAttribute("isAdmin", userService.isAdmin(auth.getName()));
        } else {
            model.addAttribute("username", null);
            model.addAttribute("isAdmin", false);
        }
        return "frontend";  
    }
    
    @GetMapping("/test-auth")
    @ResponseBody
    public String testAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return "Authenticated as: " + auth.getName() + ", Authorities: " + auth.getAuthorities();
        } else {
            return "Not authenticated";
        }
    }
    
    @GetMapping("/contact")
    public String contactForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            model.addAttribute("username", auth.getName());
            model.addAttribute("isAdmin", userService.isAdmin(auth.getName()));
        } else {
            model.addAttribute("username", null);
            model.addAttribute("isAdmin", false);
        }
        return "contact";
    }
    
    @PostMapping("/contact")
    public String saveContact(@ModelAttribute Contact contact, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return "redirect:/login";
        }
        
        try {
            // Validate contact data
            if (contact == null || contact.getName() == null || contact.getName().trim().isEmpty()) {
                model.addAttribute("error", "Name is required!");
                model.addAttribute("username", auth.getName());
                model.addAttribute("isAdmin", userService.isAdmin(auth.getName()));
                return "contact";
            }
            
            if (contact.getEmail() == null || contact.getEmail().trim().isEmpty()) {
                model.addAttribute("error", "Email is required!");
                model.addAttribute("username", auth.getName());
                model.addAttribute("isAdmin", userService.isAdmin(auth.getName()));
                return "contact";
            }
            
            if (contact.getMessage() == null || contact.getMessage().trim().isEmpty()) {
                model.addAttribute("error", "Message is required!");
                model.addAttribute("username", auth.getName());
                model.addAttribute("isAdmin", userService.isAdmin(auth.getName()));
                return "contact";
            }
            
            // Trim whitespace
            contact.setName(contact.getName().trim());
            contact.setEmail(contact.getEmail().trim());
            contact.setMessage(contact.getMessage().trim());
            
            contactService.saveContact(contact);
            model.addAttribute("message", "Message sent successfully!");
            
            model.addAttribute("username", auth.getName());
            model.addAttribute("isAdmin", userService.isAdmin(auth.getName()));
            return "contact";
        } catch (Exception e) {
            System.err.println("Error saving contact: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error saving contact. Please try again.");
            
            model.addAttribute("username", auth.getName());
            model.addAttribute("isAdmin", userService.isAdmin(auth.getName()));
            return "contact";
        }
    }
    
    @GetMapping("/view-contacts")
    public String viewContacts(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return "redirect:/login";
        }
        
        if (!userService.isAdmin(auth.getName())) {
            return "redirect:/";
        }
        
        try {
            model.addAttribute("contacts", contactService.getAllContacts());
            model.addAttribute("username", auth.getName());
            model.addAttribute("isAdmin", true);
        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            model.addAttribute("username", auth.getName());
            model.addAttribute("isAdmin", true);
        }
        return "view_contacts";
    }
    
    @GetMapping("/view-users")
    public String viewUsers(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return "redirect:/login";
        }
        
        if (!userService.isAdmin(auth.getName())) {
            return "redirect:/";
        }
        
        try {
            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("username", auth.getName());
            model.addAttribute("isAdmin", true);
        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            model.addAttribute("username", auth.getName());
            model.addAttribute("isAdmin", true);
        }
        return "view_users";
    }
    
    @GetMapping("/cleanup")
    @ResponseBody
    public String cleanupDatabase() {
        try {
            // Clear all contacts
            contactService.deleteAllContacts();
            // Clear all users (except the current user if logged in)
            userService.deleteAllUsers();
            return "Database cleaned successfully! All test data removed.";
        } catch (Exception e) {
            return "Error cleaning database: " + e.getMessage();
        }
    }
    
    @GetMapping("/manage-scholarships")
    public String manageScholarships(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return "redirect:/login";
        }
        
        if (!userService.isAdmin(auth.getName())) {
            return "redirect:/";
        }
        
        try {
            model.addAttribute("username", auth.getName());
            model.addAttribute("isAdmin", true);
        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            model.addAttribute("username", auth.getName());
            model.addAttribute("isAdmin", true);
        }
        return "manage_scholarships";
    }
}


