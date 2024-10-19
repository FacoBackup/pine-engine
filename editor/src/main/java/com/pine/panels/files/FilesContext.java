package com.pine.panels.files;

import com.pine.core.panel.AbstractPanelContext;
import com.pine.repository.fs.DirectoryEntry;
import com.pine.repository.fs.FileEntry;
import com.pine.repository.fs.IEntry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilesContext extends AbstractPanelContext {
    public DirectoryEntry currentDirectory;
    public final Map<String, IEntry> selected = new HashMap<>();
    public FileEntry inspection;

    public void setDirectory(DirectoryEntry parentDir) {
        currentDirectory = parentDir;
        onChange();
    }

    public void setInspection(FileEntry entry) {
        this.inspection = entry;
        onChange();
    }
}
