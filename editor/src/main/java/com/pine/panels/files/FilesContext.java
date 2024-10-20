package com.pine.panels.files;

import com.pine.core.panel.AbstractPanelContext;
import com.pine.repository.fs.DirectoryEntry;
import com.pine.repository.fs.FileEntry;

import java.util.HashMap;
import java.util.Map;

public class FilesContext extends AbstractPanelContext {
    public DirectoryEntry currentDirectory;
    public final Map<String, Boolean> selected = new HashMap<>();
    public final Map<String, DirectoryEntry> toCut = new HashMap<>();
    public transient FileEntry inspection;

    public void setDirectory(DirectoryEntry parentDir) {
        currentDirectory = parentDir;
        onChange();
    }

    public void setInspection(FileEntry entry) {
        this.inspection = entry;
        onChange();
    }
}
