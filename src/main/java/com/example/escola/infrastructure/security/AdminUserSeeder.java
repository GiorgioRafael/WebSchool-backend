package com.example.escola.infrastructure.security;

import com.example.escola.application.repositories.UserRepository;
import com.example.escola.domain.entities.User;
import com.example.escola.domain.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Esta classe é executada uma vez na inicialização da aplicação
 * para garantir que o usuário ADMIN exista no banco de dados.
 */
@Component
public class AdminUserSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Puxa os valores das variáveis de ambiente
    // Se a variável não existir, usa o valor padrão (ex: "admin")
    @Value("${ADMIN_DEFAULT_LOGIN:admin}")
    private String adminLogin;

    @Value("${ADMIN_DEFAULT_PASSWORD:admin123}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        // 1. VERIFICA SE O ADMIN JÁ EXISTE
        if (userRepository.findByLogin(adminLogin) == null) {

            System.out.println("--- ADMIN SEEDER: Usuário admin padrão não encontrado. Criando... ---");

            // 2. SE NÃO EXISTIR, CRIA O NOVO ADMIN
            User adminUser = new User(
                    adminLogin,
                    passwordEncoder.encode(adminPassword), // Criptografa a senha
                    UserRole.ADMIN
            );

            // 3. SALVA NO BANCO
            userRepository.save(adminUser);

            System.out.println("--- ADMIN SEEDER: Usuário admin padrão criado com sucesso. ---");
        } else {
            System.out.println("--- ADMIN SEEDER: Usuário admin já existe. Nenhuma ação necessária. ---");
        }
    }
}