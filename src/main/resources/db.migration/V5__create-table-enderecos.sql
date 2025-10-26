CREATE TABLE enderecos (
    id BIGSERIAL PRIMARY KEY,
    cep VARCHAR(10) NOT NULL,
    logradouro VARCHAR(255) NOT NULL,
    numero VARCHAR(10),
    bairro VARCHAR(100),
    cidade VARCHAR(100) NOT NULL,
    estado VARCHAR(2) NOT NULL,
    aluno_matricula BIGINT UNIQUE,
    CONSTRAINT fk_aluno FOREIGN KEY (aluno_matricula) REFERENCES alunos(matricula)
);

