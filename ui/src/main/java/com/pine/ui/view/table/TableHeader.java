package com.pine.ui.view.table;

import imgui.flag.ImGuiTableColumnFlags;

public class TableHeader {
    private int flags;
    private final String title;
    private final int columnWidth;

    public TableHeader(String title, int columnWidth) {
        this(title, columnWidth, ImGuiTableColumnFlags.WidthFixed);
    }

    public TableHeader(String title) {
        this(title, 0, ImGuiTableColumnFlags.WidthStretch);
    }

    private TableHeader(String title, int columnWidth, int flags) {
        this.title = title;
        this.flags = flags | ImGuiTableColumnFlags.NoSort;
        if (title.isEmpty()) {
            this.flags |= ImGuiTableColumnFlags.NoHeaderLabel;
        }
        this.columnWidth = columnWidth;
    }

    public String getTitle() {
        return title;
    }

    public int getColumnWidth() {
        return columnWidth;
    }

    public int getFlags() {
        return flags;
    }
}
