package com.jengine.app.view.projects;

import com.jengine.app.view.core.state.StringState;

import java.io.File;
import java.util.Date;

public class ProjectDTO {
    private final StringState name = new StringState(100).setState("New project");
    private final StringState path = new StringState(100).setState(System.getProperty("user.dir") + File.separator + "newProject" + (new Date()));
    private boolean isEditing = false;

    public StringState getName() {
        return name;
    }

    public StringState getPath() {
        return path;
    }

    public boolean isEditing() {
        return isEditing;
    }

    public void setEditing(boolean editing) {
        isEditing = editing;
    }
}
