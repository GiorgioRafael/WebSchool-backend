package com.example.escola.infrastructure.web.dto.pessoa;

import com.example.escola.infrastructure.web.dto.endereco.EnderecoDTO;

import java.time.LocalDate;

public record PessoaRequestDTO(
        String nomeCompleto,
        String email,
        String cpf,
        String rg,
        LocalDate dataNascimento,
        String telefoneContato,
        EnderecoDTO endereco
) {}
