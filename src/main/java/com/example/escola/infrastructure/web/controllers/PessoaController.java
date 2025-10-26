package com.example.escola.infrastructure.web.controllers;

import com.example.escola.infrastructure.web.dto.pessoa.PessoaRequestDTO;
import com.example.escola.infrastructure.web.dto.pessoa.PessoaResponseDTO;
import com.example.escola.application.service.PessoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("pessoas")
public class PessoaController {

    @Autowired
    private PessoaService service;

    @GetMapping
    public List<PessoaResponseDTO> getAllPessoas() {
        return service.getAllPessoas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PessoaResponseDTO> getPessoaById(@PathVariable String id) {
        try {
            PessoaResponseDTO pessoa = service.getPessoaById(id);
            return ResponseEntity.ok(pessoa);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public ResponseEntity<PessoaResponseDTO> savePessoa(@RequestBody PessoaRequestDTO data) {
        PessoaResponseDTO savedPessoa = service.createPessoa(data);
        return ResponseEntity.ok(savedPessoa);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<PessoaResponseDTO> updatePessoa(@PathVariable String id, @RequestBody PessoaRequestDTO data) {
        try {
            PessoaResponseDTO updatedPessoa = service.updatePessoa(id, data);
            return ResponseEntity.ok(updatedPessoa);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePessoa(@PathVariable String id) {
        try {
            service.deletePessoa(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
