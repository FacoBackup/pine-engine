package com.pine.type;

public enum UBODeclaration {
    CAMERA_VIEW("CameraViewInfo");

    private final String blockName;

    UBODeclaration(String blockName) {
        this.blockName = blockName;
    }

    public String getBlockName() {
        return blockName;
    }
}
