package com.pine.app.editor.panels.files;

import com.pine.app.core.ui.panel.AbstractPanel;
import com.pine.app.core.ui.view.ButtonView;
import com.pine.app.core.ui.view.InputView;
import com.pine.common.Inject;
import com.pine.common.fs.FSService;

import java.io.File;

public class FilesHeaderPanel extends AbstractPanel {
    @Inject
    public FSService fsService;

    @Override
    protected String getDefinition() {
        return """
                <inline>
                    <button id='addDir'>[FolderPlus]</button>
                    <button id='goUp'>[ArrowUp]</button>
                    <input id='path'/>
                </inline>
                """;
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        FilesContext filesContext = (FilesContext) getContext();
        var addDir = (ButtonView) document.getElementById("addDir");
        var goUp = (ButtonView) document.getElementById("goUp");
        var path = (InputView) document.getElementById("path");

        path.setValue(filesContext.getDirectory());
        path.setOnChange(this::pathChange);

        addDir.setOnClick(() -> fsService.createDirectory(filesContext.getDirectory() + File.separator + "New folder"));
        goUp.setOnClick(() -> filesContext.setDirectory(fsService.getParentDir(filesContext.getDirectory())));

        filesContext.subscribe(() -> {
            path.setValue(filesContext.getDirectory());
        });
    }

    private void pathChange(String path) {
        if (fsService.exists(path)) {
            var context = ((FilesContext) getContext());
            context.setDirectory(path);
        }
    }
}
