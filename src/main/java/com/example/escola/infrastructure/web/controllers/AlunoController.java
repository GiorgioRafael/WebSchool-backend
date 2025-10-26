package com.example.escola.infrastructure.web.controllers;

import com.example.escola.infrastructure.web.dto.aluno.AlunoRequestDTO;
import com.example.escola.infrastructure.web.dto.aluno.AlunoResponseDTO;
import com.example.escola.domain.entities.Aluno;
import com.example.escola.application.repositories.AlunoRepository;
import com.example.escola.application.service.AlunoService; // Importe o service
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("alunos")
public class AlunoController {

    // 1. INJETE O SERVICE EM VEZ DO REPOSITORY
    @Autowired
    private AlunoService service;

    // Injeção do repository ainda é útil para operações simples como GET e DELETE
    @Autowired
    private AlunoRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders="*")
    @PostMapping
    public ResponseEntity<Void> saveAluno(@RequestBody AlunoRequestDTO data) {
        // 2. CHAME O SERVICE PARA EXECUTAR A LÓGICA DE NEGÓCIO
        service.matricularNovoAluno(data);
        return ResponseEntity.ok().build();
    }

    @CrossOrigin(origins = "*", allowedHeaders="*")
    @PostMapping("/batch")
    public ResponseEntity<Void> saveAlunos(@RequestBody List<AlunoRequestDTO> data) {
        // 2. CHAME O SERVICE PARA EXECUTAR A LÓGICA DE NEGÓCIO
        service.matricularNovosAlunos(data);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public List<AlunoResponseDTO> getAll() {
        return repository.findAll().stream()
                .map(AlunoResponseDTO::new)
                .toList();
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{matricula}") // Alterado para {matricula} para consistência
    public void deleteAluno(@PathVariable Long matricula) {
        // Buscar o aluno pela matrícula e depois deletar pelo ID
        Aluno aluno = repository.findByMatricula(matricula)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
        repository.deleteById(aluno.getId());
    }

    @PutMapping("/{matricula}") // Alterado para {matricula}
    public void updateAluno(@PathVariable Long matricula, @RequestBody AlunoRequestDTO data){
        // Buscar o aluno pela matrícula e depois atualizar
        Aluno alunoData = repository.findByMatricula(matricula)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
        // ... (lógica de update)
        repository.save(alunoData);
    }
}
