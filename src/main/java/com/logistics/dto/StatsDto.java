package com.logistics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatsDto {
    private String label;
    private String value;
    private String icon;
    private String change;
    private boolean positive;
}
