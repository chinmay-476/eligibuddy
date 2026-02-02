package com.example.demo.contact;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ContactService {
    
    @Autowired
    private ContactRepository contactRepository;
    
    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }
    
    public Contact saveContact(Contact contact) {
        return contactRepository.save(contact);
    }
    
    public Contact getContactById(Long id) {
        return contactRepository.findById(id).orElse(null);
    }
    
    public void deleteContact(Long id) {
        contactRepository.deleteById(id);
    }
    
    public void deleteAllContacts() {
        contactRepository.deleteAll();
    }
} 

