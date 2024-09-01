package com.pine.app.editor.panels.files;

import com.pine.app.core.ui.panel.IPanelContext;
import com.pine.common.fs.FileInfoDTO;

import java.util.ArrayList;
import java.util.List;

public class FilesContext implements IPanelContext {
    private String directory;
    private final List<FileInfoDTO> files = new ArrayList<>();
    // TODO - UNDO REDO CLASS
    private final List<String> pathsHistory = new ArrayList<String>();

    public FilesContext(String directory) {
        this.directory = directory;
    }

    public List<FileInfoDTO> getFiles() {
        return files;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }
}
