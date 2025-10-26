package com.example.escola.infrastructure.web.dto.professor;

import com.example.escola.domain.entities.Disciplina;
import com.example.escola.domain.entities.Professor;
import com.example.escola.domain.entities.Turma;
import com.example.escola.domain.enums.ProfessorStatus;
import com.example.escola.infrastructure.web.dto.endereco.EnderecoDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public record ProfessorDetailDTO(
        String id,
        String registroFuncional,
        // Dados da pessoa
        String nomeCompleto,
        String email,
        String cpf,
        String rg,
        LocalDate dataNascimento,
        String telefoneContato,
        // Dados do endereço
        EnderecoDTO endereco,
        // Dados específicos do professor
        LocalDate dataContratacao,
        ProfessorStatus professorStatus,
        String formacaoAcademica,
        String biografia,
        // Relacionamentos
        List<String> turmasIds,
        List<String> disciplinasIds
) {
    public ProfessorDetailDTO(Professor professor) {
        this(
                professor.getId(),
                professor.getRegistroFuncional(),
                professor.getNomeCompleto(),
                professor.getEmail(),
                professor.getCpf(),
                professor.getRg(),
                professor.getDataNascimento(),
                professor.getTelefoneContato(),
                professor.getEndereco() != null ? new EnderecoDTO(professor.getEndereco()) : null,
                professor.getDataContratacao(),
                professor.getProfessorStatus(),
                professor.getFormacaoAcademica(),
                professor.getBiografia(),
                professor.getTurmas() != null ?
                        professor.getTurmas().stream()
                                .map(turma -> turma.getId().toString())
                                .collect(Collectors.toList()) :
                        null,
                professor.getDisciplinas() != null ?
                        professor.getDisciplinas().stream()
                                .map(disciplina -> disciplina.getId().toString())
                                .collect(Collectors.toList()) :
                        null
        );
    }

}
