package com.pine.app.editor.panels.files;

import com.pine.app.core.ui.View;
import com.pine.app.core.ui.panel.AbstractPanel;
import com.pine.common.Inject;
import com.pine.common.fs.FSService;

public class FilesPanel extends AbstractPanel {

    @Inject
    public FSService service;

    @Override
    public void onInitialize() {
        super.onInitialize();
        View filesRoot = getDocument().getElementById("filesRoot");
        setInternalContext(new FilesContext(service.getUserRootPath(), service.readFiles(service.getUserRootPath())));
        filesRoot.appendChild(new FilesHeaderPanel());
        filesRoot.appendChild(new FilesTreePanel());
        filesRoot.appendChild(new FilesDirectoryPanel());

    }

}
