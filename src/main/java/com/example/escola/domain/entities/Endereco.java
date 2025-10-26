package com.example.escola.domain.entities;

import com.example.escola.infrastructure.web.dto.endereco.EnderecoDTO;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "enderecos")
@Data
@NoArgsConstructor
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // Campos do endereço
    private String cep;
    private String logradouro;
    private String bairro;
    private String numero;
    private String cidade;
    private String estado;

    @OneToMany(mappedBy = "endereco")
    private Set<Pessoa> pessoas = new HashSet<>();

    public Endereco(String cep, String logradouro, String numero, String bairro, String cidade, String estado) {
        this.cep = cep;
        this.logradouro = logradouro;
        this.numero = numero;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
    }

    public Endereco(EnderecoDTO data) {
        this.setCep(data.cep());
        this.setLogradouro(data.logradouro());
        this.setNumero(data.numero());
        this.setBairro(data.bairro());
        this.setCidade(data.cidade());
        this.setEstado(data.estado());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Endereco endereco = (Endereco) o;

        if (!cep.equals(endereco.cep)) return false;
        if (!logradouro.equals(endereco.logradouro)) return false;
        if (!numero.equals(endereco.numero)) return false;
        if (!bairro.equals(endereco.bairro)) return false;
        if (!cidade.equals(endereco.cidade)) return false;
        return estado.equals(endereco.estado);
    }

    @Override
    public int hashCode() {
        int result = cep.hashCode();
        result = 31 * result + logradouro.hashCode();
        result = 31 * result + numero.hashCode();
        result = 31 * result + bairro.hashCode();
        result = 31 * result + cidade.hashCode();
        result = 31 * result + estado.hashCode();
        return result;
    }
}
