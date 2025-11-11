package com.example.escola.infrastructure.config.aop;

import com.example.escola.application.repositories.ActivityLogRepository;
import com.example.escola.domain.entities.ActivityLog;
import com.example.escola.util.SecurityUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

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
        // if the method or class is annotated with @AuditIgnore, skip this aspect
        try {
            MethodSignature ms = (MethodSignature) joinPoint.getSignature();
            Method method = ms.getMethod();
            if (method.isAnnotationPresent(AuditIgnore.class) || method.getDeclaringClass().isAnnotationPresent(AuditIgnore.class)) {
                return;
            }
        } catch (Exception ignored) {
            // ignore reflection problems and continue
        }

        String username = SecurityUtils.getCurrentUsername();
        String action = joinPoint.getSignature().getName();
        String path = getRequestPath();
        String details = getMethodArgs(joinPoint);

        ActivityLog log = new ActivityLog(username, action + " @ " + path, details);
        logRepository.save(log);
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

            // preferência: o argumento anotado com @RequestBody (se existir)
            try {
                MethodSignature ms = (MethodSignature) joinPoint.getSignature();
                Method method = ms.getMethod();
                Parameter[] params = method.getParameters();
                for (int i = 0; i < params.length && i < args.length; i++) {
                    if (params[i].isAnnotationPresent(RequestBody.class)) {
                        return objectMapper.writeValueAsString(args[i]);
                    }
                }
            } catch (Exception ignored) {
                // se algo falhar ao inspecionar parâmetros, fallback para serializar o primeiro arg
            }

            // fallback: tenta serializar o primeiro argumento (antigo comportamento)
            return objectMapper.writeValueAsString(args[0]);
        } catch (JsonProcessingException e) {
            return "Erro ao serializar argumentos do método";
        }
    }
}
