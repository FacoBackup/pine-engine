package com.pine.app.core.ui.view.table;

import imgui.flag.ImGuiTableColumnFlags;
import imgui.flag.ImGuiTableFlags;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.UUID;

public class TableHeader {
    private final int flags;
    private final String title;
    private final int columnWidth;

    public TableHeader(String title, int columnWidth) {
        this(title, columnWidth, ImGuiTableColumnFlags.WidthFixed);
    }

    public TableHeader(String title) {
        this(title, 0, ImGuiTableColumnFlags.None);
    }

    private TableHeader(String title, int columnWidth, int flags) {
        this.title = title + UUID.randomUUID().toString().replaceAll("-", "");
        this.flags = flags;
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
