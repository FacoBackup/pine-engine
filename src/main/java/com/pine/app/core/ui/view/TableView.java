package com.pine.app.core.ui.view;

import com.pine.app.core.ui.View;
import imgui.ImGui;

public class TableView extends RepeatingView {
    private int maxCells = 3;

    public TableView(View parent, String id) {
        super(parent, id);
    }

    @Override
    protected void renderInternal() {
        ImGui.beginTable(innerText + internalId, maxCells);
        for (RepeatingViewItem item : data) {
            String key = item.getKey();
            var child = getView(item, key);
            if (child.isVisible()) {
                ImGui.tableNextColumn();
            }
            child.render();
        }
        ImGui.endTable();
    }

    public void setMaxCells(int maxCells) {
        this.maxCells = maxCells;
    }
}
