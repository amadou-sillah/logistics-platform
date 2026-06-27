package com.logistics.audit;

import com.logistics.model.AuditLog;
import com.logistics.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogger {
    private final AuditLogRepository auditLogRepository;

    @AfterReturning(pointcut = "execution(* com.logistics.service.*.*(..))", returning = "result")
    public void log(JoinPoint jp) {
        String method = jp.getSignature().toShortString();
        String className = jp.getTarget().getClass().getSimpleName();
        String userId = getCurrentUserId();
        AuditLog log = new AuditLog();
        log.setUserId(userId);
        log.setAction(className + "." + method);
        log.setEntityType(className);
        log.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(log);
    }

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return auth.getName();
        }
        return "SYSTEM";
    }
}
