package com.pine.app.panels.files;

import com.pine.PInject;
import com.pine.common.fs.FSService;
import com.pine.ui.panel.AbstractWindowPanel;

public class FilesPanel extends AbstractWindowPanel {
    @PInject
    public FSService service;

    public FilesPanel() {
        super();
        setContext(new FilesContext(FSService.getUserRootPath()));
    }

    @Override
    protected String getDefinition() {
        return """
                <group>
                    <fragment id='filesHeader'/>
                    <inline id='filesContainer'/>
                </group>
                """;
    }

    @Override
    public void onInitialize() {
        super.onInitialize();

        getContext().subscribe(this::refreshFiles);

        var container = document.getElementById("filesContainer");
        document.getElementById("filesHeader").appendChild(new FilesHeaderPanel());
        container.appendChild(new FilesDirectoryPanel());
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
