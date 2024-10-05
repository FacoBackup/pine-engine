package com.pine.panels.files;

import com.pine.dock.AbstractDockPanel;
import com.pine.injection.PInject;
import com.pine.repository.ContentBrowserRepository;
import com.pine.service.FSService;

public class FilesPanel extends AbstractDockPanel {
    @PInject
    public FSService service;

    @PInject
    public ContentBrowserRepository contentBrowserRepository;
    private FilesContext context;
    private FilesDirectoryPanel directory;
    private FilesHeaderPanel header;

    @Override
    public void onInitialize() {
        context = (FilesContext) getContext();
        if (context.currentDirectory == null) {
            context.currentDirectory = contentBrowserRepository.root;
        }
        
        appendChild(header = new FilesHeaderPanel());
        appendChild(directory = new FilesDirectoryPanel());
    }

    @Override
    public void renderInternal() {

        header.render();
        directory.render();
    }
}
