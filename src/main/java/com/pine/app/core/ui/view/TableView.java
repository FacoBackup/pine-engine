package com.pine.app.core.ui.view;

import com.pine.app.core.ui.View;
import com.pine.app.core.ui.view.table.TableHeader;
import imgui.ImGui;

import java.util.Collections;
import java.util.List;

public class TableView extends RepeatingView {
    private int maxCells = 3;
    private List<TableHeader> headerColumns = Collections.emptyList();

    public TableView(View parent, String id) {
        super(parent, id);
    }

    @Override
    protected void renderInternal() {
        ImGui.beginTable(innerText + internalId, maxCells);
        for (var column : headerColumns) {
            ImGui.tableSetupColumn(column.getTitle(), column.getFlags(), column.getColumnWidth());
        }
        if (!headerColumns.isEmpty()) {
            ImGui.tableHeadersRow();
        }

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

    public void setHeaderColumns(List<TableHeader> headerColumns) {
        this.headerColumns = headerColumns;
    }

    public void setMaxCells(int maxCells) {
        this.maxCells = maxCells;
    }
}
