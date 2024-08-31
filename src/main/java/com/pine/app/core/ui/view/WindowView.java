package com.pine.app.core.ui.view;

import com.pine.app.core.ui.View;
import com.pine.app.core.ui.panel.AbstractPanel;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;

public class WindowView extends AbstractView {
    private boolean autoResize = false;
    private boolean noMove = true;
    private boolean noCollapse = false;
    private int[] dimensions = new int[2];
    private int[] position = new int[2];

    public WindowView(View parent, String id, AbstractPanel panel) {
        super(parent, id, panel);
    }

    @Override
    public void render() {
        if (!visible) {
            return;
        }

        ImGui.setNextWindowPos(position[0], position[1]);
        ImGui.setNextWindowSize(dimensions[0], dimensions[1]);

        final String tempLabel = innerText + internalId;
        int flags = ImGuiWindowFlags.None;

        if (autoResize) {
            flags |= ImGuiWindowFlags.AlwaysAutoResize;
        }

        if (noMove) {
            flags |= ImGuiWindowFlags.NoMove;
        }

        if (noCollapse) {
            flags |= ImGuiWindowFlags.NoCollapse;
        }

        ImGui.begin(tempLabel, flags);
        super.render();
        ImGui.end();
    }

    public boolean isAutoResize() {
        return autoResize;
    }

    public void setAutoResize(boolean autoResize) {
        this.autoResize = autoResize;
    }

    public int getWidth() {
        return this.dimensions[0];
    }

    public void setWidth(int width) {
        this.dimensions[0] = width;
    }

    public int getHeight() {
        return this.dimensions[1];
    }

    public void setHeight(int height) {
        this.dimensions[1] = height;
    }

    public int getLeft() {
        return this.position[0];
    }

    public void setLeft(int left) {
        this.position[0] = left;
    }

    public int getTop() {
        return this.position[1];
    }

    public void setTop(int top) {
        this.position[1] = top;
    }

    public void setDimensions(int[] dimensions) {
        this.dimensions = dimensions;
    }

    public void setPosition(int[] position) {
        this.position = position;
    }

    public void setNoCollapse(boolean noCollapse) {
        this.noCollapse = noCollapse;
    }

    public void setNoMove(boolean noMove) {
        this.noMove = noMove;
    }
}
