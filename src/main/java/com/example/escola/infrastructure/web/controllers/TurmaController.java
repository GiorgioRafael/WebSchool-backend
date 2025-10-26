package com.example.escola.infrastructure.web.controllers;

import com.example.escola.application.service.TurmaService;
import com.example.escola.infrastructure.web.dto.turma.TurmaDetailDTO;
import com.example.escola.infrastructure.web.dto.turma.TurmaRequestDTO;
import com.example.escola.infrastructure.web.dto.turma.TurmaResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("turmas")
public class TurmaController {

    @Autowired
    private TurmaService service;

    @GetMapping
    public List<TurmaResponseDTO> getAllTurmas() {
        return service.getAllTurmas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TurmaResponseDTO> getTurmaById(@PathVariable Long id) {
        try {
            TurmaResponseDTO turma = service.getTurmaById(id);
            return ResponseEntity.ok(turma);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/detalhes/{id}")
    public ResponseEntity<TurmaDetailDTO> getTurmaDetalhadaPorId(@PathVariable Long id) {
        try {
            TurmaDetailDTO turma = service.getTurmaDetailedById(id);
            return ResponseEntity.ok(turma);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public ResponseEntity<TurmaResponseDTO> saveTurma(@RequestBody TurmaRequestDTO data) {
        try {
            TurmaResponseDTO savedTurma = service.createTurma(data);
            return ResponseEntity.ok(savedTurma);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<TurmaResponseDTO> updateTurma(@PathVariable Long id, @RequestBody TurmaRequestDTO data) {
        try {
            TurmaResponseDTO updatedTurma = service.updateTurma(id, data);
            return ResponseEntity.ok(updatedTurma);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTurma(@PathVariable Long id) {
        try {
            service.deleteTurma(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
