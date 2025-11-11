package com.example.escola.application.service;

import com.example.escola.domain.entities.ActivityLog;
import com.example.escola.application.repositories.ActivityLogRepository;
import com.example.escola.infrastructure.web.dto.log.ActivityLogResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Service
public class ActivityLogService {

    @Autowired
    private ActivityLogRepository logRepository;

    public Page<ActivityLogResponseDTO> findLogs(String username, String timeFilter, String acao, Pageable pageable) {

        // 1. Inicia um Specification "vazio" (não filtra nada)
        Specification<ActivityLog> spec = Specification.where(null);

        // 2. Adiciona os filtros dinamicamente
        if (username != null && !username.isEmpty()) {
            spec = spec.and(LogSpecifications.hasUsername(username));
        }
        if (acao != null && !acao.isEmpty()) {
            spec = spec.and(LogSpecifications.hasAcao(acao));
        }
        if (timeFilter != null && !timeFilter.equals("all")) {
            Instant start = getStartTime(timeFilter);
            if (start != null) spec = spec.and(LogSpecifications.createdAfter(start));
        }

        // 3. Executa a query filtrada e paginada
        Page<ActivityLog> logPage = logRepository.findAll(spec, pageable);

        // 4. Converte a página de Entidades para uma página de DTOs
        return logPage.map(ActivityLogResponseDTO::new);
    }

    private Instant getStartTime(String timeFilter) {
        Instant now = Instant.now();
        return switch (timeFilter) {
            case "24h" -> now.minus(24, ChronoUnit.HOURS);
            case "7d" -> now.minus(7, ChronoUnit.DAYS);
            case "30d" -> now.minus(30, ChronoUnit.DAYS);
            default -> null; // or Instant.EPOCH for "all"
        };
    }

    // New: fetch most recent logs (default use size=20 at controller)
    public List<ActivityLogResponseDTO> findRecent(int size) {
        int pageSize = Math.max(1, size);
        return logRepository
                .findAll(PageRequest.of(0, pageSize, Sort.by(Sort.Direction.DESC, "createdAt")))
                .map(ActivityLogResponseDTO::new)
                .getContent();
    }
}