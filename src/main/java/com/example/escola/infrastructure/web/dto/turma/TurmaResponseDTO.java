package com.example.escola.infrastructure.web.dto.turma;

import com.example.escola.domain.entities.Turma;

public record TurmaResponseDTO(
        Long id,
        String codigo,
        Integer anoLetivo,
        Integer semestre,
        String horario,
        String sala,
        String professorNome,
        String professorId,
        Integer quantidadeAlunos
) {
    public TurmaResponseDTO(Turma turma) {
        this(
                turma.getId(),
                turma.getCodigo(),
                turma.getAnoLetivo(),
                turma.getSemestre(),
                turma.getHorario(),
                turma.getSala(),
                turma.getProfessor() != null ? turma.getProfessor().getNomeCompleto() : null,
                turma.getProfessor() != null ? turma.getProfessor().getId() : null,
                turma.getAlunos() != null ? turma.getAlunos().size() : 0
        );
    }
}
