package com.pine.repository;

import imgui.extension.imguizmo.flag.Operation;

public enum GizmoType {
    TRANSLATE(Operation.TRANSLATE, true),
    ROTATE(Operation.ROTATE, true),
    SCALE(Operation.SCALE, true),
    PAINT(0, false);

    public final int type;
    public final boolean isImguizmo;

    GizmoType(int type, boolean isImguizmo) {
        this.type = type;
        this.isImguizmo = isImguizmo;
    }

    public boolean isImGuizmo() {
        return isImguizmo;
    }
}
