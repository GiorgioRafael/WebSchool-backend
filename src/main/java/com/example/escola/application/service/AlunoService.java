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

import com.example.escola.application.repositories.ActivityLogRepository;
import com.example.escola.domain.entities.ActivityLog;
import com.example.escola.util.UpdateUtils;
import com.example.escola.util.SecurityUtils;
import com.example.escola.infrastructure.web.dto.aluno.AlunoDetailDTO;
import com.example.escola.infrastructure.web.dto.endereco.EnderecoDTO;

import java.security.Principal;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class AlunoService {
    private final AlunoRepository repository;
    private final EnderecoService enderecoService;
    private final ResponsavelRepository responsavelRepository;
    private final PessoaService pessoaService;
    private final ActivityLogRepository activityLogRepository;

    @Autowired
    public AlunoService(AlunoRepository repository,
                        EnderecoService enderecoService,
                        ResponsavelRepository responsavelRepository, PessoaService pessoaService,
                        ActivityLogRepository activityLogRepository) {
        this.repository = repository;
        this.enderecoService = enderecoService;
        this.responsavelRepository = responsavelRepository;
        this.pessoaService = pessoaService;
        this.activityLogRepository = activityLogRepository;
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

    @Transactional
    public void updateAluno(Long matricula, AlunoRequestDTO dto, Principal principal) {
        Aluno aluno = repository.findByMatricula(matricula)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        // capture before
        AlunoDetailDTO before = new AlunoDetailDTO(aluno);

        // Update person fields via PessoaService
        if (aluno.getPessoa() != null) {
            PessoaRequestDTO pessoaDTO = new PessoaRequestDTO(
                    dto.nomeCompleto() != null ? dto.nomeCompleto() : aluno.getNomeCompleto(),
                    dto.email() != null ? dto.email() : aluno.getEmail(),
                    dto.cpf() != null ? dto.cpf() : aluno.getCpf(),
                    dto.rg() != null ? dto.rg() : aluno.getRg(),
                    dto.dataNascimento() != null ? dto.dataNascimento() : aluno.getDataNascimento(),
                    dto.telefoneContato() != null ? dto.telefoneContato() : aluno.getTelefoneContato(),
                    (dto.endereco() != null ? dto.endereco() : (aluno.getEndereco() != null ? new EnderecoDTO(aluno.getEndereco()) : null))
            );

            pessoaService.updatePessoa(aluno.getPessoa().getId(), pessoaDTO);
        }

        // Update responsavel if provided
        if (dto.responsavel() != null) {
            Responsavel r = dto.responsavel().toEntity();
            r = responsavelRepository.save(r);
            aluno.setResponsavel(r);
        }

        // any other aluno-specific direct fields can be handled here

        Aluno saved = repository.save(aluno);

        // capture after and build diff
        AlunoDetailDTO after = new AlunoDetailDTO(saved);
        String diff = buildDiff(before, after);

        String username = (principal != null && principal.getName() != null && !principal.getName().isBlank()) ? principal.getName() : SecurityUtils.getCurrentUsername();
        ActivityLog log = new ActivityLog(username, "updateAluno @ PUT /alunos/" + (matricula != null ? matricula.toString() : "-"), diff == null || diff.isBlank() ? "Sem alterações" : diff);
        log.setCreatedAt(Instant.now());
        activityLogRepository.save(log);
    }

    @Transactional
    public void updateAlunoById(String id, AlunoRequestDTO dto, Principal principal) {
        Aluno aluno = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        // capture before
        AlunoDetailDTO before = new AlunoDetailDTO(aluno);

        // Update person fields via PessoaService
        if (aluno.getPessoa() != null) {
            PessoaRequestDTO pessoaDTO = new PessoaRequestDTO(
                    dto.nomeCompleto() != null ? dto.nomeCompleto() : aluno.getNomeCompleto(),
                    dto.email() != null ? dto.email() : aluno.getEmail(),
                    dto.cpf() != null ? dto.cpf() : aluno.getCpf(),
                    dto.rg() != null ? dto.rg() : aluno.getRg(),
                    dto.dataNascimento() != null ? dto.dataNascimento() : aluno.getDataNascimento(),
                    dto.telefoneContato() != null ? dto.telefoneContato() : aluno.getTelefoneContato(),
                    (dto.endereco() != null ? dto.endereco() : (aluno.getEndereco() != null ? new EnderecoDTO(aluno.getEndereco()) : null))
            );

            pessoaService.updatePessoa(aluno.getPessoa().getId(), pessoaDTO);
        }

        // Update responsavel if provided
        if (dto.responsavel() != null) {
            Responsavel r = dto.responsavel().toEntity();
            r = responsavelRepository.save(r);
            aluno.setResponsavel(r);
        }

        Aluno saved = repository.save(aluno);

        // capture after and build diff
        AlunoDetailDTO after = new AlunoDetailDTO(saved);
        String diff = buildDiff(before, after);

        String username = (principal != null && principal.getName() != null && !principal.getName().isBlank()) ? principal.getName() : SecurityUtils.getCurrentUsername();
        ActivityLog log = new ActivityLog(username, "updateAluno @ PUT /alunos/id/" + id, diff == null || diff.isBlank() ? "Sem alterações" : diff);
        log.setCreatedAt(Instant.now());
        activityLogRepository.save(log);
    }

    private String buildDiff(AlunoDetailDTO before, AlunoDetailDTO after) {
        StringBuilder sb = new StringBuilder();

        if (!equalsNullable(before.nomeCompleto(), after.nomeCompleto()))
            sb.append("nomeCompleto: ").append(before.nomeCompleto()).append(" -> ").append(after.nomeCompleto()).append("; ");
        if (!equalsNullable(before.email(), after.email()))
            sb.append("email: ").append(before.email()).append(" -> ").append(after.email()).append("; ");
        if (!equalsNullable(before.cpf(), after.cpf()))
            sb.append("cpf: ").append(before.cpf()).append(" -> ").append(after.cpf()).append("; ");
        if (!equalsNullable(before.rg(), after.rg()))
            sb.append("rg: ").append(before.rg()).append(" -> ").append(after.rg()).append("; ");
        if (!equalsNullable(String.valueOf(before.dataNascimento()), String.valueOf(after.dataNascimento())))
            sb.append("dataNascimento: ").append(before.dataNascimento()).append(" -> ").append(after.dataNascimento()).append("; ");
        if (!equalsNullable(before.telefoneContato(), after.telefoneContato()))
            sb.append("telefoneContato: ").append(before.telefoneContato()).append(" -> ").append(after.telefoneContato()).append("; ");

        String beforeEndereco = before.endereco() != null ? before.endereco().toString() : null;
        String afterEndereco = after.endereco() != null ? after.endereco().toString() : null;
        if (!equalsNullable(beforeEndereco, afterEndereco))
            sb.append("endereco: ").append(beforeEndereco).append(" -> ").append(afterEndereco).append("; ");

        String beforeResp = before.responsavel() != null ? before.responsavel().toString() : null;
        String afterResp = after.responsavel() != null ? after.responsavel().toString() : null;
        if (!equalsNullable(beforeResp, afterResp))
            sb.append("responsavel: ").append(beforeResp).append(" -> ").append(afterResp).append("; ");

        String res = sb.toString().trim();
        if (res.endsWith(";")) res = res.substring(0, res.length() - 1);
        return res;
    }

    private boolean equalsNullable(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }
}
