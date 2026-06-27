package com.logistics.pattern.strategy;

import com.logistics.model.Shipment;
import org.springframework.stereotype.Component;

@Component
public class InternationalPricingStrategy implements PricingStrategy {
    @Override
    public double calculatePrice(Shipment shipment) {
        return (shipment.getAmount() != null ? shipment.getAmount() : 10.0) * 2.0;
    }
}
