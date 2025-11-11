package com.example.escola.infrastructure.web.dto.log;

import com.example.escola.domain.entities.ActivityLog;

import java.time.Instant;

public record ActivityLogResponseDTO(
        Long id,
        String username,
        String action,
        String details,
        Instant createdAt
) {
    public ActivityLogResponseDTO(ActivityLog log) {
        this(
                log.getId(),
                log.getUsername(),
                log.getAction(),
                log.getDetails(),
                log.getCreatedAt()
        );
    }
}

