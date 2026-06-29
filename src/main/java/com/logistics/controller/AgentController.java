package com.logistics.controller;

import com.logistics.dto.ApiResponse;
import com.logistics.dto.AgentRequest;
import com.logistics.model.Agent;
import com.logistics.service.AgentService;
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
    
    private final AgentService agentService;  // ← This will now be injected successfully!
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Agent>> createAgent(@Valid @RequestBody AgentRequest request) {
        log.info("📦 Creating agent: {}", request.getUserId());
        ApiResponse<Agent> response = agentService.createAgent(request);
        
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        return ResponseEntity.badRequest().body(response);
    }
    
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
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Agent>> getAgentById(@PathVariable String id) {
        ApiResponse<Agent> response = agentService.getAgentById(id);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Agent>> updateAgent(
            @PathVariable String id,
            @Valid @RequestBody AgentRequest request) {
        log.info("📦 Updating agent: {}", id);
        ApiResponse<Agent> response = agentService.updateAgent(id, request);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }
    
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Agent>> updateAgentStatus(
            @PathVariable String id,
            @RequestParam boolean active) {
        ApiResponse<Agent> response = agentService.updateAgentStatus(id, active);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteAgent(@PathVariable String id) {
        log.info("📦 Deleting agent: {}", id);
        ApiResponse<Void> response = agentService.deleteAgent(id);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }
    
    private String getCurrentUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
