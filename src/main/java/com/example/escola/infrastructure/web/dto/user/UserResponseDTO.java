package com.example.escola.infrastructure.web.dto.user;

import com.example.escola.domain.entities.Pessoa;
import com.example.escola.domain.enums.UserRole;

public record UserResponseDTO(
        String login,
        String password,
        UserRole role,

        //DTO Aninhado
        Pessoa pessoa
) {
}
