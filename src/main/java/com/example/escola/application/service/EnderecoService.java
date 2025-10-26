package com.example.escola.application.service;

import com.example.escola.infrastructure.web.dto.endereco.EnderecoDTO;
import com.example.escola.domain.entities.Endereco;
import com.example.escola.application.repositories.EnderecoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;

    @Autowired
    public EnderecoService(EnderecoRepository enderecoRepository) {
        this.enderecoRepository = enderecoRepository;
    }

    public Endereco findOrCreateEndereco(EnderecoDTO enderecoDTO) {
        return enderecoRepository.findByCepAndLogradouroAndNumeroAndBairroAndCidadeAndEstado(
                enderecoDTO.cep(),
                enderecoDTO.logradouro(),
                enderecoDTO.numero(),
                enderecoDTO.bairro(),
                enderecoDTO.cidade(),
                enderecoDTO.estado()
        ).orElseGet(() -> {
            Endereco novoEndereco = new Endereco(enderecoDTO);
            return enderecoRepository.save(novoEndereco);
        });
    }

    public Endereco findOrCreateEndereco(Endereco endereco) {
        return enderecoRepository.findByCepAndLogradouroAndNumeroAndBairroAndCidadeAndEstado(
                endereco.getCep(),
                endereco.getLogradouro(),
                endereco.getNumero(),
                endereco.getBairro(),
                endereco.getCidade(),
                endereco.getEstado()
        ).orElseGet(() -> enderecoRepository.save(endereco));
    }
}
