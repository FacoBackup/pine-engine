package com.pine.repository;

import com.pine.PBean;
import com.pine.injection.EngineExternalModule;
import com.pine.repository.GizmoOperation;
import com.pine.repository.GizmoOperationMode;
import com.pine.tools.ExecutionEnvironment;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
import imgui.type.ImFloat;

import java.util.LinkedList;

@PBean
public class EditorRepository {
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

    public ExecutionEnvironment environment = ExecutionEnvironment.DEVELOPMENT;
    public int gizmoOperation = Operation.TRANSLATE;
    public int gizmoMode = Mode.LOCAL;
    public boolean gizmoUseSnap;
    public float[] gizmoSnapTranslate = new float[3];
    public ImFloat gizmoSnapRotate = new ImFloat();
    public ImFloat gizmoSnapScale = new ImFloat();

}