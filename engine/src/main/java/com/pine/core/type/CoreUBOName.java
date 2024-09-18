package com.pine.core.type;

public enum CoreUBOName {
    CAMERA_VIEW("CameraViewInfo"),
    FRAME_COMPOSITION("CompositionSettings"),
    LENS_PP("LensEffects"),
    SSAO("Settings"),
    UBER("UberShaderSettings"),
    LIGHTS("Lights"),
    CAMERA_PROJECTION("CameraProjectionInfo");

    private final String blockName;

    CoreUBOName(String blockName) {
        this.blockName = blockName;
    }

    public String getBlockName() {
        return blockName;
    }
}
