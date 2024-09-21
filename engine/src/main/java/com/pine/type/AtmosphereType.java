package com.pine.type;

import com.pine.component.SelectableEnum;

public enum AtmosphereType implements SelectableEnum {
    MIE("Mie'"),
    RAYLEIGH("Rayleigh"),
    COMBINED("Combined");

    private final String label;

    AtmosphereType(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
    }
}
