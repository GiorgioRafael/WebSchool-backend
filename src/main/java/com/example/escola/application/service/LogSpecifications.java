package com.example.escola.application.service;

import com.example.escola.domain.entities.ActivityLog;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public class LogSpecifications {

    public static Specification<ActivityLog> hasUsername(String username) {
        return (root, query, cb) -> cb.equal(root.get("username"), username);
    }

    public static Specification<ActivityLog> hasAcao(String acao) {
        return (root, query, cb) -> cb.equal(root.get("action"), acao);
    }

    public static Specification<ActivityLog> createdAfter(Instant startTime) {
        if (startTime == null) return null;
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), startTime);
    }
}