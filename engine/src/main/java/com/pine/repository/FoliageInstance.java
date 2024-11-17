package com.pine.repository;

import com.pine.inspection.Color;

public class FoliageInstance {
    public final String id;
    public String material;
    public final Color color = new Color();
    public int count = 0;
    public int offset = 0;

    public FoliageInstance(String id, int i) {
        this.id = id;
        color.x = (i >> 16) & 0xFF;
        color.y = (i >> 8) & 0xFF;
        color.z = i & 0xFF;
    }
}
