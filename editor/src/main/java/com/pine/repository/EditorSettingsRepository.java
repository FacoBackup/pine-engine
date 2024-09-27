package com.pine.repository;

import com.pine.PBean;
import com.pine.inspection.Inspectable;
import com.pine.inspection.MutableField;
import com.pine.theme.Icons;
import com.pine.tools.types.DebugShadingModel;
import com.pine.tools.types.ExecutionEnvironment;
import imgui.ImVec4;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
import imgui.type.ImFloat;

@PBean
public class EditorSettingsRepository extends Inspectable {
    @MutableField(label = "Show grid")
    public boolean showGrid = true;

    @MutableField(label = "Grid color")
    public float gridColor = 0.3f;

    @MutableField(label = "Grid scale")
    public float gridScale = 1f;

    @MutableField(label = "Grid threshold")
    public int gridThreshold = 100;

    @MutableField(label = "Grid opacity")
    public float gridOpacity = 1f;

    @MutableField(label = "Shading model")
    public DebugShadingModel debugShadingModel = DebugShadingModel.RANDOM;

    @MutableField(label = "Accent color")
    public final ImVec4 accentColor = new ImVec4(0.26f, 0.59f, 0.98f, 1);

    @MutableField(label = "Dark mode")
    public boolean isDarkMode = true;

    // Icon settings
    public boolean showIcons = true;
    public float iconScale = 1f;

    // Outline settings
    public boolean showOutline = true;
    public float outlineWidth = 0.75f;
    public float[] outlineColor = {1f, 0.5f, 0f};

    public ExecutionEnvironment environment = ExecutionEnvironment.DEVELOPMENT;
    public int gizmoOperation = Operation.TRANSLATE;
    public int gizmoMode = Mode.LOCAL;
    public boolean gizmoUseSnap;
    public float[] gizmoSnapTranslate = new float[3];
    public ImFloat gizmoSnapRotate = new ImFloat();
    public ImFloat gizmoSnapScale = new ImFloat();

    @Override
    public String getIcon() {
        return Icons.settings;
    }

    @Override
    public String getTitle() {
        return "Editor Settings";
    }
}
