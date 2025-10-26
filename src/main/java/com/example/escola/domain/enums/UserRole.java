package com.example.escola.domain.enums;

public enum UserRole {
    ADMIN("admin"),
    COORDENADOR("coordenador"),
    PROFESSOR("professor"),
    SECRETARIA("secretaria"),
    ALUNO("aluno"),
    RESPONSAVEL("responsavel");

    private String role;

    UserRole(String role){
        this.role = role;
    }

    public String getRole(){
        return role;
    }
}
