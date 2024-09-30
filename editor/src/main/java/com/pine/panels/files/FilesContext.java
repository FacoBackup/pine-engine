package com.pine.panels.files;

import com.pine.panel.AbstractPanelContext;
import com.pine.repository.FileInfoDTO;
import com.pine.service.FSService;

import java.util.ArrayList;
import java.util.List;

public class FilesContext extends AbstractPanelContext {
    private String directory = FSService.getUserRootPath();
    private List<FileInfoDTO> files = new ArrayList<>();
    private FileInfoDTO selectedFile;

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
