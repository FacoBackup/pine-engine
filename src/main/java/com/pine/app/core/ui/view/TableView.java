package com.pine.app.core.ui.view;

import com.pine.app.core.ui.View;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;

public class TableView extends RepeatingView {
    private int maxCells = 3;

    public TableView(View parent, String id) {
        super(parent, id);
    }

    @Override
    public void render() {
        if (!visible) {
            return;
        }

        ImGui.beginTable(innerText + internalId, maxCells);
        for (RepeatingViewItem item : data) {
            String key = item.getKey();
            ImGui.tableNextColumn();
            getView(item, key).render();
        }
        ImGui.endTable();
    }

    public void setMaxCells(int maxCells) {
        this.maxCells = maxCells;
    }
}
