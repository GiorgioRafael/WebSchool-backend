package com.example.escola.application.service;

import com.example.escola.infrastructure.web.dto.pessoa.PessoaRequestDTO;
import com.example.escola.infrastructure.web.dto.pessoa.PessoaResponseDTO;
import com.example.escola.domain.entities.Endereco;
import com.example.escola.domain.entities.Pessoa;
import com.example.escola.application.repositories.PessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PessoaService {

    private final PessoaRepository repository;
    private final EnderecoService enderecoService;

    @Autowired
    public PessoaService(PessoaRepository repository, EnderecoService enderecoService) {
        this.repository = repository;
        this.enderecoService = enderecoService;
    }

    public List<PessoaResponseDTO> getAllPessoas() {
        return repository.findAll().stream()
                .map(PessoaResponseDTO::new)
                .collect(Collectors.toList());
    }

    public PessoaResponseDTO getPessoaById(String id) {
        Pessoa pessoa = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pessoa não encontrada"));
        return new PessoaResponseDTO(pessoa);
    }

    @Transactional
    public PessoaResponseDTO createPessoa(PessoaRequestDTO data) {
        Pessoa pessoa = new Pessoa();
        updatePessoaFromDTO(pessoa, data);

        pessoa = repository.save(pessoa);
        return new PessoaResponseDTO(pessoa);
    }

    @Transactional
    public PessoaResponseDTO updatePessoa(String id, PessoaRequestDTO data) {
        Pessoa pessoa = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pessoa não encontrada"));

        updatePessoaFromDTO(pessoa, data);
        pessoa = repository.save(pessoa);
        return new PessoaResponseDTO(pessoa);
    }

    public void deletePessoa(String id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Pessoa não encontrada");
        }
        repository.deleteById(id);
    }

    // Método auxiliar para atualizar os dados de uma pessoa
    private void updatePessoaFromDTO(Pessoa pessoa, PessoaRequestDTO data) {
        pessoa.setNomeCompleto(data.nomeCompleto());
        pessoa.setEmail(data.email());
        pessoa.setCpf(data.cpf());
        pessoa.setRg(data.rg());
        pessoa.setDataNascimento(data.dataNascimento());
        pessoa.setTelefoneContato(data.telefoneContato());

        // Reutiliza o EnderecoService para manter a funcionalidade de compartilhamento de endereços
        if (data.endereco() != null) {
            Endereco endereco = enderecoService.findOrCreateEndereco(data.endereco());
            pessoa.setEndereco(endereco);
        }
    }

    // Método para criar uma pessoa a partir de um DTO e retornar a entidade
    // Útil para ser chamado de outros serviços (Aluno, Professor, etc.)
    @Transactional
    public Pessoa createAndGetPessoa(PessoaRequestDTO data) {
        Pessoa pessoa = new Pessoa();
        updatePessoaFromDTO(pessoa, data);
        return repository.save(pessoa);
    }
}
