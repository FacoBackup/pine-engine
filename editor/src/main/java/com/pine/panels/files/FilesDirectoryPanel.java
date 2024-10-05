package com.pine.panels.files;

import com.pine.injection.PInject;
import com.pine.repository.ContentBrowserRepository;
import com.pine.repository.FileInfoDTO;
import com.pine.view.AbstractView;
import com.pine.view.RepeatingViewItem;
import com.pine.view.TableView;
import com.pine.view.table.TableHeader;
import imgui.ImGui;
import imgui.flag.ImGuiTableFlags;

import java.util.List;

public class FilesDirectoryPanel extends AbstractView {
    private static final int FLAGS = ImGuiTableFlags.ScrollY | ImGuiTableFlags.RowBg;

    @PInject
    public ContentBrowserRepository contentBrowserRepository;

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
    public void renderInternal() {
//        if (ImGui.beginTable(imguiId, 3, FLAGS)) {
//                ImGui.tableSetupColumn("", column.getFlags(), column.getColumnWidth());
//                ImGui.tableSetupColumn("Name", column.getFlags(), column.getColumnWidth());
//                ImGui.tableSetupColumn("Type", column.getFlags(), column.getColumnWidth());
//                ImGui.tableSetupColumn("Size", column.getFlags(), column.getColumnWidth());
//                ImGui.tableHeadersRow();
//
//            for (RepeatingViewItem item : data) {
//                String key = item.getKey();
//                var child = getView(item, key);
//                if (child.isVisible()) {
//                    child.tick();
//                    ImGui.tableNextRow();
//                    child.renderInternal();
//                }
//            }
//            ImGui.endTable();
//        }
    }
}
