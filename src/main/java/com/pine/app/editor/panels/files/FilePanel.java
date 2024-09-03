package com.pine.app.editor.panels.files;

import com.pine.app.core.ui.panel.AbstractPanel;
import com.pine.app.core.ui.view.DivView;
import com.pine.app.core.ui.view.TextView;
import com.pine.common.fs.FileInfoDTO;

public class FilePanel extends AbstractPanel {
    private final FileInfoDTO item;

    public FilePanel(FileInfoDTO item) {
        super();
        this.item = item;
    }

    @Override
    protected String getDefinition() {
        return """
            <inline>
                 <icon>folder</icon>
                <button>
                    <text id="name"/>
                </button>
            </inline>
            """;
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        var name = (TextView) document.getElementById("name");
        name.setInnerText(item.fileName());
        var div = (DivView) name.getParent();
        div.setHeight(50);
        div.setWidth(75);
    }
}
