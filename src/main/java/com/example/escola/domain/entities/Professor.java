package com.example.escola.domain.entities;

import com.example.escola.domain.enums.ProfessorStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "professores")
@Data
public class Professor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "pessoa_id")
    private Pessoa pessoa;

    private LocalDate dataContratacao;
    private ProfessorStatus professorStatus;
    private String registroFuncional;

    private String formacaoAcademica;
    private String biografia;

    @OneToMany(mappedBy = "professor")
    private List<Turma> turmas;

    @ManyToMany
    @JoinTable(
            name= "professores_disciplinas",
            joinColumns = @JoinColumn(name = "professor_id"),
            inverseJoinColumns = @JoinColumn(name = "disciplina_id")
    )
    private List<Disciplina> disciplinas;

    public Professor() {
        this.pessoa = new Pessoa();
    }

    // Métodos de conveniência
    public String getNomeCompleto() {
        return pessoa != null ? pessoa.getNomeCompleto() : null;
    }

    public void setNomeCompleto(String nomeCompleto) {
        if (pessoa == null) pessoa = new Pessoa();
        pessoa.setNomeCompleto(nomeCompleto);
    }

    public String getEmail() {
        return pessoa != null ? pessoa.getEmail() : null;
    }

    public void setEmail(String email) {
        if (pessoa == null) pessoa = new Pessoa();
        pessoa.setEmail(email);
    }

    public String getCpf() {
        return pessoa != null ? pessoa.getCpf() : null;
    }

    public void setCpf(String cpf) {
        if (pessoa == null) pessoa = new Pessoa();
        pessoa.setCpf(cpf);
    }

    public String getRg() {
        return pessoa != null ? pessoa.getRg() : null;
    }

    public void setRg(String rg) {
        if (pessoa == null) pessoa = new Pessoa();
        pessoa.setRg(rg);
    }

    public LocalDate getDataNascimento() {
        return pessoa != null ? pessoa.getDataNascimento() : null;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        if (pessoa == null) pessoa = new Pessoa();
        pessoa.setDataNascimento(dataNascimento);
    }

    public String getTelefoneContato() {
        return pessoa != null ? pessoa.getTelefoneContato() : null;
    }

    public void setTelefoneContato(String telefoneContato) {
        if (pessoa == null) pessoa = new Pessoa();
        pessoa.setTelefoneContato(telefoneContato);
    }

    public Endereco getEndereco() {
        return pessoa != null ? pessoa.getEndereco() : null;
    }

    public void setEndereco(Endereco endereco) {
        if (pessoa == null) pessoa = new Pessoa();
        pessoa.setEndereco(endereco);
    }

    public ProfessorStatus getProfessorStatus() {
        return professorStatus;
    }
}
