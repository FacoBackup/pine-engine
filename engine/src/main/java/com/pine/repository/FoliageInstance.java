package com.pine.repository;

import com.pine.inspection.Color;

import java.util.UUID;

public class FoliageInstance {
    public final String id = UUID.randomUUID().toString();
    public String material;
    public String mesh;
    public final Color color = new Color();
    public int count = 0;
    public int offset = 0;

    public FoliageInstance(int i) {
        color.x = (i >> 16) & 0xFF;
        color.y = (i >> 8) & 0xFF;
        color.z = i & 0xFF;
    }
}
