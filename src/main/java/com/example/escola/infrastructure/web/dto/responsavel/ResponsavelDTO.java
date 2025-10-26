package com.example.escola.infrastructure.web.dto.responsavel;

import com.example.escola.domain.entities.Responsavel;

public record ResponsavelDTO(
        String nome,
        String cpf,
        String email,
        String telefoneContato,
        String parentesco
) {
    // Adicione este construtor
    public ResponsavelDTO(Responsavel responsavel) {
        this(
                responsavel.getNome(),
                responsavel.getCpf(),
                responsavel.getEmail(),
                responsavel.getTelefoneContato(),
                responsavel.getParentesco()
        );
    }
    // Método para converter DTO para entidade
    public Responsavel toEntity() {
        Responsavel responsavel = new Responsavel();
        responsavel.setNome(this.nome);
        responsavel.setCpf(this.cpf);
        responsavel.setEmail(this.email);
        responsavel.setTelefoneContato(this.telefoneContato);
        responsavel.setParentesco(this.parentesco);
        return responsavel;
    }
}

