package com.pine.engine.repository;

import com.pine.common.inspection.SelectableEnum;

import java.io.Serializable;

public enum ShadingMode implements SelectableEnum, Serializable {
    ALBEDO("Albedo", 0, 0),
    NORMAL("Normal", 1, 1),
    DEPTH("Depth", 3, 2),
    AO("Ambient occlusion", 4, 3),
    LIGHT_ONLY("Light only", 6, 4),
    METALLIC("Metallic", 7, 5),
    ROUGHNESS("Roughness", 8, 6),
    POSITION("Position", 11, 7),
    RANDOM("Random", 13, 8),
    WIREFRAME("Wireframe", 17, 9),
    UV("UV", 18, 10),
    INDIRECT("Indirect illumination", 19, 11),
    TRIANGLE_ID("Triangle id", 20, 12),
    HEIGHT("Height", 21, 13),
    MATERIAL_MASK("Material mask", 22, 14),
    LIT("Lit", -1, 15);

    private final int index;
    private final int id;
    private final String label;
    private static final String[] labels = new String[values().length];

    ShadingMode(String label, int id, int index) {
        this.id = id;
        this.label = label;
        this.index = index;
    }

    public int getId() {
        return id;
    }

    public int getIndex() {
        return index;
    }

    public static String[] getLabels() {
        if (labels[0] == null) {
            ShadingMode[] values = values();
            for (int i = 0, valuesLength = values.length; i < valuesLength; i++) {
                var value = values[i];
                labels[i] = value.label;
            }
        }
        return labels;
    }

    @Override
    public String getTitle() {
        return label;
    }
}
