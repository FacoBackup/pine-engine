package com.pine.repository.fs;

import com.pine.theme.Icons;

public enum ResourceEntryType {
    MATERIAL(Icons.format_paint, "Material"),
    DIRECTORY(Icons.folder, "Directory"),
    MESH(Icons.category, "Mesh"),
    AUDIO(Icons.audio_file, "Audio"),
    TEXTURE(Icons.texture, "Texture"),
    SCENE(Icons.inventory_2, "Scene");

    private final String icon;
    private final String label;

    ResourceEntryType(String icon, String label) {
        this.icon = icon;
        this.label = label;
    }

    public String getIcon() {
        return icon;
    }

    public String getLabel() {
        return label;
    }
}
