package com.example.escola.infrastructure.web.dto.user;

import com.example.escola.domain.enums.UserRole;

public record RegisterDTO(String login, String password, UserRole role) {
}
