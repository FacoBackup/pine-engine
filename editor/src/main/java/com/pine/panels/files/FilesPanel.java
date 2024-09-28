package com.pine.panels.files;

import com.pine.PInject;
import com.pine.dock.AbstractDockPanel;
import com.pine.service.FSService;
import imgui.ImGui;

public class FilesPanel extends AbstractDockPanel {
    @PInject
    public FSService service;

    public FilesPanel() {
        super();
        setContext(new FilesContext(FSService.getUserRootPath()));
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
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
