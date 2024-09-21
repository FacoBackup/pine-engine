package com.pine.type;

import com.pine.component.SelectableEnum;

public enum LightType implements SelectableEnum {
    DIRECTIONAL("Directional"),
    SPOT("Spot"),
    POINT("Point"),
    SPHERE("Area sphere"),
    DISK("Area disk"),
    PLANE("Area plane");

    private final String label;

    LightType(String type) {
        this.label = type;
    }

    @Override
    public String getLabel() {
        return label;
    }
}
