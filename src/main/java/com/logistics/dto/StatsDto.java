package com.logistics.dto;

public class StatsDto {
    private String label;
    private String value;
    private String icon;
    private String change;
    private boolean positive;

    public StatsDto(String label, String value, String icon, String change, boolean positive) {
        this.label = label;
        this.value = value;
        this.icon = icon;
        this.change = change;
        this.positive = positive;
    }
    public String getLabel() { return label; }
    public String getValue() { return value; }
    public String getIcon() { return icon; }
    public String getChange() { return change; }
    public boolean isPositive() { return positive; }
}
