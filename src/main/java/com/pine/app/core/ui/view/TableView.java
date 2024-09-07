package com.pine.app.core.ui.view;

import com.pine.app.core.ui.View;
import com.pine.app.core.ui.view.table.TableHeader;
import imgui.ImGui;
import imgui.flag.ImGuiTableFlags;

import java.util.Collections;
import java.util.List;

public class TableView extends ListView {
    private int maxCells = 3;
    private List<TableHeader> headerColumns = Collections.emptyList();

    public TableView(View parent, String id) {
        super(parent, id);
    }

    @Override
    protected void renderInternal() {
        ImGui.beginTable(innerText + internalId, maxCells, ImGuiTableFlags.ScrollY);
        for (var column : headerColumns) {
            if (column.getColumnWidth() > 0) {
                ImGui.tableSetupColumn(column.getTitle(), column.getFlags(), column.getColumnWidth());
            } else {
                ImGui.tableSetupColumn(column.getTitle(), column.getFlags());
            }
        }
        if (!headerColumns.isEmpty()) {
            ImGui.tableHeadersRow();
        }

        for (RepeatingViewItem item : data) {
            String key = item.getKey();
            var child = getView(item, key);
            ImGui.tableNextRow();
            child.render();
        }
        ImGui.endTable();
    }

    public void setHeaderColumns(List<TableHeader> headerColumns) {
        this.headerColumns = headerColumns;
    }

    public void setMaxCells(int maxCells) {
        if (maxCells >= 1 && maxCells <= 64) {
            this.maxCells = maxCells;
        }
    }
}
