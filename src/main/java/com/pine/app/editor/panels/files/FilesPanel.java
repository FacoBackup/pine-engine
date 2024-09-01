package com.pine.app.editor.panels.files;

import com.pine.app.core.ui.panel.AbstractPanel;
import com.pine.app.core.ui.view.WindowView;
import com.pine.common.Inject;
import com.pine.common.fs.FSService;

public class FilesPanel extends AbstractPanel {
    @Inject
    public FSService service;

    public FilesPanel() {
        internalContext = new FilesContext(FSService.getUserRootPath());
    }

    @Override
    protected String getDefinition() {
        return """
                <window id="filesRoot">
                    Files
                </window>
                """;
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        var context = (FilesContext) internalContext;
        context.getFiles().addAll(service.readFiles(context.getDirectory()));
        var filesRoot = (WindowView) getDocument().getElementById("filesRoot");
        filesRoot.appendChild(new FilesHeaderPanel());
        filesRoot.appendChild(new FilesTreePanel());
        filesRoot.appendChild(new FilesDirectoryPanel());
        filesRoot.setAutoResize(true);
        filesRoot.setNoMove(false);
        filesRoot.setNoCollapse(true);
    }
}
