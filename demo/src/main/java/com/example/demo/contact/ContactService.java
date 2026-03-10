package com.example.demo.contact;

import com.example.demo.config.MongoSequenceService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
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
        return contactRepository.findAll();
    }
    
    public Contact saveContact(@NonNull Contact contact) {
        if (contact.getId() == null) {
            contact.setId(mongoSequenceService.generateSequence(CONTACT_SEQUENCE));
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
} 

