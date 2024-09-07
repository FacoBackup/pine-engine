package com.pine.app.editor.panels.files;

import com.pine.app.core.Icon;
import com.pine.app.core.ui.panel.AbstractPanel;
import com.pine.common.fs.FileInfoDTO;
import imgui.ImGui;
import imgui.ImVec4;

public class FilePanel extends AbstractPanel {
    private final FileInfoDTO item;
    private final ImVec4 color = new ImVec4();
    private String iconCodepoint;
    private String label;

    public FilePanel(FileInfoDTO item) {
        super();
        this.item = item;
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        iconCodepoint = item.isDirectory() ? Icon.FOLDER.codePoint : Icon.FILE.codePoint;
        if (item.isDirectory()) {
            color.x = 1;
            color.y = 0.8352941f;
            color.z = 0.38039216f;
        } else {
            color.x = color.y = color.z = 1;
        }

        color.w = 1;
        label = item.fileName() + "##" + item.getKey();

    }

    @Override
    protected void renderInternal() {
        ImGui.tableNextColumn();
        ImGui.textColored(color, iconCodepoint);
        ImGui.tableNextColumn();
        ImGui.text(item.fileName());

        if (item.isDirectory()) {
            if (ImGui.isItemClicked() && ImGui.isMouseDoubleClicked(0)) {
                ((FilesContext) getContext()).setDirectory(item.absolutePath());
            }
        }
        ImGui.tableNextColumn();
        ImGui.text(item.fileType());
        ImGui.tableNextColumn();
        ImGui.text(item.fileSize());
    }
}
