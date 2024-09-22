package com.pine.panels.files;

import com.pine.PInject;
import com.pine.common.fs.FSService;
import com.pine.common.fs.FileInfoDTO;
import com.pine.service.loader.ResourceLoaderService;
import com.pine.service.loader.impl.info.MeshLoaderExtraInfo;
import com.pine.ui.panel.AbstractPanel;
import com.pine.ui.view.ButtonView;
import com.pine.ui.view.InputView;

import java.io.File;

public class FilesHeaderPanel extends AbstractPanel {
    @PInject
    public FSService fsService;
    @PInject
    public ResourceLoaderService resourceLoader;

    private ButtonView importFile;
    private FilesContext filesContext;

    @Override
    protected String getDefinition() {
        return """
                <inline>
                    <button id='addDir'>[FolderPlus]</button>
                    <button id='goUp'>[ArrowUp]</button>
                    <input id='path'/>
                    <button id='import'>[File] Import File</button>
                </inline>
                """;
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        filesContext = (FilesContext) getContext();
        importFile = (ButtonView) document.getElementById("import");
        var addDir = (ButtonView) document.getElementById("addDir");
        var goUp = (ButtonView) document.getElementById("goUp");
        var path = (InputView) document.getElementById("path");

        path.setValue(filesContext.getDirectory());
        path.setOnChange(this::pathChange);

        importFile.setOnClick(this::importFile);
        addDir.setOnClick(() -> fsService.createDirectory(filesContext.getDirectory() + File.separator + "New folder"));
        goUp.setOnClick(() -> filesContext.setDirectory(fsService.getParentDir(filesContext.getDirectory())));

        filesContext.subscribe(() -> {
            path.setValue(filesContext.getDirectory());
        });
    }

    @Override
    public void tick() {
        FileInfoDTO selected = filesContext.getSelectedFile();
        importFile.setVisible(selected != null && !selected.isDirectory());
    }

    private void importFile() {
        FileInfoDTO file = filesContext.getSelectedFile();
        if (file != null && !file.isDirectory()) {
            resourceLoader.load(file.absolutePath(), false, new MeshLoaderExtraInfo().setInstantiateHierarchy(true));
        }
    }

    private void pathChange(String path) {
        if (fsService.exists(path)) {
            var context = ((FilesContext) getContext());
            context.setDirectory(path);
        }
    }
}