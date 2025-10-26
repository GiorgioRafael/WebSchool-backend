package com.example.escola.application.service;

import com.example.escola.application.repositories.AlunoRepository;
import com.example.escola.application.repositories.ProfessorRepository;
import com.example.escola.application.repositories.TurmaRepository;
import com.example.escola.domain.entities.Aluno;
import com.example.escola.domain.entities.Professor;
import com.example.escola.domain.entities.Turma;
import com.example.escola.infrastructure.web.dto.turma.TurmaDetailDTO;
import com.example.escola.infrastructure.web.dto.turma.TurmaRequestDTO;
import com.example.escola.infrastructure.web.dto.turma.TurmaResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TurmaService {

    @Autowired
    private TurmaRepository turmaRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    public List<TurmaResponseDTO> getAllTurmas() {
        return turmaRepository.findAll().stream()
                .map(TurmaResponseDTO::new)
                .toList();
    }

    public TurmaResponseDTO getTurmaById(Long id) {
        Turma turma = turmaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turma não encontrada"));
        return new TurmaResponseDTO(turma);
    }

    public TurmaDetailDTO getTurmaDetailedById(Long id) {
        Turma turma = turmaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turma não encontrada"));
        return new TurmaDetailDTO(turma);
    }

    @Transactional
    public TurmaResponseDTO createTurma(TurmaRequestDTO data) {
        // Verificar se já existe uma turma com o código fornecido
        if (turmaRepository.existsByCodigo(data.codigo())) {
            throw new RuntimeException("Já existe uma turma com o código: " + data.codigo());
        }

        // Buscar o professor
        Professor professor = professorRepository.findById(data.professorId())
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        // Criar nova turma
        Turma novaTurma = new Turma();
        novaTurma.setCodigo(data.codigo());
        novaTurma.setAnoLetivo(data.anoLetivo());
        novaTurma.setSemestre(data.semestre());
        novaTurma.setHorario(data.horario());
        novaTurma.setSala(data.sala());
        novaTurma.setProfessor(professor);

        // Adicionar alunos, se houver
        if (data.alunosMatriculas() != null && !data.alunosMatriculas().isEmpty()) {
            List<Aluno> alunos = new ArrayList<>();
            for (Long matricula : data.alunosMatriculas()) {
                Aluno aluno = alunoRepository.findByMatricula(matricula)
                        .orElseThrow(() -> new RuntimeException("Aluno com matrícula " + matricula + " não encontrado"));
                alunos.add(aluno);
            }
            novaTurma.setAlunos(alunos);
        }

        // Salvar a turma
        Turma turmaSalva = turmaRepository.save(novaTurma);
        return new TurmaResponseDTO(turmaSalva);
    }

    @Transactional
    public TurmaResponseDTO updateTurma(Long id, TurmaRequestDTO data) {
        Turma turma = turmaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turma não encontrada"));

        // Verificar se o código já existe e não é da turma atual
        if (!turma.getCodigo().equals(data.codigo()) && turmaRepository.existsByCodigo(data.codigo())) {
            throw new RuntimeException("Já existe uma turma com o código: " + data.codigo());
        }

        // Buscar o professor
        Professor professor = professorRepository.findById(data.professorId())
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        // Atualizar dados
        turma.setCodigo(data.codigo());
        turma.setAnoLetivo(data.anoLetivo());
        turma.setSemestre(data.semestre());
        turma.setHorario(data.horario());
        turma.setSala(data.sala());
        turma.setProfessor(professor);

        // Atualizar alunos, se houver
        if (data.alunosMatriculas() != null) {
            List<Aluno> alunos = new ArrayList<>();
            for (Long matricula : data.alunosMatriculas()) {
                Aluno aluno = alunoRepository.findByMatricula(matricula)
                        .orElseThrow(() -> new RuntimeException("Aluno com matrícula " + matricula + " não encontrado"));
                alunos.add(aluno);
            }
            turma.setAlunos(alunos);
        }

        // Salvar a turma
        Turma turmaAtualizada = turmaRepository.save(turma);
        return new TurmaResponseDTO(turmaAtualizada);
    }

    @Transactional
    public void deleteTurma(Long id) {
        if (!turmaRepository.existsById(id)) {
            throw new RuntimeException("Turma não encontrada");
        }
        turmaRepository.deleteById(id);
    }
}
