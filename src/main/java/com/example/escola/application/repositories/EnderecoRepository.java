package com.example.escola.application.repositories;

import com.example.escola.domain.entities.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, String> {
    Optional<Endereco> findByCepAndLogradouroAndNumeroAndBairroAndCidadeAndEstado(
            String cep, String logradouro, String numero, String bairro, String cidade, String estado);
}
