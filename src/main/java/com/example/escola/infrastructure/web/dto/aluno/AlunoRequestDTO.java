package com.example.escola.infrastructure.web.dto.aluno;

import com.example.escola.infrastructure.web.dto.endereco.EnderecoDTO;
import com.example.escola.infrastructure.web.dto.responsavel.ResponsavelDTO;

import java.time.LocalDate;

public record AlunoRequestDTO(
        String nomeCompleto,
        String email,
        String telefoneContato,
        String cpf,
        String rg,
        LocalDate dataNascimento,

        // DTOs aninhados
        EnderecoDTO endereco,
        ResponsavelDTO responsavel
) {}
