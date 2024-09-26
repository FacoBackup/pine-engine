package com.pine.dock;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DockGroup {
    private final String id;
    private String title;
    private String titleWithId;
    public final List<DockDTO> docks = new ArrayList<>();
    public boolean isInitialized = false;

    public DockGroup(String title, DockDTO... docks) {
        this.id = "##" + UUID.randomUUID().toString().replaceAll("-", "");
        this.title = title;
        this.titleWithId = title + id;
        this.docks.addAll(List.of(docks));
    }

    public String getTitleWithId() {
        return titleWithId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.titleWithId = title + id;
        this.title = title;
    }

    public DockGroup generateNew() {
        DockGroup newDockGroup = new DockGroup("New dock group");
        newDockGroup.docks.addAll(docks);
        return newDockGroup;
    }
}
