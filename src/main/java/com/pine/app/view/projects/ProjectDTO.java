package com.pine.app.view.projects;

import com.pine.app.view.component.view.RepeatingViewItem;
import com.pine.app.view.core.state.StringState;

import java.io.File;
import java.util.Date;

public class ProjectDTO implements RepeatingViewItem {
    private String name = "New project";
    private String path = System.getProperty("user.dir") + File.separator + "newProject" + (new Date());
    private boolean isEditing = false;

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public boolean isEditing() {
        return isEditing;
    }

    public void setEditing(boolean editing) {
        isEditing = editing;
    }

    @Override
    public String getKey() {
        return path;
    }
}
