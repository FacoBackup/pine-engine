package com.pine.panels.header;

import com.pine.theme.Icons;

import java.io.Serializable;

public enum EditorTab implements Serializable {
    FILE("File", Icons.folder),
    VIEWPORT("Viewport", Icons.personal_video);

    private final String label;
    private final String icon;

    EditorTab(String label, String icon) {
        this.label = label;
        this.icon = icon;
    }

    public String label() {
        return label;
    }

    public String icon() {
        return icon;
    }
}
