package com.pine.app.editor.panels.files;

import com.pine.app.core.ui.View;
import com.pine.app.core.ui.panel.AbstractPanel;
import com.pine.app.core.ui.panel.IPanelContext;
import com.pine.common.Inject;
import com.pine.common.fs.FSService;

public class FilesPanel extends AbstractPanel {
    @Inject
    public FSService service;

    public FilesPanel() {
        internalContext = new FilesContext(FSService.getUserRootPath());
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        var context = (FilesContext) internalContext;
        context.getFiles().addAll(service.readFiles(context.getDirectory()));
        View filesRoot = getDocument().getElementById("filesRoot");
        filesRoot.appendChild(new FilesHeaderPanel());
        filesRoot.appendChild(new FilesTreePanel());
        filesRoot.appendChild(new FilesDirectoryPanel());
    }
}
