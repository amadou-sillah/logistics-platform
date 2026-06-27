package com.logistics.repository;

import com.logistics.model.AuditLog;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends BaseRepository<AuditLog, String> {
}
