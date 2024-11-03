package com.pine.repository;

import com.pine.theme.Icons;

public enum PaintingType {
    TERRAIN(Icons.terrain + "Terrain"),
    FOLIAGE(Icons.forest + "Foliage"),
    MATERIAL(Icons.format_paint + "Material");

    public final String label;

    PaintingType(String label) {
        this.label = label;
    }
}
