package com.logistics.repository;

import com.logistics.model.DeliveryAgent;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryAgentRepository extends BaseRepository<DeliveryAgent, String> {
}
