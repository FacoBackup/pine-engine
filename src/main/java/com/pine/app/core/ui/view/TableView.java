package com.pine.app.core.ui.view;

import com.pine.app.core.ui.View;
import com.pine.app.core.ui.view.table.TableHeader;
import imgui.ImGui;
import imgui.flag.ImGuiTableColumnFlags;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TableView extends RepeatingView {
    private int maxCells = 3;
    private List<TableHeader> columns = Collections.emptyList();

    public TableView(View parent, String id) {
        super(parent, id);
    }

    @Override
    protected void renderInternal() {
        ImGui.beginTable(innerText + internalId, maxCells);
        for (var column : columns) {
            ImGui.tableSetupColumn(column.getTitle(), column.getFlags(), column.getColumnWidth());
        }
        if (!columns.isEmpty()) {
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

    public void setColumns(List<TableHeader> columns) {
        this.columns = columns;
    }

    public void setMaxCells(int maxCells) {
        this.maxCells = maxCells;
    }
}
