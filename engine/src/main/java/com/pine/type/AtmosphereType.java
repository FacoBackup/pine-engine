package com.pine.type;

import com.pine.inspection.SelectableEnum;

import java.io.Serializable;

public enum AtmosphereType implements SelectableEnum, Serializable {
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
    public String getTitle() {
        return label;
    }
}
