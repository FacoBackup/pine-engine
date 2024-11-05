package com.pine.repository;

import com.pine.theme.Icons;

public enum PaintingType {
    TERRAIN(Icons.terrain + "Terrain", 0),
    FOLIAGE(Icons.forest + "Foliage", 1),
    MATERIAL(Icons.format_paint + "Material", 2);

    public final String label;
    public final int id;

    PaintingType(String label, int id) {
        this.label = label;
        this.id = id;
    }
}
