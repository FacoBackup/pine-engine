package com.pine.core.dock;

import com.pine.core.panel.AbstractPanelContext;
import com.pine.messaging.Loggable;
import imgui.type.ImInt;

import java.io.Serializable;
import java.util.UUID;

public final class DockDTO implements Loggable, Serializable {
    private final ImInt nodeId = new ImInt(0);
    private final ImInt selectedOption = new ImInt(0);
    private final String internalId;
    private AbstractPanelContext context;
    private int splitDir;
    private float sizeX;
    private float sizeY;
    private float sizeRatioForNodeAtDir;
    private DockDTO outAtOppositeDir;
    private DockDTO origin;
    private DockDescription description;
    private DockPosition direction;

    public DockDTO(DockDescription description) {
        setDescription(description);
        selectedOption.set(description.getOptionIndex());
        internalId = "##" + UUID.randomUUID().toString().replace("-", "");
    }

    public void setDirection(DockPosition direction) {
        this.direction = direction;
    }

    public DockPosition getPosition() {
        return direction;
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
        try {
            if (description.getContext() != null) {
                context = description.getContext().getConstructor().newInstance();
            } else {
                context = null;
            }
        } catch (Exception ex) {
            getLogger().error(ex.getMessage(), ex);
        }
    }

    public AbstractPanelContext getContext() {
        return context;
    }
}
