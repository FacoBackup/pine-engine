package com.pine.app.panels.files;

import com.pine.Icon;
import com.pine.PInject;
import com.pine.app.EditorWindow;
import com.pine.common.fs.FileInfoDTO;
import com.pine.service.loader.ResourceLoaderService;
import com.pine.service.loader.impl.info.MeshLoaderExtraInfo;
import com.pine.ui.panel.AbstractPanel;
import com.pine.ui.view.TableView;
import imgui.ImGui;
import imgui.ImVec4;

public class FilePanel extends AbstractPanel {
    private final FileInfoDTO item;
    private final ImVec4 color = new ImVec4();
    private String iconCodepoint;
    private FilesContext context;

    @PInject
    public ResourceLoaderService loader;

    public FilePanel(FileInfoDTO item) {
        super();
        this.item = item;
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        context = (FilesContext) getContext();
        iconCodepoint = item.isDirectory() ? Icon.FOLDER.codePoint : Icon.FILE.codePoint;
        if (item.isDirectory()) {
            color.x = 1;
            color.y = 0.8352941f;
            color.z = 0.38039216f;
        } else {
            color.x = color.y = color.z = 1;
        }

        color.w = 1;
    }

    @Override
    public void renderInternal() {
        if (context.getSelectedFile() == item) {
            TableView.highlightRow();
        }

        ImGui.tableNextColumn();
        ImGui.textColored(color, iconCodepoint);
        ImGui.tableNextColumn();
        ImGui.text(item.fileName());
        if (ImGui.isItemClicked()) {
            onClick();
        }
        ImGui.tableNextColumn();
        ImGui.text(item.fileType());
        ImGui.tableNextColumn();
        ImGui.text(item.fileSize());
    }

    private void onClick() {
        context.setSelectedFile(context.getSelectedFile() == item ? null : item);
        if (ImGui.isMouseDoubleClicked(0)) {
            if (item.isDirectory()) {
                ((FilesContext) getContext()).setDirectory(item.absolutePath());
            } else {
                loader.load(item.absolutePath(), false, new MeshLoaderExtraInfo().setInstantiateHierarchy(true));
            }
        }
    }

}
