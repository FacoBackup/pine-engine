package com.pine.repository.fs;

import com.pine.theme.Icons;

public enum ResourceEntryType {
    DIRECTORY(Icons.folder, "Directory"),
    MESH(Icons.view_in_ar, "Mesh"),
    AUDIO(Icons.audio_file, "Audio"),
    TEXTURE(Icons.texture, "Texture");

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
