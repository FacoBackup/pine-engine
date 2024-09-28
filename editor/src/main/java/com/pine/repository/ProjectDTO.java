package com.pine.repository;

import com.pine.view.RepeatingViewItem;

import java.io.File;
import java.util.Date;
import java.util.UUID;

public class ProjectDTO implements RepeatingViewItem {
    private final Date creationDate = new Date();
    private final String id = UUID.randomUUID().toString();
    private final String path = System.getProperty("user.home") + File.separator + id;
    private String name = "New project";

    public Date getCreationDate() {
        return creationDate;
    }

    public String getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getKey() {
        return id;
    }
}
