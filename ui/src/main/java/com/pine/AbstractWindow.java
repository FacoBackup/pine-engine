package com.pine;

import com.pine.dock.AbstractDockHeader;
import com.pine.dock.DockPanel;
import com.pine.view.AbstractView;
import imgui.ImGui;
import imgui.ImVec4;
import imgui.flag.ImGuiCol;

public abstract class AbstractWindow extends AbstractView {
    protected final DockPanel root = new DockPanel() {
        @Override
        protected ImVec4 getAccentColor() {
            return AbstractWindow.this.getAccentColor();
        }
    };

    final public void initializeWindow() {
        appendChild(root);
        onInitializeInternal();
        root.setHeader(getHeader());
        root.onInitialize();
    }

    protected abstract void onInitializeInternal();

    protected abstract AbstractDockHeader getHeader();

    @Override
    final public void render() {
        tick();
        ImGui.pushStyleColor(ImGuiCol.Button, getNeutralPalette());
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, getAccentColor());
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, getAccentColor());
        renderInternal();
        ImGui.popStyleColor(3);
    }

    protected abstract ImVec4 getNeutralPalette();

    protected abstract ImVec4 getAccentColor();

    public float getWindowScaleX() {
        return 1;
    }

    public float getWindowScaleY() {
        return 1;
    }
}
