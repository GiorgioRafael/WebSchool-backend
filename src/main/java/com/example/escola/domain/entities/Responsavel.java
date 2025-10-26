package com.example.escola.domain.entities;

import jakarta.persistence.*; // Importe as anotações da JPA
import lombok.*;

import java.util.List;

@Entity // <<-- Define que esta classe é uma entidade do banco de dados
@Table(name = "responsaveis") // <<-- Define o nome da tabela
@Data // <<-- Combina @Getter, @Setter, @ToString, etc.
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class Responsavel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String nome;
    private String cpf;
    private String email;
    private String telefoneContato;
    private String parentesco;

    @OneToMany(mappedBy = "responsavel")
    private List<Aluno> alunos;
}