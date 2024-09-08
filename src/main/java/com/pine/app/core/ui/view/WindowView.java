package com.pine.app.core.ui.view;

import com.pine.app.core.ui.View;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;

public class WindowView extends AbstractView {
    private boolean autoResize = true;
    private boolean noMove = true;
    private boolean noCollapse = false;
    private boolean noDecoration = false;
    private ImVec2 dimensions = new ImVec2();
    private int[] position = new int[2];

    public WindowView(View parent, String id) {
        super(parent, id);
    }

    @Override
    public void renderInternal() {
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

        if (noDecoration) {
            flags |= ImGuiWindowFlags.NoDecoration;
        }

        ImGui.setNextWindowPos(position[0], position[1]);
        if (!autoResize) {
            flags |= ImGuiWindowFlags.NoResize;
            ImGui.setNextWindowSize(dimensions, ImGuiCond.Always);
        }
        if (ImGui.begin(tempLabel, flags)) {
            super.renderInternal();
        }
        ImGui.end();
    }

    public void setAutoResize(boolean autoResize) {
        this.autoResize = autoResize;
    }

    public void setNoDecoration(boolean noDecoration) {
        this.noDecoration = noDecoration;
    }

    public void setWidth(int width) {
        this.dimensions.x = width;
    }

    public void setHeight(int height) {
        this.dimensions.y = height;
    }

    public void setLeft(int left) {
        this.position[0] = left;
    }

    public void setTop(int top) {
        this.position[1] = top;
    }

    public void setDimensions(ImVec2 dimensions) {
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
