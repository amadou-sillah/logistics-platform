package com.logistics.controller;

import com.logistics.dto.ApiResponse;
import com.logistics.dto.AgentRequest;
import com.logistics.model.Agent;
import com.logistics.service.AgentService;
import com.logistics.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agents")
@RequiredArgsConstructor
@Slf4j
public class AgentController {
    
    private final AgentService agentService;
    private final NotificationService notificationService;
    
    // CREATE AGENT
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Agent>> createAgent(@Valid @RequestBody AgentRequest request) {
        log.info("📦 Creating agent: {}", request.getUserId());
        ApiResponse<Agent> response = agentService.createAgent(request);
        
        if (response.isSuccess() && notificationService != null) {
            notificationService.createSystemNotification(
                "New agent created: " + request.getFullName() + " (" + request.getUserId() + ")"
            );
        }
        
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        return ResponseEntity.badRequest().body(response);
    }
    
    // GET ALL AGENTS (Admin only)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Agent>>> getAllAgents(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        ApiResponse<List<Agent>> response = agentService.getAllAgents(search, active, page, size);
        return ResponseEntity.ok(response);
    }
    
    // GET CURRENT AGENT'S PROFILE
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Agent>> getCurrentAgent() {
        String userId = getCurrentUserId();
        ApiResponse<Agent> response = agentService.getAgentByUserId(userId);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    
    // GET AGENT BY ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Agent>> getAgentById(@PathVariable String id) {
        ApiResponse<Agent> response = agentService.getAgentById(id);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    
    // UPDATE AGENT
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Agent>> updateAgent(
            @PathVariable String id,
            @Valid @RequestBody AgentRequest request) {
        log.info("📦 Updating agent: {}", id);
        ApiResponse<Agent> response = agentService.updateAgent(id, request);
        
        if (response.isSuccess() && notificationService != null) {
            Agent agent = response.getData();
            notificationService.createTaskNotification(
                agent.getId(),
                "Your profile has been updated by admin",
                "PROFILE_UPDATE"
            );
        }
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }
    
    // UPDATE AGENT STATUS
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Agent>> updateAgentStatus(
            @PathVariable String id,
            @RequestParam boolean active) {
        ApiResponse<Agent> response = agentService.updateAgentStatus(id, active);
        
        if (response.isSuccess() && notificationService != null) {
            Agent agent = response.getData();
            String status = active ? "activated" : "deactivated";
            notificationService.createTaskNotification(
                agent.getId(),
                "Your account has been " + status + " by admin",
                "STATUS_UPDATE"
            );
        }
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }
    
    // DELETE AGENT (Soft delete)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteAgent(@PathVariable String id) {
        log.info("📦 Deleting agent: {}", id);
        ApiResponse<Void> response = agentService.deleteAgent(id);
        
        if (response.isSuccess() && notificationService != null) {
            notificationService.createSystemNotification("Agent deactivated: " + id);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }
    
    private String getCurrentUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
