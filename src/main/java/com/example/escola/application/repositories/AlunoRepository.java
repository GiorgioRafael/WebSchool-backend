package com.example.escola.application.repositories;

import com.example.escola.domain.entities.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Interface Repository para a entidade Aluno.
 * Fornece todos os métodos de acesso a dados (CRUD) para Alunos.
 */
@Repository
public interface AlunoRepository extends JpaRepository<Aluno, String> {
    Optional<Aluno> findByMatricula(Long matricula);
}
