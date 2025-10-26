CREATE TABLE turmas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) UNIQUE NOT NULL,
    ano_letivo INTEGER NOT NULL,
    semestre INTEGER NOT NULL,
    horario VARCHAR(100),
    sala VARCHAR(50),
    professor_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_turmas_professor
        FOREIGN KEY (professor_id) REFERENCES professores(id)
        ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE turmas_alunos (
    turma_id BIGINT NOT NULL,
    aluno_matricula BIGINT NOT NULL,

    PRIMARY KEY (turma_id, aluno_matricula),

    CONSTRAINT fk_turmas_alunos_turma
        FOREIGN KEY (turma_id) REFERENCES turmas(id)
        ON DELETE CASCADE ON UPDATE CASCADE,

    CONSTRAINT fk_turmas_alunos_aluno
        FOREIGN KEY (aluno_matricula) REFERENCES alunos(matricula)
        ON DELETE CASCADE ON UPDATE CASCADE
);
