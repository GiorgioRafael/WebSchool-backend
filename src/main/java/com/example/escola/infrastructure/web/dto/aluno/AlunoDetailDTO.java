package com.example.escola.infrastructure.web.dto.aluno;

import com.example.escola.domain.entities.Aluno;
import com.example.escola.infrastructure.web.dto.endereco.EnderecoDTO;
import com.example.escola.infrastructure.web.dto.responsavel.ResponsavelDTO;

import java.time.LocalDate;

public record AlunoDetailDTO(
        String id,
        Long matricula,
        String nomeCompleto,
        String email,
        String telefoneContato,
        String cpf,
        String rg,
        LocalDate dataNascimento,
        EnderecoDTO endereco,
        ResponsavelDTO responsavel
) {
    public AlunoDetailDTO(Aluno aluno) {
        this(
                aluno.getId(),
                aluno.getMatricula(),
                aluno.getNomeCompleto(),
                aluno.getEmail(),
                aluno.getTelefoneContato(),
                aluno.getCpf(),
                aluno.getRg(),
                aluno.getDataNascimento(),
                aluno.getEndereco() != null ? new EnderecoDTO(aluno.getEndereco()) : null,
                aluno.getResponsavel() != null ? new ResponsavelDTO(aluno.getResponsavel()) : null
        );
    }
}

