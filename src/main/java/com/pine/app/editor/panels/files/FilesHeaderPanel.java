package com.pine.app.editor.panels.files;

import com.pine.app.core.ui.panel.AbstractPanel;
import com.pine.app.core.ui.view.InputView;
import com.pine.common.Inject;
import com.pine.common.fs.FSService;

public class FilesHeaderPanel extends AbstractPanel {
    @Inject
    public FSService fsService;

    @Override
    protected String getDefinition() {
        return """
                <inline>
                    <input id="path"/>
                </inline>
                """;
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        var context = (FilesContext) getContext();
        var path = (InputView) getDocument().getElementById("path");
        path.setState(context.getDirectory());
        path.setOnChange(this::pathChange);
        // FORWARD
        // BACKWARD
    }

    private void pathChange(String path) {
        if (fsService.exists(path)) {
            var context = ((FilesContext) getContext());
            context.setDirectory(path);
            FilesPanel.setCurrentDirectory(fsService, context);
        }
    }
}
