package com.pine.tools.types;

import com.pine.inspection.SelectableEnum;

public enum DebugShadingModel implements SelectableEnum {
    ALBEDO("Albedo", 0),
    NORMAL("Normal", 1),
    TANGENT("Tangent", 2),
    DEPTH("Depth", 3),
    AO("Ambient occlusion", 4),
    DETAIL("Detail", 5),
    LIGHT_ONLY("Light only", 6),
    METALLIC("Metallic", 7),
    ROUGHNESS("Roughness", 8),
    G_AO("Generated ambient occlusion", 9),
    AMBIENT("Ambient", 10),
    POSITION("Position", 11),
    UV("UV", 12),
    RANDOM("Random", 13),
    OVERDRAW("Overdraw", 14),
    LIGHT_COMPLEXITY("Lighting complexity", 15),
    LIGHT_QUANTITY("Light quantity", 16);

    private final int id;
    private final String label;

    DebugShadingModel(String label, int id) {
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
