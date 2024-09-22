package com.pine.type;

public enum BlockPoint {
    CAMERA_VIEW("CameraViewInfo"),
    FRAME_COMPOSITION("CompositionSettings"),
    LENS_PP("LensEffects"),
    SSAO("Settings"),
    UBER("UberShaderSettings"),
    LIGHTS("Lights"),
    CAMERA_PROJECTION("CameraProjectionInfo");

    private final String blockName;

    BlockPoint(String blockName) {
        this.blockName = blockName;
    }

    public String getBlockName() {
        return blockName;
    }
}
