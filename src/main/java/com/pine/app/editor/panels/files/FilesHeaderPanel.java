package com.pine.app.editor.panels.files;

import com.pine.app.core.ui.panel.AbstractPanel;
import com.pine.app.core.ui.view.InputView;

public class FilesHeaderPanel extends AbstractPanel {

    @Override
    public void onInitialize() {
        super.onInitialize();
        var context = (FilesContext) getContext();
        var path = (InputView) getDocument().getElementById("path");
        path.getState().setState(context.getDirectory());
        // FORWARD
        // BACKWARD
    }
}
