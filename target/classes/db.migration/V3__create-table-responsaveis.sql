CREATE TABLE responsaveis (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    cpf VARCHAR(20) NOT NULL UNIQUE,
    telefone_contato VARCHAR(20)
);

