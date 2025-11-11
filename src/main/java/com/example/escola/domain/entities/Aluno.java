package com.example.escola.domain.entities;

import com.example.escola.infrastructure.web.dto.aluno.AlunoRequestDTO;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "alunos")
@Data
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name= "pessoa_id")
    private Pessoa pessoa;

    @Column(unique = true, nullable = false)
    private Long matricula;

    @ManyToOne
    @JoinColumn(name = "responsavel_id", nullable = true)
    private Responsavel responsavel;

    @ManyToMany(mappedBy = "alunos")
    private List<Turma> turmas;

    // timestamps
    private Instant createdAt = Instant.now();
    private Instant updatedAt;

    public Aluno(){
        this.pessoa = new Pessoa();
    }

    public Aluno(AlunoRequestDTO data) {
        this();
        // Mapeia os dados para a pessoa
        this.pessoa.setNomeCompleto(data.nomeCompleto());
        this.pessoa.setEmail(data.email());
        this.pessoa.setCpf(data.cpf());
        this.pessoa.setRg(data.rg());
        this.pessoa.setDataNascimento(data.dataNascimento());
        this.pessoa.setTelefoneContato(data.telefoneContato());

        // Cria e associa a nova entidade Endereco à pessoa
        if (data.endereco() != null) {
            this.pessoa.setEndereco(new Endereco(data.endereco()));
        }
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
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
}
