package com.pine.repository;

import com.pine.SerializableRepository;
import com.pine.component.TransformationComponent;
import com.pine.injection.PBean;
import com.pine.inspection.Color;
import com.pine.inspection.Inspectable;
import com.pine.inspection.InspectableField;
import com.pine.service.camera.Camera;
import com.pine.theme.Icons;
import com.pine.tools.types.ExecutionEnvironment;
import imgui.ImVec4;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
import imgui.type.ImFloat;
import imgui.type.ImInt;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@PBean
public class EditorRepository extends Inspectable implements SerializableRepository {
    @InspectableField(group = "Icons", label = "Show")
    public boolean showIcons = true;
    @InspectableField(group = "Icons", label = "Scale")
    public float iconScale = 1;
    @InspectableField(group = "Icons", label = "Color")
    public Color iconColor = new Color(1f, 1f, 1f);

    @InspectableField(group = "Editor", label = "Accent color")
    public final Color accentColor = new Color(0.26f, 0.59f, 0.98f);
    @InspectableField(group = "Editor", label = "Dark mode")
    public boolean isDarkMode = true;

    @InspectableField(group = "Grid", label = "Show")
    public boolean showGrid = true;
    @InspectableField(group = "Grid", label = "Overlay objects")
    public boolean gridOverlayObjects = true;
    @InspectableField(group = "Grid", label = "Grid scale", min = 1, max = 10)
    public float gridScale = 1f;
    @InspectableField(group = "Grid", label = "Grid threshold", max = 500, min = 100)
    public int gridThreshold = 100;
    @InspectableField(group = "Grid", label = "Line thickness", min = 1)
    public float gridThickness = 2;

    public transient final ImVec4 accent = new ImVec4();
    public int accentU32 = 0;

    // Outline settings
    @InspectableField(group = "Outline", label = "Show outline")
    public boolean showOutline = true;
    @InspectableField(group = "Outline", label = "width")
    public float outlineWidth = 2f;
    @InspectableField(group = "Outline", label = "Color")
    public Color outlineColor = new Color(1f, 0.5f, 0f);


    public ExecutionEnvironment environment = ExecutionEnvironment.DEVELOPMENT;
    public EditorMode editorMode = EditorMode.TRANSFORM;
    public int gizmoType = Operation.SCALE;
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

    public final ImInt shadingModelOption = new ImInt(ShadingMode.LIT.getIndex());
    public boolean showOnlyEntitiesHierarchy = false;

    public Map<String, Boolean> pinnedEntities = new HashMap<>();
    public transient TransformationComponent primitiveSelected = null;
    public String mainSelection = null;
    public final Map<String, Boolean> selected = new HashMap<>();
    public final LinkedList<String> copied = new LinkedList<>();
    public final Map<String, Camera> viewportCamera = new HashMap<>();

    public BrushMode brushMode = BrushMode.ADD;
    public float brushRadius = 10;
    public float brushDensity = .5f;
    public String foliageForPainting;
    public String materialForPainting;
    public boolean fullScreen;
    public boolean showPaintingMask = false;


    @Override
    public String getIcon() {
        return Icons.settings;
    }

    @Override
    public String getTitle() {
        return "Editor Settings";
    }
}
