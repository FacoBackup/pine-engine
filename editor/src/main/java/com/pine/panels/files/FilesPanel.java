package com.pine.panels.files;

import com.pine.core.dock.AbstractDockPanel;
import com.pine.injection.PInject;
import com.pine.repository.EditorRepository;
import com.pine.service.FSService;
import com.pine.service.FilesService;

public class FilesPanel extends AbstractDockPanel {
    @PInject
    public FSService service;
    @PInject
    public FilesService filesService;
    @PInject
    public EditorRepository editorRepository;


    private FilesDirectoryPanel directory;
    private FilesHeaderPanel header;

    @Override
    public void onInitialize() {
        FilesContext context = (FilesContext) getContext();
        if (context.currentDirectory == null) {
            context.currentDirectory = editorRepository.rootDirectory;
        }

        appendChild(header = new FilesHeaderPanel());
        appendChild(directory = new FilesDirectoryPanel());
    }

    @Override
    public void render() {
        header.render();
        directory.isWindowFocused = isWindowFocused;
        directory.render();
    }
}
