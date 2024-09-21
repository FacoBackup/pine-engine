package com.pine.ui.view;

import com.pine.ui.View;
import imgui.ImGui;

public class GroupView extends AbstractView {

    public GroupView(View parent, String id) {
        super(parent, id);
    }

    @Override
    public void renderInternal() {
        ImGui.beginGroup();
        super.renderInternal();
        ImGui.endGroup();
    }
}
