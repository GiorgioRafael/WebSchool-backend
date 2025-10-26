package com.example.escola.infrastructure.web.dto.turma;

import com.example.escola.domain.entities.Turma;
import com.example.escola.infrastructure.web.dto.aluno.AlunoSimplificadoDTO;
import com.example.escola.infrastructure.web.dto.professor.ProfessorSimplificadoDTO;

import java.util.ArrayList;
import java.util.List;

public record TurmaDetailDTO(
        Long id,
        String codigo,
        Integer anoLetivo,
        Integer semestre,
        String horario,
        String sala,
        ProfessorSimplificadoDTO professor,
        List<AlunoSimplificadoDTO> alunos
) {
    public TurmaDetailDTO(Turma turma) {
        this(
                turma.getId(),
                turma.getCodigo(),
                turma.getAnoLetivo(),
                turma.getSemestre(),
                turma.getHorario(),
                turma.getSala(),
                turma.getProfessor() != null ?
                        new ProfessorSimplificadoDTO(
                                turma.getProfessor().getId(), // Removendo toString() desnecessário
                                turma.getProfessor().getNomeCompleto(),
                                turma.getProfessor().getRegistroFuncional()
                        ) : null,
                convertAlunosList(turma)
        );
    }

    private static List<AlunoSimplificadoDTO> convertAlunosList(Turma turma) {
        if (turma.getAlunos() == null) {
            return new ArrayList<>();
        }

        return turma.getAlunos().stream()
                .map(aluno -> new AlunoSimplificadoDTO(
                        aluno.getMatricula(),
                        aluno.getNomeCompleto()
                ))
                .toList(); // Usando .toList() ao invés de collect(Collectors.toList())
    }
}
