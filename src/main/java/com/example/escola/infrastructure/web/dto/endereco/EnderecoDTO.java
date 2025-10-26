package com.example.escola.infrastructure.web.dto.endereco; // Sugestão de novo pacote para DTOs compartilhados

import com.example.escola.domain.entities.Endereco;

public record EnderecoDTO(
        String cep,
        String logradouro,
        String numero,
        String bairro,
        String cidade,
        String estado
) {
    // Novo construtor que aceita a entidade Endereco
    public EnderecoDTO(Endereco endereco) {
        this(
                endereco.getCep(),
                endereco.getLogradouro(),
                endereco.getNumero(),
                endereco.getBairro(),
                endereco.getCidade(),
                endereco.getEstado()
        );
    }
}