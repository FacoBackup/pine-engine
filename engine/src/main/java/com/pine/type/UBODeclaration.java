package com.pine.type;

public enum UBODeclaration {
    CAMERA_VIEW("CameraViewInfo"),
    FRAME_COMPOSITION("CompositionSettings"),
    // TODO - SIMPLE UNIFORMS, NO NEED FOR UBO SINCE THIS WILL ONLY EXECUTE ONCE PER FRAME
    LENS_PP("LensEffects"),
    SSAO("Settings"),
    UBER("UberShaderSettings"),
    CAMERA_PROJECTION("CameraProjectionInfo");

    private final String blockName;

    UBODeclaration(String blockName) {
        this.blockName = blockName;
    }

    public String getBlockName() {
        return blockName;
    }
}
