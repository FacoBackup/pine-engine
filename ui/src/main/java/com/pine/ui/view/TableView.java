package com.pine.ui.view;

import com.pine.ui.View;
import com.pine.ui.view.table.TableHeader;
import imgui.ImGui;
import imgui.flag.ImGuiTableBgTarget;
import imgui.flag.ImGuiTableFlags;

import java.util.Collections;
import java.util.List;

import static com.pine.ui.theme.ThemeUtil.ACCENT_COLOR;

public class TableView extends ListView {
    private static final int FLAGS = ImGuiTableFlags.ScrollY | ImGuiTableFlags.RowBg;
    private int maxCells = 3;
    private List<TableHeader> headerColumns = Collections.emptyList();
    private static final int ACCENT = ImGui.getColorU32(ACCENT_COLOR);

    public TableView(View parent, String id) {
        super(parent, id);
    }

    @Override
    public void renderInternal() {
        ImGui.beginTable(innerText + internalId, maxCells, FLAGS);
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
            if (child.isVisible()) {
                child.tick();
                ImGui.tableNextRow();
                child.renderInternal();
            }
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

    public static void highlightRow(){
        ImGui.tableSetBgColor(ImGuiTableBgTarget.RowBg0, ACCENT);
        ImGui.tableSetBgColor(ImGuiTableBgTarget.RowBg1, ACCENT);
    }
}
