package com.example.escola.domain.entities;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="jogos")
@Data
public class Jogo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nome;
    private String descricao;
}
