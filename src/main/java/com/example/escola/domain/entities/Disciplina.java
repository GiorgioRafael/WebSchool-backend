package com.example.escola.domain.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "disciplinas")
@Data
public class Disciplina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nome;

    private Integer cargaHoraria;

    @ManyToMany(mappedBy = "disciplinas") //disciplinas e o nome do campo na entity do professor
    private List<Professor> professores;
}
