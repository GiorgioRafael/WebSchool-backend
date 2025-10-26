package com.example.escola.infrastructure.web.dto.user;

import com.example.escola.domain.entities.Pessoa;
import com.example.escola.domain.enums.UserRole;

public record UserRequestDTO (
        String login,
        String passowrd,
        UserRole role,

        //DTO ANINHADO
        Pessoa pessoa
) {

}
