package com.example.escola.infrastructure.web.controllers;

import com.example.escola.infrastructure.web.dto.user.UserResponseDTO;
import com.example.escola.application.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{login}")
    public ResponseEntity<UserResponseDTO> getUserByLogin(@PathVariable String login) {
        var user = userRepository.findByLogin(login);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        var response = new UserResponseDTO(
                user.getUsername(), // ou getLogin()
                "", // nunca retorne a senha!
                user.getRole(), // supondo que getRole() retorna UserRole
                user.getPessoa() // supondo que getPessoa() retorna Pessoa
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        //busca todos os usuarios
        var users = userRepository.findAll();

        //converte cada usuario para dto
        List<UserResponseDTO> responseDTOList = users.stream()
                .map(user -> new UserResponseDTO(
                        user.getUsername(),
                        "",
                        user.getRole(),
                        user.getPessoa()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDTOList);
    }
}
