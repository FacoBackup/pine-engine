package com.pine.app.core.ui.panel;

import imgui.type.ImInt;

public final class DockDTO {
    private final ImInt nodeId =  new ImInt(0);
    private int splitDir;
    private float sizeRatioForNodeAtDir;
    private DockDTO outAtOppositeDir;
    private DockDTO origin;
    private final String name;
    private final Class<? extends AbstractWindowPanel> bodyPanelClass;

    public DockDTO(String name, Class<? extends AbstractWindowPanel> bodyPanelClass){
        this.name = name;
        this.bodyPanelClass = bodyPanelClass;
    }

    public String getName() {
        return name;
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

    public Class<? extends AbstractWindowPanel> getBodyPanelClass() {
        return bodyPanelClass;
    }

}
