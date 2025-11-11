package com.example.escola.infrastructure.config.aop;

import com.example.escola.application.repositories.ActivityLogRepository;
import com.example.escola.domain.entities.ActivityLog;
import com.example.escola.domain.entities.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class AuditLogAspect {

    @Autowired
    private ActivityLogRepository logRepository;

    @Autowired
    private ObjectMapper objectMapper; // Para converter objetos em JSON

    @Pointcut("within(com.example.escola.infrastructure.web.controllers..*) && " +
            "(@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping))")
    public void allWriteOperations() {}

    @AfterReturning(pointcut = "allWriteOperations()")
    public void logActivity(JoinPoint joinPoint) {
        String username = getUsername();
        String action = joinPoint.getSignature().getName();
        String path = getRequestPath();
        String details = getMethodArgs(joinPoint);

        ActivityLog log = new ActivityLog(username, action + " @ " + path, details);
        logRepository.save(log);
    }

    private String getUsername() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof User) {
                return ((User) principal).getLogin();
            } else if (principal instanceof String) {
                return (String) principal;
            }
        } catch (Exception e) {
            // ignore
        }
        return "SISTEMA_OU_ANONIMO";
    }

    private String getRequestPath() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                    .currentRequestAttributes())
                    .getRequest();
            return request.getMethod() + " " + request.getRequestURI();
        } catch (Exception e) {
            return "N/A";
        }
    }

    private String getMethodArgs(JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args == null || args.length == 0) return "Sem argumentos";
            // tenta serializar o primeiro argumento (normalmente o @RequestBody)
            return objectMapper.writeValueAsString(args[0]);
        } catch (JsonProcessingException e) {
            return "Erro ao serializar argumentos do método";
        }
    }
}

