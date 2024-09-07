package com.pine.app.editor.panels.files;

import com.pine.app.core.ui.panel.AbstractPanel;
import com.pine.app.core.ui.view.TableView;
import com.pine.common.fs.FileInfoDTO;

public class FilesDirectoryPanel extends AbstractPanel {

    @Override
    protected String getDefinition() {
        return "<table id='list'/>";
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        var context = (FilesContext) getContext();
        var table = (TableView) document.getElementById("list");
        table.setData(context.getFiles());
        table.setGetView(item -> new FilePanel((FileInfoDTO) item));
    }

}
