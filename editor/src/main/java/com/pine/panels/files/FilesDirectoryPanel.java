package com.pine.panels.files;

import com.pine.repository.FileInfoDTO;
import com.pine.view.AbstractView;
import com.pine.view.TableView;
import com.pine.view.table.TableHeader;

import java.util.List;

public class FilesDirectoryPanel extends AbstractView {

    private TableView table;

    @Override
    public void onInitialize() {
        
        table = appendChild(new TableView());
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
