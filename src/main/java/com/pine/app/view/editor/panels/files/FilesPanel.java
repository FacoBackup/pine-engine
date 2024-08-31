package com.pine.app.view.editor.panels.files;

import com.pine.app.view.core.component.panel.AbstractPanel;

public class FilesPanel extends AbstractPanel {
    @Override
    public void onInitialize() {
        super.onInitialize();
        appendChild(new FilesHeaderPanel());
        appendChild(new FilesTreePanel());
        appendChild(new FilesDirectoryPanel());
    }
}
