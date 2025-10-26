CREATE TABLE alunos (
    matricula BIGSERIAL PRIMARY KEY,
    nome_completo VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    telefone_contato VARCHAR(20),
    cpf VARCHAR(20),
    rg VARCHAR(20),
    data_nascimento DATE,
    responsavel_id BIGINT,
    CONSTRAINT fk_responsavel FOREIGN KEY (responsavel_id) REFERENCES responsaveis(id)
);
