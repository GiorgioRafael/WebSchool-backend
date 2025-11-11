package com.example.escola.domain.entities;

import com.example.escola.domain.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Table(name ="users")
@Entity(name ="users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of ="id")
public class User implements UserDetails {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String login;
    private String password;
    private UserRole role;

    @OneToOne
    @JoinColumn(name = "pessoa_id")
    private Pessoa pessoa;

    public User(String login, String password, UserRole role){
        this.login = login;
        this.password = password;
        this.role = role;
    }
    public User(String login, String password, UserRole role, Pessoa pessoa) {
        this.login = login;
        this.password = password;
        this.role = role;
        this.pessoa = pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Garante que o usuário receba uma authority específica do seu papel
        // ex: ROLE_ADMIN, ROLE_SECRETARIA, ROLE_COORDENADOR, etc.
        if (this.role == null) {
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }
        String roleName = this.role.name(); // usa o nome do enum (ADMIN, SECRETARIA, ...)
        return List.of(new SimpleGrantedAuthority("ROLE_" + roleName), new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        return login;
    }

    // Explicitly implement getPassword for UserDetails
    @Override
    public String getPassword() {
        return password;
    }

    // Provide getLogin() used elsewhere in the codebase
    public String getLogin() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
