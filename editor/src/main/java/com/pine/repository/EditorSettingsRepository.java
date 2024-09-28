package com.pine.repository;

import com.pine.PBean;
import com.pine.SerializableRepository;
import com.pine.inspection.Inspectable;
import com.pine.inspection.MutableField;
import com.pine.theme.Icons;
import com.pine.tools.types.DebugShadingModel;
import com.pine.tools.types.ExecutionEnvironment;
import imgui.ImVec4;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
import imgui.type.ImFloat;
import imgui.type.ImInt;
import org.joml.Vector4f;

@PBean
public class EditorSettingsRepository extends Inspectable implements SerializableRepository {
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
    public DebugShadingModel debugShadingModel = DebugShadingModel.ALBEDO;

    @MutableField(label = "Accent color")
    public final Vector4f accentColor = new Vector4f(0.26f, 0.59f, 0.98f, 1);
    transient private final ImVec4 accent = new ImVec4();

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

    public float[] gizmoSnapTranslate = new float[3];
    public ImFloat gizmoSnapRotate = new ImFloat();
    public ImFloat gizmoSnapScale = new ImFloat();

    public final ImInt gizmoSnapTranslateOption = new ImInt(0);
    public final ImInt gizmoSnapRotateOption = new ImInt(0);
    public final ImInt gizmoSnapScaleOption = new ImInt(0);

    public boolean gizmoUseSnapTranslate;
    public boolean gizmoUseSnapRotate;
    public boolean gizmoUseSnapScale;
    public final ImInt gizmoModeOption = new ImInt(0);
    public final ImInt shadingModelOption = new ImInt(0);

    @Override
    public String getIcon() {
        return Icons.settings;
    }

    @Override
    public String getTitle() {
        return "Editor Settings";
    }

    public ImVec4 getAccentColor() {
        accent.x = accentColor.x;
        accent.y = accentColor.y;
        accent.z = accentColor.z;
        accent.w = accentColor.w;
        return accent;
    }
}
