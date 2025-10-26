package com.example.escola.application.repositories;

import com.example.escola.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository  extends JpaRepository<User, String> {
    User findByLogin(String login);
}
