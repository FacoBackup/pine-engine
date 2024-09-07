package com.pine.app.editor.panels.files;

import com.pine.app.core.Icon;
import com.pine.app.core.ui.View;
import com.pine.app.core.ui.panel.AbstractPanel;
import com.pine.app.core.ui.view.DivView;
import com.pine.common.fs.FileInfoDTO;
import imgui.ImGui;

public class FilePanel extends AbstractPanel {
    public static final int SIZE = 75;
    private final FileInfoDTO item;
    private View name;
    private View icon;

    public FilePanel(FileInfoDTO item) {
        super();
        this.item = item;
    }

    @Override
    protected String getDefinition() {
        return """
                <fragment>
                    <text id='icon'/>
                    <text id='name'/>
                </fragment>
                """;
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        icon = document.getElementById("icon");
        icon.setInnerText(item.isDirectory() ? Icon.FOLDER.codePoint : Icon.FILE.codePoint);
        name = document.getElementById("name");
        name.setInnerText(item.fileName());
    }

    @Override
    protected void renderInternal() {
        icon.render();
        ImGui.tableNextColumn();
        name.render();
        ImGui.tableNextColumn();
    }
}
