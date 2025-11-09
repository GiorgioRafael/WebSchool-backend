package com.example.escola.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ProfessorStatus {
    ATIVO("ativo"),
    INATIVO("inativo"),
    FERIAS("ferias");

    private final String status;

    ProfessorStatus(String status){
        this.status = status;
    }

    public String getRole(){
        return status;
    }

    @JsonCreator
    public static ProfessorStatus from(String value) {
        if (value == null) return null;
        try {
            return ProfessorStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            // Optionally, try matching by the 'status' field
            for (ProfessorStatus ps : ProfessorStatus.values()) {
                if (ps.status.equalsIgnoreCase(value.trim())) return ps;
            }
            throw e;
        }
    }
}
