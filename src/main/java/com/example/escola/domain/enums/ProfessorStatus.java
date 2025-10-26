package com.example.escola.domain.enums;

public enum ProfessorStatus {
    ATIVO("ativo"),
    INATIVO("inativo"),
    FERIAS("ferias");

    private String status;

    ProfessorStatus(String status){
        this.status = status;
    }

    public String getRole(){
        return status;
    }
}
