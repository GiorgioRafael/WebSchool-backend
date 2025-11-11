package com.example.escola.infrastructure.web.controllers;

import com.example.escola.infrastructure.web.dto.professor.ProfessorDetailDTO;
import com.example.escola.infrastructure.web.dto.professor.ProfessorRequestDTO;
import com.example.escola.infrastructure.web.dto.professor.ProfessorResponseDTO;
import com.example.escola.application.service.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.example.escola.infrastructure.config.aop.AuditIgnore;

@RestController
@RequestMapping("professores")
public class ProfessorController {

    @Autowired
    private ProfessorService service;

    @GetMapping
    public List<ProfessorResponseDTO> getAllProfessores() {
        return service.getAllProfessors();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfessorResponseDTO> getProfessorById(@PathVariable String id) {
        try {
            ProfessorResponseDTO professor = service.getProfessorById(id);
            return ResponseEntity.ok(professor);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/debug/all")
    public List<ProfessorDetailDTO> getAllProfessoresDebug() {
        return service.getAllProfessorsDetailedData();
    }

    @GetMapping("/debug/{id}")
    public ResponseEntity<ProfessorDetailDTO> getProfessorDetailedById(@PathVariable String id) {
        try {
            ProfessorDetailDTO professor = service.getProfessorDetailedDataById(id);
            return ResponseEntity.ok(professor);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public ResponseEntity<ProfessorResponseDTO> saveProfessor(@RequestBody ProfessorRequestDTO data) {
        ProfessorResponseDTO savedProfessor = service.createProfessor(data);
        return ResponseEntity.ok(savedProfessor);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @AuditIgnore
    @PutMapping("/{id}")
    public ResponseEntity<ProfessorResponseDTO> updateProfessor(@PathVariable String id, @RequestBody ProfessorRequestDTO data, java.security.Principal principal) {
        try {
            String username = (principal != null && principal.getName() != null && !principal.getName().isBlank()) ? principal.getName() : com.example.escola.util.SecurityUtils.getCurrentUsername();
            ProfessorResponseDTO updatedProfessor = service.updateProfessor(id, data, username);
            return ResponseEntity.ok(updatedProfessor);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfessor(@PathVariable String id) {
        try {
            service.deleteProfessor(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
