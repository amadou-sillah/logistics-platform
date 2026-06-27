package com.logistics.pattern.strategy;

import com.logistics.model.Shipment;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class StandardPricingStrategy implements PricingStrategy {
    @Override
    public double calculatePrice(Shipment shipment) {
        return shipment.getAmount() != null ? shipment.getAmount() : 10.0;
    }
}
