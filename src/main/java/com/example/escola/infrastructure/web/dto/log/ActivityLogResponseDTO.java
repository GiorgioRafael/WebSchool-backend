package com.example.escola.infrastructure.web.dto.log;

import com.example.escola.domain.entities.ActivityLog;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public record ActivityLogResponseDTO(
        Long id,
        String username,
        String action,
        String details,
        Instant createdAt,
        String createdAtFormatted
) {
    private static final ZoneId BRASILIA = ZoneId.of("America/Sao_Paulo");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH:mm:ss.SSSXXX");
    public ActivityLogResponseDTO(ActivityLog log) {
        this(
                log.getId(),
                log.getUsername(),
                log.getAction(),
                log.getDetails(),
                log.getCreatedAt(),
                formatInstant(log.getCreatedAt())
        );
    }
    private static String formatInstant(Instant instant) {
        if(instant == null) return null;
        return FORMATTER.format(instant.atZone(BRASILIA));
    }
}

