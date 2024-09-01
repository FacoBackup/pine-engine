package com.pine.app.core.ui.view;

import com.pine.app.core.ui.View;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;

public class DivView extends AbstractView {
    private int width = 0;
    private int height = 0;

    public DivView(View parent, String id) {
        super(parent, id);
    }

    @Override
    public void render() {
        if (!visible) {
            return;
        }

        ImGui.beginChild(innerText + internalId,  width, height, true, ImGuiWindowFlags.NoScrollbar);
        super.render();
        ImGui.endChild();
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
