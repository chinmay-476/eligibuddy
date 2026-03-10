package com.example.demo.contact;

import com.example.demo.config.MongoSequenceService;
import org.springframework.lang.Nullable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ContactService {

    private static final String CONTACT_SEQUENCE = "contacts_sequence";

    private final ContactRepository contactRepository;
    private final MongoSequenceService mongoSequenceService;

    public ContactService(ContactRepository contactRepository, MongoSequenceService mongoSequenceService) {
        this.contactRepository = contactRepository;
        this.mongoSequenceService = mongoSequenceService;
    }
    
    public List<Contact> getAllContacts() {
        return contactRepository.findAllByOrderByIdDesc();
    }

    public List<Contact> getContactsForUser(@NonNull String username) {
        return contactRepository.findBySubmittedByOrderByIdDesc(username);
    }
    
    public Contact saveContact(@NonNull Contact contact) {
        if (contact.getId() == null) {
            contact.setId(mongoSequenceService.generateSequence(CONTACT_SEQUENCE));
        }
        if (contact.getSubmittedAt() == null) {
            contact.setSubmittedAt(LocalDateTime.now());
        }
        return contactRepository.save(contact);
    }
    
    public Optional<Contact> getContactById(@NonNull Long id) {
        return contactRepository.findById(id);
    }
    
    public void deleteContact(@NonNull Long id) {
        contactRepository.deleteById(id);
    }
    
    public void deleteAllContacts() {
        contactRepository.deleteAll();
    }

    public Optional<Contact> saveAdminReply(@NonNull Long id, @Nullable String replyMessage, @NonNull String adminUsername) {
        String normalizedReply = replyMessage == null ? null : replyMessage.trim();
        if (normalizedReply == null || normalizedReply.isEmpty()) {
            return Optional.empty();
        }

        return contactRepository.findById(id)
                .map(contact -> {
                    contact.setAdminReply(normalizedReply);
                    contact.setRepliedBy(adminUsername);
                    contact.setRepliedAt(LocalDateTime.now());
                    return contactRepository.save(contact);
                });
    }
} 

