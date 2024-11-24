package com.pine.editor.repository;

import com.pine.common.Icons;

public enum EditorMode {
    TRANSFORM("Transform", Icons.transform, 0),
    TERRAIN("Terrain", Icons.terrain, 1),
    FOLIAGE("Foliage", Icons.forest, 2),
    MATERIAL("Material", Icons.format_paint, 3);

    private static String[] options;
    public final String label;
    public final String icon;
    public final int index;

    EditorMode(String label, String icon, int index) {
        this.label = label;
        this.icon = icon;

        this.index = index;
    }

    public static String[] getOptions() {
        if (options == null) {
            options = new String[EditorMode.values().length];
            for (int i = 0; i < EditorMode.values().length; i++) {
                options[i] = EditorMode.values()[i].icon + EditorMode.values()[i].label;
            }
        }
        return options;
    }

    public static EditorMode valueOfIndex(int index) {
        for (int i = 0; i < EditorMode.values().length; i++) {
            var c = EditorMode.values()[i];
            if (c.index == index) {
                return c;
            }
        }
        return null;
    }
}
