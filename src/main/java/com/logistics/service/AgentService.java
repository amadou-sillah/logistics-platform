package com.logistics.service;

import com.logistics.dto.ApiResponse;
import com.logistics.dto.AgentRequest;
import com.logistics.model.Agent;
import java.util.List;

public interface AgentService {
    
    // Create
    ApiResponse<Agent> createAgent(AgentRequest request);
    
    // Read
    ApiResponse<List<Agent>> getAllAgents(String search, Boolean active, int page, int size);
    ApiResponse<Agent> getAgentById(String id);
    ApiResponse<Agent> getAgentByUserId(String userId);
    
    // Update
    ApiResponse<Agent> updateAgent(String id, AgentRequest request);
    ApiResponse<Agent> updateAgentStatus(String id, boolean active);
    
    // Delete
    ApiResponse<Void> deleteAgent(String id);
    
    // Stats
    long getTotalAgents();
    long getActiveAgents();
    long getInactiveAgents();
}
