package com.pine.dock;

import imgui.type.ImInt;

import java.util.UUID;

public final class DockDTO {
    private final ImInt nodeId = new ImInt(0);
    private final ImInt selectedOption = new ImInt(0);
    private final String internalId;
    private int splitDir;
    private float sizeX;
    private float sizeY;
    private float sizeRatioForNodeAtDir;
    private DockDTO outAtOppositeDir;
    private DockDTO origin;
    private DockDescription description;

    public DockDTO(DockDescription description) {
        this.description = description;
        selectedOption.set(description.getOptionIndex());
        internalId = "##" + UUID.randomUUID().toString().replace("-", "");
    }

    public DockDescription getDescription() {
        return description;
    }

    public ImInt getNodeId() {
        return nodeId;
    }

    public int getSplitDir() {
        return splitDir;
    }

    public void setSplitDir(int splitDir) {
        this.splitDir = splitDir;
    }

    public float getSizeRatioForNodeAtDir() {
        return sizeRatioForNodeAtDir;
    }

    public void setSizeRatioForNodeAtDir(float sizeRatioForNodeAtDir) {
        this.sizeRatioForNodeAtDir = sizeRatioForNodeAtDir;
    }

    public DockDTO getOutAtOppositeDir() {
        return outAtOppositeDir;
    }

    public void setOutAtOppositeDir(DockDTO outAtOppositeDir) {
        this.outAtOppositeDir = outAtOppositeDir;
    }

    public DockDTO getOrigin() {
        return origin;
    }

    public void setOrigin(DockDTO origin) {
        this.origin = origin;
    }

    public String getInternalId() {
        return internalId;
    }

    public float getSizeX() {
        return sizeX;
    }

    public float getSizeY() {
        return sizeY;
    }

    public void setSizeX(float sizeX) {
        this.sizeX = sizeX;
    }

    public void setSizeY(float sizeY) {
        this.sizeY = sizeY;
    }

    public ImInt selectedOption() {
        return selectedOption;
    }

    public void setDescription(DockDescription description) {
        this.description = description;
    }
}
