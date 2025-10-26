package com.example.escola.infrastructure.web.dto.professor;

import com.example.escola.infrastructure.web.dto.endereco.EnderecoDTO;

import java.time.LocalDate;

public record ProfessorRequestDTO(
        String nomeCompleto,
        String email,
        String cpf,
        String rg,
        LocalDate dataNascimento,
        String telefoneContato,
        LocalDate dataContratacao,

        //Dados do Endereço (aninhado)
        EnderecoDTO endereco,

        //Dados para criacao do user
        String login,
        String senha

) {}
