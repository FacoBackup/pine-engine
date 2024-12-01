package com.pine.editor.repository;

import com.pine.common.Icons;

public enum BrushMode {
    ADD(Icons.add + "Add"),
    REMOVE(Icons.remove + "Remove");

    public final String label;

    BrushMode(String label) {
        this.label = label;
    }
}
