package com.pine.type;

import com.pine.inspection.SelectableEnum;

public enum LightType implements SelectableEnum {
    DIRECTIONAL("Directional light", 1, 32),
    SPOT("Spot light", 2, 15),
    POINT("Point light", 3, 15),
    SPHERE("Spherical area light", 4, 13);

    private final String title;
    private final int typeId;
    private int dataDisplacement;

    LightType(String type, int typeId, int dataDisplacement) {
        this.title = type;
        this.typeId = typeId;
        this.dataDisplacement = dataDisplacement;
    }

    @Override
    public String getTitle() {
        return title;
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
