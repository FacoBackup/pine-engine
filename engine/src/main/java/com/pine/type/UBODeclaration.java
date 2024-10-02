package com.pine.type;

public enum UBODeclaration {
    CAMERA_VIEW("CameraViewInfo"),
    // TODO - SIMPLE UNIFORMS, NO NEED FOR UBO SINCE THIS WILL ONLY EXECUTE ONCE PER FRAME
    LENS_PP("LensEffects"),
    SSAO("Settings"),
    UBER("UberShaderSettings");

    private final String blockName;

    UBODeclaration(String blockName) {
        this.blockName = blockName;
    }

    public String getBlockName() {
        return blockName;
    }
}
