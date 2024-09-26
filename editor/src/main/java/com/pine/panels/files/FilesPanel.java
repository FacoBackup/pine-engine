package com.pine.panels.files;

import com.pine.PInject;
import com.pine.common.fs.FSService;
import com.pine.ui.panel.AbstractWindowPanel;
import imgui.ImGui;

public class FilesPanel extends AbstractWindowPanel {
    @PInject
    public FSService service;
    private FilesHeaderPanel header;
    private FilesDirectoryPanel directory;

    public FilesPanel() {
        super();
        setContext(new FilesContext(FSService.getUserRootPath()));
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        refreshFiles();
        getContext().subscribe(this::refreshFiles);

        header = appendChild(new FilesHeaderPanel());
        directory = appendChild(new FilesDirectoryPanel());
    }

    @Override
    public void renderInternal() {
        ImGui.beginGroup();
        super.renderInternal();
        ImGui.endGroup();
    }

    @Override
    protected String getTitle() {
        return "Files";
    }

    private void refreshFiles() {
        final FilesContext context = (FilesContext) getContext();
        final String directory = (context).getDirectory();
        service.refreshFiles(directory, () -> {
            context.setFiles(service.readFiles(directory));
        });
    }
}
