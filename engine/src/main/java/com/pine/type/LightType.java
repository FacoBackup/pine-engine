package com.pine.type;

import com.pine.component.SelectableEnum;

public enum LightType implements SelectableEnum {
    DIRECTIONAL("Directional", 1),
    SPOT("Spot", 2),
    POINT("Point", 3),
    SPHERE("Area sphere", 4),
    DISK("Area disk", 5),
    PLANE("Area plane", 6);

    private final String label;
    private final int typeId;

    LightType(String type, int typeId) {
        this.label = type;
        this.typeId = typeId;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public int getTypeId() {
        return typeId;
    }
}
