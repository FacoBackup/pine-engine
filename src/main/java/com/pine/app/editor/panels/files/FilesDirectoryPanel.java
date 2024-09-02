package com.pine.app.editor.panels.files;

import com.pine.app.core.ui.View;
import com.pine.app.core.ui.panel.AbstractPanel;
import com.pine.app.core.ui.view.RepeatingViewItem;
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
        var list = (TableView) getDocument().getElementById("list");
        list.setData(context.getFiles());
        list.setGetView(this::createListItem);
    }

    private View createListItem(RepeatingViewItem repeatingViewItem) {
        var item = (FileInfoDTO) repeatingViewItem;
        return new FilePanel(item);
    }
}
