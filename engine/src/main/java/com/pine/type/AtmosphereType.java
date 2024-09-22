package com.pine.type;

import com.pine.component.SelectableEnum;

public enum AtmosphereType implements SelectableEnum {
    MIE("Mie", 0),
    RAYLEIGH("Rayleigh", 1),
    COMBINED("Combined", 2);

    private final String label;
    private final int id;

    AtmosphereType(String label, int id) {
        this.id = id;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    @Override
    public String getLabel() {
        return label;
    }
}
