package com.pine.ui.view;

import com.pine.ui.View;
import imgui.ImGui;
import imgui.ImVec4;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;

public class DivView extends AbstractView {
    private static final ImVec4 HOVERED_COLOR = new ImVec4(0.3f, 0.5f, 0.7f, 1.0f);
    private int width = 0;
    private int height = 0;
    private boolean canBeHovered = false;
    private boolean isHovered = false;
    private boolean isStylePushed = false;

    public DivView(View parent, String id) {
        super(parent, id);
    }

    @Override
    public void renderInternal() {
        if (isHovered) {
            ImGui.pushStyleColor(ImGuiCol.ChildBg, HOVERED_COLOR);
            isStylePushed = true;
        }

        ImGui.beginChild(internalId, width, height, true, ImGuiWindowFlags.NoScrollbar);
        if (canBeHovered) {
            isHovered = ImGui.isItemHovered();
        }
        super.renderInternal();
        ImGui.endChild();

        if (isStylePushed) {
            isStylePushed = false;
            ImGui.popStyleColor();
        }
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setCanBeHovered(boolean canBeHovered) {
        this.canBeHovered = canBeHovered;
    }
}
