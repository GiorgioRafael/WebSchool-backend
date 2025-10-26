package com.example.escola.application.repositories;

import com.example.escola.domain.entities.Turma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TurmaRepository extends JpaRepository<Turma, Long> {
    // Métodos personalizados
    boolean existsByCodigo(String codigo);
}
