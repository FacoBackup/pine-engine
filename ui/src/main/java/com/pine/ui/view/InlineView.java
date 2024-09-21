package com.pine.ui.view;

import com.pine.ui.View;
import imgui.ImGui;

public class InlineView extends AbstractView {

    public InlineView(View parent, String id) {
        super(parent, id);
    }

    @Override
    public void renderInternal() {
        int qtt = children.size() - 1;
        for (int i = 0; i <= qtt; i++) {
            View child = children.get(i);
            child.render();
            if (i < qtt && children.get(i + 1).isVisible()) {
                ImGui.sameLine();
            }
        }
    }
}
