package com.example.escola.application.service;

import com.example.escola.infrastructure.web.dto.pessoa.PessoaRequestDTO;
import com.example.escola.domain.entities.Aluno;
import com.example.escola.domain.entities.Endereco;
import com.example.escola.domain.entities.Pessoa;
import com.example.escola.domain.entities.Responsavel;
import com.example.escola.application.repositories.AlunoRepository;
import com.example.escola.application.repositories.ResponsavelRepository;
import com.example.escola.infrastructure.web.dto.aluno.AlunoRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Service
public class AlunoService {
    private final AlunoRepository repository;
    private final EnderecoService enderecoService;
    private final ResponsavelRepository responsavelRepository;
    private final PessoaService pessoaService;

    @Autowired
    public AlunoService(AlunoRepository repository,
                        EnderecoService enderecoService,
                        ResponsavelRepository responsavelRepository, PessoaService pessoaService) {
        this.repository = repository;
        this.enderecoService = enderecoService;
        this.responsavelRepository = responsavelRepository;
        this.pessoaService = pessoaService;
    }

    private Aluno convertToEntity(AlunoRequestDTO dto) {
        Aluno aluno = new Aluno(dto);
        aluno.setNomeCompleto(dto.nomeCompleto());
        aluno.setEmail(dto.email());
        aluno.setTelefoneContato(dto.telefoneContato());
        aluno.setCpf(dto.cpf());
        aluno.setRg(dto.rg());
        aluno.setDataNascimento(dto.dataNascimento());

        // Busca ou cria o responsável
        if (dto.responsavel() != null) {
            Responsavel responsavel = dto.responsavel().toEntity();
            responsavel = responsavelRepository.save(responsavel); // Salva o responsável antes de associar
            aluno.setResponsavel(responsavel);
        }
        return aluno;
    }

    @Transactional
    public void matricularNovoAluno(AlunoRequestDTO dados) {
        Long matriculaUnica = gerarMatriculaUnica();

        // Criar a pessoa através do PessoaService
        PessoaRequestDTO pessoaDTO = new PessoaRequestDTO(
                dados.nomeCompleto(),
                dados.email(),
                dados.cpf(),
                dados.rg(),
                dados.dataNascimento(),
                dados.telefoneContato(),
                dados.endereco()
        );

        Pessoa pessoa = pessoaService.createAndGetPessoa(pessoaDTO);

        // Criar o aluno associado à pessoa
        Aluno novoAluno = new Aluno();
        novoAluno.setPessoa(pessoa);
        novoAluno.setMatricula(matriculaUnica);

        // Busca ou cria o responsável
        if (dados.responsavel() != null) {
            Responsavel responsavel = dados.responsavel().toEntity();
            responsavel = responsavelRepository.save(responsavel);
            novoAluno.setResponsavel(responsavel);
        }

        repository.save(novoAluno);
    }

    @Transactional
    public void matricularNovosAlunos(List<AlunoRequestDTO> dtoList) {
        List<Aluno> novosAlunos = new ArrayList<>();

        // 1. Percorre a lista de DTOs recebida
        for (AlunoRequestDTO dados : dtoList) {
            Long matriculaUnica = gerarMatriculaUnica();

            // Usar o método de conversão
            Aluno novoAluno = convertToEntity(dados);
            novoAluno.setMatricula(matriculaUnica);

            // Verifica se já existe um endereço idêntico no banco
            if (dados.endereco() != null) {
                Endereco endereco = enderecoService.findOrCreateEndereco(dados.endereco());
                novoAluno.setEndereco(endereco);
            }

            novosAlunos.add(novoAluno);
        }

        // 2. Salva TODOS os novos alunos no banco de uma só vez
        repository.saveAll(novosAlunos);
    }

    private Long gerarMatriculaUnica() {
        Long matricula;
        do {
            matricula = (long) (100000 + new SecureRandom().nextInt(900000));
        } while (repository.findByMatricula(matricula).isPresent());

        return matricula;
    }
}
