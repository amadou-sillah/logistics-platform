package com.logistics.pattern.strategy;

import com.logistics.model.Shipment;

public interface PricingStrategy {
    double calculatePrice(Shipment shipment);
}
