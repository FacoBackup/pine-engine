package com.pine.engine.core;

public class ConfigurationRepository {
    // Grid settings
    public float gridColor = 0.3f;
    public float gridScale = 1f;
    public int gridThreshold = 100;
    public float gridOpacity = 1f;
    public boolean showGrid = true;

    // Icon settings
    public boolean showIcons = true;
    public boolean showLines = true;
    public float iconScale = 1f;
    public int maxDistanceIcon = 50;

    // Outline settings
    public boolean showOutline = true;
    public float outlineWidth = 0.75f;
    public float[] outlineColor = {1f, 0.5f, 0f};
}
