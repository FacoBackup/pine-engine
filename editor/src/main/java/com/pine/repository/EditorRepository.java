package com.pine.repository;

import com.pine.SerializableRepository;
import com.pine.component.Entity;
import com.pine.component.Transformation;
import com.pine.injection.PBean;
import com.pine.inspection.Color;
import com.pine.inspection.Inspectable;
import com.pine.inspection.InspectableField;
import com.pine.repository.fs.DirectoryEntry;
import com.pine.theme.Icons;
import com.pine.tools.types.ExecutionEnvironment;
import imgui.ImVec4;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
import imgui.type.ImFloat;
import imgui.type.ImInt;

import java.io.Serial;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@PBean
public class EditorRepository extends Inspectable implements SerializableRepository {
    @Serial
    private static final long serialVersionUID = -5292170530653725873L;

    @InspectableField(group = "Grid", label = "Show grid")
    public boolean showGrid = true;

    @InspectableField(group = "Grid", label = "Grid color", min = 0, max = 1)
    public float gridColor = 0.3f;

    @InspectableField(group = "Grid", label = "Grid scale", min = 1, max = 10)
    public float gridScale = 1f;

    @InspectableField(group = "Grid", label = "Grid threshold", max = 500, min = 100)
    public int gridThreshold = 100;

    @InspectableField(group = "Grid", label = "Grid opacity")
    public float gridOpacity = 1f;

    @InspectableField(group = "Editor", label = "Accent color")
    public final Color accentColor = new Color(0.26f, 0.59f, 0.98f);

    public transient final ImVec4 accent = new ImVec4();
    public int accentU32 = 0;

    @InspectableField(group = "Editor", label = "Dark mode")
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
    public int gizmoMode = Mode.WORLD;

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
    public final ImInt shadingModelOption = new ImInt(DebugShadingModel.LIT.getIndex());
    public boolean showOnlyEntitiesHierarchy = false;

    public Map<String, Entity> pinnedEntities = new HashMap<>();
    public Transformation primitiveSelected = null;
    public boolean gizmoExternalChange = false;
    public Entity mainSelection = null;
    public final LinkedList<Entity> selected = new LinkedList<>();
    public final LinkedList<String> copied = new LinkedList<>();
    public final DirectoryEntry root = new DirectoryEntry("Files", null);

    @Override
    public String getIcon() {
        return Icons.settings;
    }

    @Override
    public String getTitle() {
        return "Editor Settings";
    }
}
