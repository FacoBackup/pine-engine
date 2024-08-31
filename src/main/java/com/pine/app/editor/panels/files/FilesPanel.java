package com.pine.app.editor.panels.files;

import com.pine.app.core.ui.panel.AbstractPanel;

public class FilesPanel extends AbstractPanel {
    @Override
    public void onInitialize() {
        super.onInitialize();
        appendChild(new FilesHeaderPanel());
        appendChild(new FilesTreePanel());
        appendChild(new FilesDirectoryPanel());
    }
}
