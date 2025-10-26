package com.example.escola.infrastructure.web.dto.turma;

import java.util.List;

public record TurmaRequestDTO(
        String codigo,
        Integer anoLetivo,
        Integer semestre,
        String horario,
        String sala,
        String professorId,
        List<Long> alunosMatriculas
) {}
