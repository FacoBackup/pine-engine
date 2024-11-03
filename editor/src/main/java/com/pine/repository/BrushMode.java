package com.pine.repository;

import com.pine.theme.Icons;

public enum BrushMode {
    ADD(Icons.add + "Add"),
    REMOVE(Icons.remove + "Remove");

    public final String label;

    BrushMode(String label) {
        this.label = label;
    }
}
