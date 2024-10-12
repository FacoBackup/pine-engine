package com.pine.panels.files;

import com.pine.core.panel.AbstractPanelContext;
import com.pine.repository.fs.ResourceEntry;

public class FilesContext extends AbstractPanelContext {
    public ResourceEntry currentDirectory;
    public ResourceEntry selected;

    public void setDirectory(ResourceEntry parentDir) {
        currentDirectory = parentDir;
        onChange();
    }
}
