package com.example.escola.application.service;

import com.example.escola.infrastructure.web.dto.pessoa.PessoaRequestDTO;
import com.example.escola.infrastructure.web.dto.professor.ProfessorDetailDTO;
import com.example.escola.infrastructure.web.dto.professor.ProfessorRequestDTO;
import com.example.escola.infrastructure.web.dto.professor.ProfessorResponseDTO;
import com.example.escola.domain.entities.Pessoa;
import com.example.escola.domain.entities.Professor;
import com.example.escola.application.repositories.ProfessorRepository;
import com.example.escola.domain.enums.ProfessorStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ProfessorService {
    private final ProfessorRepository professorRepository;
    private final PessoaService pessoaService;

    @Autowired
    public ProfessorService(ProfessorRepository professorRepository,
                            PessoaService pessoaService) {
        this.professorRepository = professorRepository;
        this.pessoaService = pessoaService;
    }

    @Transactional
    public ProfessorResponseDTO createProfessor(ProfessorRequestDTO dto) {
        // Criar a pessoa através do PessoaService
        PessoaRequestDTO pessoaDTO = new PessoaRequestDTO(
                dto.nomeCompleto(),
                dto.email(),
                dto.cpf(),
                dto.rg(),
                dto.dataNascimento(),
                dto.telefoneContato(),
                dto.endereco()
        );

        Pessoa pessoa = pessoaService.createAndGetPessoa(pessoaDTO);

        // Criar o professor associado à pessoa
        Professor professor = new Professor();
        professor.setPessoa(pessoa);
        professor.setDataContratacao(dto.dataContratacao());
        // Campos formacaoAcademica e biografia não estão no DTO
        professor.setFormacaoAcademica(null);
        professor.setBiografia(null);

        // Gerar registro funcional no formato PROF-AACCC (AA=ano atual, CCC=contador)
        String registroFuncional = gerarRegistroFuncional();
        professor.setRegistroFuncional(registroFuncional);

        // Definir status como ATIVO por padrão
        professor.setProfessorStatus(ProfessorStatus.ATIVO);

        Professor saved = professorRepository.save(professor);
        return new ProfessorResponseDTO(saved);
    }

    public List<ProfessorResponseDTO> getAllProfessors() {
        return professorRepository.findAll().stream()
                .map(ProfessorResponseDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProfessorDetailDTO> getAllProfessorsDetailedData() {
        return professorRepository.findAll().stream()
                .map(ProfessorDetailDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProfessorDetailDTO getProfessorDetailedDataById(String id) {
        Professor professor = professorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));
        return new ProfessorDetailDTO(professor);
    }

    public ProfessorResponseDTO getProfessorById(String id) {
        Optional<Professor> optional = professorRepository.findById(id);
        if (optional.isEmpty()) {
            throw new RuntimeException("Professor não encontrado");
        }
        return new ProfessorResponseDTO(optional.get());
    }

    private String gerarRegistroFuncional() {
        // Obtém o ano atual (últimos 2 dígitos)
        int anoAtual = LocalDate.now().getYear() % 100;

        // Prefixo do registro
        String prefixo = "PROF-" + anoAtual;

        // Busca todos os professores e filtra os que têm registro começando com o prefixo
        List<Professor> professoresAnoAtual = professorRepository.findAll().stream()
                .filter(p -> p.getRegistroFuncional() != null &&
                        p.getRegistroFuncional().startsWith(prefixo))
                .toList();

        // Determina o próximo número sequencial
        int contador = 1;
        if (!professoresAnoAtual.isEmpty()) {
            // Extrai o maior contador atual
            contador = professoresAnoAtual.stream()
                    .map(p -> {
                        try {
                            return Integer.parseInt(p.getRegistroFuncional().substring(prefixo.length()));
                        } catch (NumberFormatException e) {
                            return 0;
                        }
                    })
                    .max(Integer::compare)
                    .orElse(0) + 1;
        }

        return prefixo + String.format("%03d", contador);
    }

    @Transactional
    public ProfessorResponseDTO updateProfessor(String id, ProfessorRequestDTO dto) {
        Professor professor = professorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        // Usar o PessoaService para atualizar os dados da pessoa
        if (professor.getPessoa() != null) {
            PessoaRequestDTO pessoaDTO = new PessoaRequestDTO(
                    dto.nomeCompleto(),
                    dto.email(),
                    dto.cpf(),
                    professor.getRg(), // Mantém o RG existente
                    professor.getDataNascimento(), // Mantém a data de nascimento existente
                    dto.telefoneContato(),
                    dto.endereco()
            );

            pessoaService.updatePessoa(professor.getPessoa().getId(), pessoaDTO);
        }

        // Atualiza os campos específicos do professor
        if (dto.dataContratacao() != null) {
            professor.setDataContratacao(dto.dataContratacao());
        }

        Professor saved = professorRepository.save(professor);
        return new ProfessorResponseDTO(saved);
    }

    public void deleteProfessor(String id) {
        professorRepository.deleteById(id);
    }
}