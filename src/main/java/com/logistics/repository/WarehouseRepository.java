package com.logistics.repository;

import com.logistics.model.Warehouse;
import org.springframework.stereotype.Repository;

@Repository
public interface WarehouseRepository extends BaseRepository<Warehouse, String> {
}
