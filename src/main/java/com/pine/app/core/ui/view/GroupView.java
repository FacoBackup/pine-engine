package com.pine.app.core.ui.view;

import com.pine.app.core.ui.View;
import com.pine.app.core.ui.panel.AbstractPanel;
import imgui.ImGui;

public class GroupView extends AbstractView {
    public GroupView(View parent, String id, AbstractPanel panel) {
        super(parent, id, panel);
    }

    @Override
    public void render() {
        ImGui.beginGroup();
        super.render();
        ImGui.endGroup();
    }
}
