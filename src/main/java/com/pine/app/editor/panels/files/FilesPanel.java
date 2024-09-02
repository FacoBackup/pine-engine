package com.pine.app.editor.panels.files;

import com.pine.app.ProjectService;
import com.pine.app.core.ui.panel.AbstractPanel;
import com.pine.app.core.ui.view.WindowView;
import com.pine.common.Inject;
import com.pine.common.fs.FSService;

public class FilesPanel extends AbstractPanel {
    @Inject
    public FSService service;

    @Inject
    public ProjectService projectService;

    public FilesPanel() {
        super();
        internalContext = new FilesContext(projectService.getCurrentProject().getPath());
    }

    @Override
    protected String getDefinition() {
        return """
                <window id="filesRoot">
                    Files
                    <group>
                        <fragment id='filesHeader'/>
                        <inline id='filesContainer'/>
                    </group>
                </window>
                """;
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        setCurrentDirectory(service, (FilesContext) internalContext);
        var filesRoot = (WindowView) document.getElementById("filesRoot");
        var container = document.getElementById("filesContainer");
        document.getElementById("filesHeader").appendChild(new FilesHeaderPanel());
        container.appendChild(new FilesTreePanel());
        container.appendChild(new FilesDirectoryPanel());
        filesRoot.setAutoResize(true);
        filesRoot.setNoMove(false);
        filesRoot.setNoCollapse(true);
    }

    public static void setCurrentDirectory(FSService service, FilesContext context) {
        context.setDirectory(context.getDirectory());
        context.getFiles().clear();
        service.refreshFiles(context.getDirectory());
    }
}
