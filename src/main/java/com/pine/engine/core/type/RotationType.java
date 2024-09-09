package com.pine.engine.core.type;

public enum RotationType {
    QUATERNION(0),
    EULER_XYZ(1),
    EULER_XZY(2),
    EULER_YXZ(3),
    EULER_YZX(4),
    EULER_ZXY(5),
    EULER_ZYX(6);

    private final int id;

    RotationType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
