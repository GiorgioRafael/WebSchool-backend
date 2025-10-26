CREATE TABLE professores (
    id VARCHAR(255) PRIMARY KEY,
    pessoa_id VARCHAR(255) NOT NULL,
    data_contratacao DATE NOT NULL,
    professor_status ENUM('ATIVO', 'INATIVO', 'LICENCA') DEFAULT 'ATIVO',
    registro_funcional VARCHAR(20) UNIQUE NOT NULL,
    formacao_academica TEXT,
    biografia TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_professores_pessoa
        FOREIGN KEY (pessoa_id) REFERENCES pessoas(id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

