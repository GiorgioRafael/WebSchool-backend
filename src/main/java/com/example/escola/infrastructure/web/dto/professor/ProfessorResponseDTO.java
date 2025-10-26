package com.example.escola.infrastructure.web.dto.professor;

import com.example.escola.infrastructure.web.dto.endereco.EnderecoDTO;
import com.example.escola.domain.entities.Professor;

import java.time.LocalDate;

public record ProfessorResponseDTO(
        String id,
        String registroFuncional, //codigo funcional gerado pelo sistema
        String nomeCompleto,
        String email,
        LocalDate dataContratacao,
        EnderecoDTO endereco
) {
    public ProfessorResponseDTO(Professor professor) {
        this(
                professor.getId(),
                professor.getRegistroFuncional(),
                professor.getNomeCompleto(),
                professor.getEmail(),
                professor.getDataContratacao(),
                new EnderecoDTO(professor.getEndereco())
        );
    }
}
