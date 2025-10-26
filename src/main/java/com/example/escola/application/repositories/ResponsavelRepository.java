package com.example.escola.application.repositories;

import com.example.escola.domain.entities.Responsavel;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interface Repository para a entidade Responsavel.
 * Fornece todos os métodos de acesso a dados (CRUD) para Responsáveis.
 */
public interface ResponsavelRepository extends JpaRepository<Responsavel, Long> {
    Responsavel findByCpf(String cpf);
}
