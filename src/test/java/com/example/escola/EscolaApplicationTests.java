package com.example.escola;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest // Deixe apenas esta anotação
class EscolaApplicationTests {

    @Test
    void contextLoads() {
        // Este teste agora vai carregar o contexto completo 
        // usando o banco de dados H2.
    }
}