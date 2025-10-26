CREATE TABLE pessoas (
    id VARCHAR(255) PRIMARY KEY,
    nome_completo VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    cpf VARCHAR(14) UNIQUE NOT NULL,
    rg VARCHAR(20),
    data_nascimento DATE,
    telefone_contato VARCHAR(20),
    endereco_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_pessoas_endereco
        FOREIGN KEY (endereco_id) REFERENCES enderecos(id)
        ON DELETE SET NULL ON UPDATE CASCADE
);

