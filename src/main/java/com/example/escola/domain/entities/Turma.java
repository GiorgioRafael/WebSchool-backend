package com.example.escola.domain.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "turmas")
@Data
public class Turma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String codigo; // Ex: "ING-B1-2025-1" (Inglês Básico 1, 2025, 1º semestre)

    private Integer anoLetivo;
    private Integer semestre;
    private String horario; //Ex: Seg/Qua 19-21h
    private String sala;

    //RELACIONAMENTOS

    //Muitas turmas para UM professor
    @ManyToOne
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;

    //Muitas turmas para MUITOS alunos
    @ManyToMany
    @JoinTable(
            name="turmas_alunos", //nome da tabela de junção
            joinColumns = @JoinColumn(name= "turma_id"),
            inverseJoinColumns = @JoinColumn(name = "aluno_matricula")
    )
    private List<Aluno> alunos;
}
