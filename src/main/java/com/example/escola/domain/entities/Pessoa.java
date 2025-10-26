package com.example.escola.domain.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pessoas")
@Data
public class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String nomeCompleto;
    private String email;
    private String cpf;
    private String rg;
    private LocalDate dataNascimento;
    private String telefoneContato;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "endereco_id")
    private Endereco endereco;

    @OneToOne(mappedBy = "pessoa", cascade = CascadeType.ALL)
    private User user;

    // Remover as relações bidirecionais que causam ciclos
    // e deixar apenas a navegação unidirecional

    public Set<String> getRoles() {
        Set<String> roles = new HashSet<>();
        // Usar queries ou outra estratégia para determinar roles
        return roles;
    }
}
