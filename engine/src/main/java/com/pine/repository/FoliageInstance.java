package com.pine.repository;

import com.pine.inspection.Color;

public class FoliageInstance {
    public final String id;
    public int index;
    public final Color color = new Color();

    public FoliageInstance(String id) {
        this.id = id;
    }
}
