package com.pine.app.panels.files;

import com.pine.app.EditorWindow;
import com.pine.Icon;
import com.pine.ui.panel.AbstractPanel;
import com.pine.ui.view.TableView;
import com.pine.common.fs.FileInfoDTO;
import com.pine.core.service.loader.ResourceLoaderService;
import com.pine.core.service.loader.impl.info.MeshLoaderExtraInfo;
import imgui.ImGui;
import imgui.ImVec4;

public class FilePanel extends AbstractPanel {
    private final FileInfoDTO item;
    private final ImVec4 color = new ImVec4();
    private String iconCodepoint;
    private FilesContext context;
    private ResourceLoaderService loader;

    public FilePanel(FileInfoDTO item) {
        super();
        this.item = item;
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        loader = ((EditorWindow) document.getWindow()).getEngine().getResourceLoaderService();
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
