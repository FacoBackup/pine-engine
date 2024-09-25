package com.pine.type;

import com.pine.inspection.SelectableEnum;

public enum LightType implements SelectableEnum {
    DIRECTIONAL("Directional", 1, 32),
    SPOT("Spot", 2, 15),
    POINT("Point", 3, 15),
    SPHERE("Area sphere", 4, 13),
    DISK("Area disk", 5, 12),
    PLANE("Area plane", 6, 12);

    private final String label;
    private final int typeId;
    private int dataDisplacement;

    LightType(String type, int typeId, int dataDisplacement) {
        this.label = type;
        this.typeId = typeId;
        this.dataDisplacement = dataDisplacement;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public int getTypeId() {
        return typeId;
    }

    /**
     * Amount of indices occupied by light information
     * @return
     */
    public int getDataDisplacement() {
        return dataDisplacement;
    }
}
