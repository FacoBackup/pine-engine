package com.pine.app.editor.panels.files;

import com.pine.app.ProjectService;
import com.pine.app.core.ui.panel.AbstractPanel;
import com.pine.app.core.ui.view.WindowView;
import com.pine.common.Inject;
import com.pine.common.fs.FSService;

import java.nio.file.Files;

public class FilesPanel extends AbstractPanel {
    @Inject
    public FSService service;

    @Inject
    public ProjectService projectService;

    public FilesPanel() {
        super();
        setContext(new FilesContext(projectService.getCurrentProject().getPath()));
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
        container.appendChild(new FilesTreePanel());
        container.appendChild(new FilesDirectoryPanel());
    }

    private void refreshFiles() {
        final FilesContext context = (FilesContext) getContext();
        final String directory = (context).getDirectory();
        service.refreshFiles(directory, () -> {
            context.setFiles(service.readFiles(directory));
        });
    }
}
