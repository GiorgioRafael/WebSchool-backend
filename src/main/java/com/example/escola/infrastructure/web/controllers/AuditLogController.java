package com.example.escola.infrastructure.web.controllers;

import com.example.escola.application.service.ActivityLogService;
import com.example.escola.infrastructure.web.dto.log.ActivityLogResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/logs")
public class AuditLogController {

    @Autowired
    private ActivityLogService logService;

    @GetMapping
    public ResponseEntity<Page<ActivityLogResponseDTO>> getLogs(
            // Filtros que você pediu:
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String timeFilter, // "24h", "7d", "30d", "all"
            // Filtros que eu sugeri:
            @RequestParam(required = false) String acao,
            // Paginação (ex: ?page=0&size=20&sort=timestamp,desc)
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<ActivityLogResponseDTO> logPage = logService.findLogs(username, timeFilter, acao, pageable);
        return ResponseEntity.ok(logPage);
    }

    // Novo endpoint: logs recentes
    @GetMapping("/recent")
    public ResponseEntity<List<ActivityLogResponseDTO>> getRecentLogs(@RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(logService.findRecent(size));
    }
}