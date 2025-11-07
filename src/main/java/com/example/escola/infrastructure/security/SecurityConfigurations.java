package com.example.escola.infrastructure.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {

    @Autowired
    private SecurityFilter securityFilter;

    // ADICIONADO: Define os caminhos do Swagger que devem ser públicos
    private static final String[] SWAGGER_PATHS = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                        // ADICIONADO: Libera o acesso aos caminhos do Swagger
                        .requestMatchers(SWAGGER_PATHS).permitAll()
                        //POST ALUNOS
                        .requestMatchers(HttpMethod.POST, "/alunos").hasAnyRole("COORDENADOR", "SECRETARIA", "ADMIN")
                        //rotas debug
                        .requestMatchers(HttpMethod.GET, "/professores/debug/all").hasAnyRole("ADMIN")
                        //PROFESSORES
                        .requestMatchers(HttpMethod.POST, "/professores").hasAnyRole("SECRETARIA", "COORDENADOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/professores").hasAnyRole("SECRETARIA", "COORDENADOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/professores/**").hasAnyRole("SECRETARIA", "COORDENADOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/professores/**").hasAnyRole("SECRETARIA", "COORDENADOR", "ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}