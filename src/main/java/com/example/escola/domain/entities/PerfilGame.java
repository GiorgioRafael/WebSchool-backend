package com.example.escola.domain.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name="perfis_game")
@Data
public class PerfilGame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name= "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Long pontuacaoTotal = 0L;

    @Column(nullable = false)
    private Integer nivel = 1;

    @Column(nullable = false)
    private Integer sequenciaDiaria = 0;

    private LocalDate dateUltimaSequencia;
}
