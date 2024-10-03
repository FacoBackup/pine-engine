package com.pine.panels.files;

import com.pine.dock.AbstractDockPanel;
import com.pine.injection.PInject;
import com.pine.service.FSService;
import imgui.ImGui;

public class FilesPanel extends AbstractDockPanel {
    @PInject
    public FSService service;

    @Override
    public void onInitialize() {
        
        refreshFiles();
        getContext().subscribe(this::refreshFiles);

        appendChild(new FilesHeaderPanel());
        appendChild(new FilesDirectoryPanel());
    }

    @Override
    public void renderInternal() {
        ImGui.beginGroup();
        super.renderInternal();
        ImGui.endGroup();
    }

    private void refreshFiles() {
        final FilesContext context = (FilesContext) getContext();
        final String directory = (context).getDirectory();
        service.refreshFiles(directory, () -> {
            context.setFiles(service.readFiles(directory));
        });
    }
}
