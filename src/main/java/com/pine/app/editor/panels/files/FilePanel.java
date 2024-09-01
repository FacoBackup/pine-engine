package com.pine.app.editor.panels.files;

import com.pine.app.core.ui.panel.AbstractPanel;
import com.pine.app.core.ui.view.TextView;
import com.pine.common.fs.FileInfoDTO;

public class FilePanel extends AbstractPanel {
    private final FileInfoDTO item;

    public FilePanel(FileInfoDTO item) {
        this.item = item;
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        var name = (TextView) getDocument().getElementById("name");
        name.setInnerText(item.fileName());
    }
}
