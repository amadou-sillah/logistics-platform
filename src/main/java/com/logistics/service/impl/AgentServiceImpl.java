package com.logistics.service.impl;

import com.logistics.dto.ApiResponse;
import com.logistics.dto.AgentRequest;
import com.logistics.model.Agent;
import com.logistics.repository.AgentRepository;
import com.logistics.service.AgentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service  // ← CRITICAL: This makes it a Spring Bean!
@RequiredArgsConstructor
@Slf4j
public class AgentServiceImpl implements AgentService {
    
    private final AgentRepository agentRepository;
    
    @Override
    public ApiResponse<Agent> createAgent(AgentRequest request) {
        try {
            log.info("Creating agent: {}", request.getUserId());
            
            // Check duplicates
            if (agentRepository.findByUserId(request.getUserId()).isPresent()) {
                return ApiResponse.error("User ID already exists", 
                    List.of(new ApiResponse.ValidationError("userId", "User ID already taken")));
            }
            
            if (agentRepository.findByEmail(request.getEmail()).isPresent()) {
                return ApiResponse.error("Email already exists",
                    List.of(new ApiResponse.ValidationError("email", "Email already registered")));
            }
            
            // Create agent
            Agent agent = new Agent();
            agent.setUserId(request.getUserId());
            agent.setFullName(request.getFullName());
            agent.setEmail(request.getEmail());
            agent.setPhone(request.getPhone());
            agent.setVehicle(request.getVehicle());
            agent.setVehicleNumber(request.getVehicleNumber());
            agent.setActive(true);
            agent.setIsAvailable(true);
            agent.setJoinedDate(LocalDateTime.now());
            agent.setRating(0.0);
            agent.setTotalDeliveries(0);
            
            Agent saved = agentRepository.save(agent);
            log.info("✅ Agent created: {}", saved.getUserId());
            
            return ApiResponse.success("Agent created successfully", saved);
            
        } catch (Exception e) {
            log.error("❌ Error creating agent: {}", e.getMessage());
            return ApiResponse.error("Failed to create agent: " + e.getMessage());
        }
    }
    
    @Override
    public ApiResponse<List<Agent>> getAllAgents(String search, Boolean active, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Agent> agentPage;
            
            if (search != null && !search.isEmpty()) {
                List<Agent> agents = agentRepository.search(search);
                return ApiResponse.success("Agents fetched successfully", agents);
            } else if (active != null) {
                if (active) {
                    agentPage = (Page<Agent>) agentRepository.findByActiveTrue();
                } else {
                    agentPage = agentRepository.findAll(pageable);
                }
            } else {
                agentPage = agentRepository.findAll(pageable);
            }
            
            return ApiResponse.success("Agents fetched successfully", agentPage.getContent());
            
        } catch (Exception e) {
            log.error("❌ Error fetching agents: {}", e.getMessage());
            return ApiResponse.error("Failed to fetch agents: " + e.getMessage());
        }
    }
    
    @Override
    public ApiResponse<Agent> getAgentById(String id) {
        try {
            Optional<Agent> agentOpt = agentRepository.findById(id);
            if (agentOpt.isEmpty()) {
                return ApiResponse.error("Agent not found");
            }
            return ApiResponse.success("Agent found", agentOpt.get());
        } catch (Exception e) {
            log.error("❌ Error fetching agent: {}", e.getMessage());
            return ApiResponse.error("Failed to fetch agent: " + e.getMessage());
        }
    }
    
    @Override
    public ApiResponse<Agent> getAgentByUserId(String userId) {
        try {
            Optional<Agent> agentOpt = agentRepository.findByUserId(userId);
            if (agentOpt.isEmpty()) {
                return ApiResponse.error("Agent not found");
            }
            return ApiResponse.success("Agent found", agentOpt.get());
        } catch (Exception e) {
            log.error("❌ Error fetching agent: {}", e.getMessage());
            return ApiResponse.error("Failed to fetch agent: " + e.getMessage());
        }
    }
    
    @Override
    public ApiResponse<Agent> updateAgent(String id, AgentRequest request) {
        try {
            Optional<Agent> agentOpt = agentRepository.findById(id);
            if (agentOpt.isEmpty()) {
                return ApiResponse.error("Agent not found");
            }
            
            Agent agent = agentOpt.get();
            
            // Update fields
            if (request.getFullName() != null) agent.setFullName(request.getFullName());
            if (request.getEmail() != null) agent.setEmail(request.getEmail());
            if (request.getPhone() != null) agent.setPhone(request.getPhone());
            if (request.getVehicle() != null) agent.setVehicle(request.getVehicle());
            if (request.getVehicleNumber() != null) agent.setVehicleNumber(request.getVehicleNumber());
            
            Agent updated = agentRepository.save(agent);
            log.info("✅ Agent updated: {}", updated.getUserId());
            
            return ApiResponse.success("Agent updated successfully", updated);
            
        } catch (Exception e) {
            log.error("❌ Error updating agent: {}", e.getMessage());
            return ApiResponse.error("Failed to update agent: " + e.getMessage());
        }
    }
    
    @Override
    public ApiResponse<Agent> updateAgentStatus(String id, boolean active) {
        try {
            Optional<Agent> agentOpt = agentRepository.findById(id);
            if (agentOpt.isEmpty()) {
                return ApiResponse.error("Agent not found");
            }
            
            Agent agent = agentOpt.get();
            agent.setActive(active);
            Agent updated = agentRepository.save(agent);
            
            String status = active ? "activated" : "deactivated";
            log.info("✅ Agent {}: {}", updated.getUserId(), status);
            
            return ApiResponse.success("Agent " + status + " successfully", updated);
            
        } catch (Exception e) {
            log.error("❌ Error updating agent status: {}", e.getMessage());
            return ApiResponse.error("Failed to update agent status: " + e.getMessage());
        }
    }
    
    @Override
    public ApiResponse<Void> deleteAgent(String id) {
        try {
            Optional<Agent> agentOpt = agentRepository.findById(id);
            if (agentOpt.isEmpty()) {
                return ApiResponse.error("Agent not found");
            }
            
            // Soft delete
            Agent agent = agentOpt.get();
            agent.setActive(false);
            agentRepository.save(agent);
            
            log.info("✅ Agent deactivated: {}", agent.getUserId());
            return ApiResponse.success("Agent deactivated successfully", null);
            
        } catch (Exception e) {
            log.error("❌ Error deleting agent: {}", e.getMessage());
            return ApiResponse.error("Failed to delete agent: " + e.getMessage());
        }
    }
    
    @Override
    public long getTotalAgents() {
        return agentRepository.count();
    }
    
    @Override
    public long getActiveAgents() {
        return agentRepository.countByActiveTrue();
    }
    
    @Override
    public long getInactiveAgents() {
        return agentRepository.count() - agentRepository.countByActiveTrue();
    }
}
