CREATE TABLE disciplinas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    codigo VARCHAR(20) UNIQUE NOT NULL,
    carga_horaria INTEGER NOT NULL,
    descricao TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE professores_disciplinas (
    professor_id VARCHAR(255) NOT NULL,
    disciplina_id BIGINT NOT NULL,

    PRIMARY KEY (professor_id, disciplina_id),

    CONSTRAINT fk_professores_disciplinas_professor
        FOREIGN KEY (professor_id) REFERENCES professores(id)
        ON DELETE CASCADE ON UPDATE CASCADE,

    CONSTRAINT fk_professores_disciplinas_disciplina
        FOREIGN KEY (disciplina_id) REFERENCES disciplinas(id)
        ON DELETE CASCADE ON UPDATE CASCADE
);
