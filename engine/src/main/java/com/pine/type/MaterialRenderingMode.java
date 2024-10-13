package com.pine.type;

import com.pine.inspection.SelectableEnum;

public enum MaterialRenderingMode implements SelectableEnum {
    ISOTROPIC("Isotropic", 1),
    ANISOTROPIC("Anisotropic", 2),
    SHEEN("Sheen", 3),
    CLEAR_COAT("Clear coat", 4),
    TRANSPARENCY("Transparency", 5);

    private final String label;
    private final int id;

    MaterialRenderingMode(String label, int id) {
        this.label = label;
        this.id = id;
    }

    @Override
    public String getTitle() {
        return label;
    }

    public int getId() {
        return id;
    }
}
