package com.logic.springjwt.services;

import com.logic.springjwt.models.InformationSystem;
import com.logic.springjwt.repository.InformationSystemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InformationSystemService {

    private final InformationSystemRepository repository;

    // ✅ Constructor Injection (IMPORTANT)
    public InformationSystemService(InformationSystemRepository repository) {
        this.repository = repository;
    }

    public List<InformationSystem> getAll() {
        return repository.findAll();
    }

}
