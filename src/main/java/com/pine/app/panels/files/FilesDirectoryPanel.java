package com.pine.app.panels.files;

import com.pine.app.core.ui.panel.AbstractPanel;
import com.pine.app.core.ui.view.TableView;
import com.pine.app.core.ui.view.table.TableHeader;
import com.pine.common.fs.FileInfoDTO;

import java.util.List;

public class FilesDirectoryPanel extends AbstractPanel {

    private TableView table;

    @Override
    protected String getDefinition() {
        return "<table id='list'/>";
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        table = (TableView) document.getElementById("list");
        table.setGetView(item -> new FilePanel((FileInfoDTO) item));
        table.setHeaderColumns(List.of(
                new TableHeader("", 30),
                new TableHeader("Name"),
                new TableHeader("Type"),
                new TableHeader("Size")
        ));
        table.setMaxCells(4);
    }

    @Override
    public void tick() {
        table.setData(((FilesContext) getContext()).getFiles());
    }
}
