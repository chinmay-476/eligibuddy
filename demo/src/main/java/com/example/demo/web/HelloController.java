package com.example.demo.web;

import com.example.demo.auth.UserService;
import com.example.demo.contact.Contact;
import com.example.demo.contact.ContactService;
import com.example.demo.validation.InputValidationUtils;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;        
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class HelloController {

    private final ContactService contactService;
    private final UserService userService;

    public HelloController(ContactService contactService, UserService userService) {
        this.contactService = contactService;
        this.userService = userService;
    }
    
    @GetMapping("/")
    public String home(Model model) {
        getAuthenticatedUsername().ifPresentOrElse(
                username -> populateUserContext(model, username),
                () -> {
                    model.addAttribute("username", null);
                    model.addAttribute("isAdmin", false);
                }
        );
        return "frontend";  
    }
    
    @GetMapping("/test-auth")
    @ResponseBody
    public String testAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (isAuthenticated(auth)) {
            return "Authenticated as: " + auth.getName() + ", Authorities: " + auth.getAuthorities();
        } else {
            return "Not authenticated";
        }
    }
    
    @GetMapping("/contact")
    public String contactForm(Model model) {
        getAuthenticatedUsername().ifPresentOrElse(
                username -> {
                    populateUserContext(model, username);
                    model.addAttribute("contactHistory", contactService.getContactsForUser(username));
                },
                () -> {
                    model.addAttribute("username", null);
                    model.addAttribute("isAdmin", false);
                    model.addAttribute("contactHistory", java.util.List.of());
                }
        );
        if (!model.containsAttribute("contact")) {
            model.addAttribute("contact", new Contact());
        }
        return "contact";
    }
    
    @PostMapping("/contact")
    public String saveContact(@ModelAttribute("contact") @Nullable Contact contact, Model model) {
        Optional<String> authenticatedUsername = getAuthenticatedUsername();
        if (authenticatedUsername.isEmpty()) {
            return "redirect:/login";
        }

        String username = authenticatedUsername.orElseThrow();

        try {
            // Validate contact data
            if (contact == null) {
                model.addAttribute("error", "Contact details are required!");
                populateUserContext(model, username);
                model.addAttribute("contact", new Contact());
                return "contact";
            }

            contact.setName(trimToNull(contact.getName()));
            contact.setEmail(trimToNull(contact.getEmail()));
            contact.setPhoneNumber(trimToNull(contact.getPhoneNumber()));
            contact.setMessage(trimToNull(contact.getMessage()));
            contact.setSubmittedBy(username);

            if (contact.getName() == null || contact.getName().isEmpty()) {
                model.addAttribute("error", "Name is required!");
                populateUserContext(model, username);
                model.addAttribute("contactHistory", contactService.getContactsForUser(username));
                model.addAttribute("contact", contact);
                return "contact";
            }
            
            if (!InputValidationUtils.isValidName(contact.getName())) {
                model.addAttribute("error", "Please enter a valid name.");
                populateUserContext(model, username);
                model.addAttribute("contactHistory", contactService.getContactsForUser(username));
                model.addAttribute("contact", contact);
                return "contact";
            }

            if (contact.getEmail() == null || contact.getEmail().isEmpty()) {
                model.addAttribute("error", "Email is required!");
                populateUserContext(model, username);
                model.addAttribute("contactHistory", contactService.getContactsForUser(username));
                model.addAttribute("contact", contact);
                return "contact";
            }

            if (!InputValidationUtils.isValidEmail(contact.getEmail())) {
                model.addAttribute("error", "Please enter a valid email address.");
                populateUserContext(model, username);
                model.addAttribute("contactHistory", contactService.getContactsForUser(username));
                model.addAttribute("contact", contact);
                return "contact";
            }

            if (contact.getPhoneNumber() == null || contact.getPhoneNumber().isEmpty()) {
                model.addAttribute("error", "Phone number is required!");
                populateUserContext(model, username);
                model.addAttribute("contactHistory", contactService.getContactsForUser(username));
                model.addAttribute("contact", contact);
                return "contact";
            }

            if (!InputValidationUtils.isValidPhoneNumber(contact.getPhoneNumber())) {
                model.addAttribute("error", "Please enter a valid 10-digit mobile number.");
                populateUserContext(model, username);
                model.addAttribute("contactHistory", contactService.getContactsForUser(username));
                model.addAttribute("contact", contact);
                return "contact";
            }
            
            if (contact.getMessage() == null || contact.getMessage().isEmpty()) {
                model.addAttribute("error", "Message is required!");
                populateUserContext(model, username);
                model.addAttribute("contactHistory", contactService.getContactsForUser(username));
                model.addAttribute("contact", contact);
                return "contact";
            }

            if (contact.getMessage().length() < 10 || contact.getMessage().length() > 1000) {
                model.addAttribute("error", "Message must be between 10 and 1000 characters.");
                populateUserContext(model, username);
                model.addAttribute("contactHistory", contactService.getContactsForUser(username));
                model.addAttribute("contact", contact);
                return "contact";
            }

            contactService.saveContact(contact);
            model.addAttribute("message", "Message sent successfully!");
            populateUserContext(model, username);
            model.addAttribute("contactHistory", contactService.getContactsForUser(username));
            model.addAttribute("contact", new Contact());
            return "contact";
        } catch (Exception e) {
            System.err.println("Error saving contact: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error saving contact. Please try again.");
            
            populateUserContext(model, username);
            model.addAttribute("contactHistory", contactService.getContactsForUser(username));
            model.addAttribute("contact", contact);
            return "contact";
        }
    }

    @PostMapping("/view-contacts/{id}/reply")
    public String replyToContact(@PathVariable Long id,
                                 @RequestParam("replyMessage") String replyMessage,
                                 Model model) {
        Optional<String> authenticatedUsername = getAuthenticatedUsername();
        if (authenticatedUsername.isEmpty()) {
            return "redirect:/login";
        }

        String username = authenticatedUsername.orElseThrow();
        if (!userService.isAdmin(username)) {
            return "redirect:/";
        }

        try {
            if (trimToNull(replyMessage) == null) {
                model.addAttribute("error", "Reply message is required.");
            } else if (replyMessage.trim().length() > 2000) {
                model.addAttribute("error", "Reply must be 2000 characters or fewer.");
            } else if (contactService.saveAdminReply(id, replyMessage, username).isPresent()) {
                model.addAttribute("message", "Reply saved successfully.");
            } else {
                model.addAttribute("error", "Unable to save the reply for this contact.");
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
        }

        model.addAttribute("contacts", contactService.getAllContacts());
        populateUserContext(model, username);
        return "view_contacts";
    }
    
    @GetMapping("/view-contacts")
    public String viewContacts(Model model) {
        Optional<String> authenticatedUsername = getAuthenticatedUsername();
        if (authenticatedUsername.isEmpty()) {
            return "redirect:/login";
        }

        String username = authenticatedUsername.orElseThrow();
        if (!userService.isAdmin(username)) {
            return "redirect:/";
        }
        
        try {
            model.addAttribute("contacts", contactService.getAllContacts());
            populateUserContext(model, username);
        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            populateUserContext(model, username);
        }
        return "view_contacts";
    }
    
    @GetMapping("/view-users")
    public String viewUsers(Model model) {
        Optional<String> authenticatedUsername = getAuthenticatedUsername();
        if (authenticatedUsername.isEmpty()) {
            return "redirect:/login";
        }

        String username = authenticatedUsername.orElseThrow();
        if (!userService.isAdmin(username)) {
            return "redirect:/";
        }
        
        try {
            model.addAttribute("users", userService.getAllUsers());
            populateUserContext(model, username);
        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            populateUserContext(model, username);
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
        Optional<String> authenticatedUsername = getAuthenticatedUsername();
        if (authenticatedUsername.isEmpty()) {
            return "redirect:/login";
        }

        String username = authenticatedUsername.orElseThrow();
        if (!userService.isAdmin(username)) {
            return "redirect:/";
        }
        
        try {
            populateUserContext(model, username);
        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            populateUserContext(model, username);
        }
        return "manage_scholarships";
    }

    @GetMapping("/manage-schemes")
    public String manageSchemes(Model model) {
        Optional<String> authenticatedUsername = getAuthenticatedUsername();
        if (authenticatedUsername.isEmpty()) {
            return "redirect:/login";
        }

        String username = authenticatedUsername.orElseThrow();
        if (!userService.isAdmin(username)) {
            return "redirect:/";
        }

        try {
            populateUserContext(model, username);
        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            populateUserContext(model, username);
        }
        return "manage_schemes";
    }

    @GetMapping("/manage-exams")
    public String manageExams(Model model) {
        Optional<String> authenticatedUsername = getAuthenticatedUsername();
        if (authenticatedUsername.isEmpty()) {
            return "redirect:/login";
        }

        String username = authenticatedUsername.orElseThrow();
        if (!userService.isAdmin(username)) {
            return "redirect:/";
        }

        try {
            populateUserContext(model, username);
        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            populateUserContext(model, username);
        }
        return "manage_exams";
    }

    @GetMapping("/manage-jobs")
    public String manageJobs(Model model) {
        Optional<String> authenticatedUsername = getAuthenticatedUsername();
        if (authenticatedUsername.isEmpty()) {
            return "redirect:/login";
        }

        String username = authenticatedUsername.orElseThrow();
        if (!userService.isAdmin(username)) {
            return "redirect:/";
        }

        try {
            populateUserContext(model, username);
        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            populateUserContext(model, username);
        }
        return "manage_jobs";
    }

    private Optional<String> getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!isAuthenticated(authentication)) {
            return Optional.empty();
        }
        return Optional.of(authentication.getName());
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName());
    }

    private void populateUserContext(Model model, String username) {
        model.addAttribute("username", username);
        model.addAttribute("isAdmin", userService.isAdmin(username));
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


