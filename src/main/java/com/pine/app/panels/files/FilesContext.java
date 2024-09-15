package com.pine.app.panels.files;

import com.pine.app.core.ui.panel.AbstractPanelContext;
import com.pine.common.fs.FileInfoDTO;

import java.util.ArrayList;
import java.util.List;

public class FilesContext extends AbstractPanelContext {
    private String directory;
    private List<FileInfoDTO> files = new ArrayList<>();
    private FileInfoDTO selectedFile;

    public FilesContext(String directory) {
        this.directory = directory;
    }

    public List<FileInfoDTO> getFiles() {
        return files;
    }

    public void setFiles(List<FileInfoDTO> files) {
        this.files = files;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
        onChange();
    }

    public FileInfoDTO getSelectedFile() {
        return selectedFile;
    }

    public void setSelectedFile(FileInfoDTO selectedFile) {
        this.selectedFile = selectedFile;
    }
}
