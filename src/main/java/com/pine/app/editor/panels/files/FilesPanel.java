package com.pine.app.editor.panels.files;

import com.pine.app.core.ui.panel.AbstractPanel;
import com.pine.common.Inject;
import com.pine.common.fs.FSService;

public class FilesPanel extends AbstractPanel {

    @Inject
    private FSService service;

    @Override
    public void onInitialize() {
        super.onInitialize();
        setInternalContext(new FilesContext(service.getUserRootPath(), service.readFiles(service.getUserRootPath())));
        appendChild(new FilesHeaderPanel());
        appendChild(new FilesTreePanel());
        appendChild(new FilesDirectoryPanel());
    }

}
