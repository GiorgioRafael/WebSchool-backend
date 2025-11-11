package com.example.escola.application.service;

import com.example.escola.infrastructure.web.dto.pessoa.PessoaRequestDTO;
import com.example.escola.infrastructure.web.dto.professor.ProfessorDetailDTO;
import com.example.escola.infrastructure.web.dto.professor.ProfessorRequestDTO;
import com.example.escola.infrastructure.web.dto.professor.ProfessorResponseDTO;
import com.example.escola.domain.entities.Pessoa;
import com.example.escola.domain.entities.Professor;
import com.example.escola.application.repositories.ProfessorRepository;
import com.example.escola.domain.enums.ProfessorStatus;
import com.example.escola.infrastructure.web.dto.endereco.EnderecoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.example.escola.application.repositories.ActivityLogRepository;
import com.example.escola.domain.entities.ActivityLog;
import com.example.escola.util.SecurityUtils;

@Service
public class ProfessorService {
    private final ProfessorRepository professorRepository;
    private final PessoaService pessoaService;
    private final ActivityLogRepository activityLogRepository;

    @Autowired
    public ProfessorService(ProfessorRepository professorRepository,
                            PessoaService pessoaService,
                            ActivityLogRepository activityLogRepository) {
        this.professorRepository = professorRepository;
        this.pessoaService = pessoaService;
        this.activityLogRepository = activityLogRepository;
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
    public ProfessorResponseDTO updateProfessor(String id, ProfessorRequestDTO dto, String username) {
        Professor professor = professorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        // capture before state
        ProfessorDetailDTO before = new ProfessorDetailDTO(professor);

        // Usar o PessoaService para atualizar os dados da pessoa
        if (professor.getPessoa() != null) {
            PessoaRequestDTO pessoaDTO = new PessoaRequestDTO(
                    dto.nomeCompleto() != null ? dto.nomeCompleto() : professor.getNomeCompleto(),
                    dto.email() != null ? dto.email() : professor.getEmail(),
                    dto.cpf() != null ? dto.cpf() : professor.getCpf(),
                    // Mantém RG e data de nascimento a partir da entidade se DTO não fornecer
                    (dto.rg() != null ? dto.rg() : professor.getRg()),
                    (dto.dataNascimento() != null ? dto.dataNascimento() : professor.getDataNascimento()),
                    dto.telefoneContato() != null ? dto.telefoneContato() : professor.getTelefoneContato(),
                    // converter entidade Endereco -> EnderecoDTO quando necessário
                    (dto.endereco() != null ? dto.endereco() : (professor.getEndereco() != null ? new EnderecoDTO(professor.getEndereco()) : null))
            );

            pessoaService.updatePessoa(professor.getPessoa().getId(), pessoaDTO);
        }

        // Atualiza os campos específicos do professor
        if (dto.dataContratacao() != null) {
            professor.setDataContratacao(dto.dataContratacao());
        }

        // Atualiza o status do professor se fornecido no DTO
        if (dto.professorStatus() != null) {
            professor.setProfessorStatus(dto.professorStatus());
        }

        Professor saved = professorRepository.save(professor);

        // capture after state and compute diff
        ProfessorDetailDTO after = new ProfessorDetailDTO(saved);
        String diff = buildDiff(before, after);

        // create activity log with diff
        String uname = (username != null && !username.isBlank()) ? username : SecurityUtils.getCurrentUsername();
        String action = "updateProfessor @ PUT /professores/" + id;
        ActivityLog log = new ActivityLog(uname, action, diff == null || diff.isBlank() ? "Sem alterações" : diff);
        activityLogRepository.save(log);

        return new ProfessorResponseDTO(saved);
    }

    public void deleteProfessor(String id) {
        professorRepository.deleteById(id);
    }

    private String buildDiff(ProfessorDetailDTO before, ProfessorDetailDTO after) {
        StringBuilder sb = new StringBuilder();

        // compare person fields
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

        // endereco: compare as string
        String beforeEndereco = before.endereco() != null ? before.endereco().toString() : null;
        String afterEndereco = after.endereco() != null ? after.endereco().toString() : null;
        if (!equalsNullable(beforeEndereco, afterEndereco))
            sb.append("endereco: ").append(beforeEndereco).append(" -> ").append(afterEndereco).append("; ");

        // professor-specific fields
        if (!equalsNullable(String.valueOf(before.dataContratacao()), String.valueOf(after.dataContratacao())))
            sb.append("dataContratacao: ").append(before.dataContratacao()).append(" -> ").append(after.dataContratacao()).append("; ");
        if (!equalsNullable(String.valueOf(before.professorStatus()), String.valueOf(after.professorStatus())))
            sb.append("professorStatus: ").append(before.professorStatus()).append(" -> ").append(after.professorStatus()).append("; ");

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
