package com.pine.core.dock;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DockGroup implements Serializable {
    private final String id;
    public DockDTO center;
    private String title;
    private String titleWithId;
    public final List<DockDTO> bottom = new ArrayList<>();
    public final List<DockDTO> left = new ArrayList<>();
    public final List<DockDTO> right = new ArrayList<>();

    public transient boolean isInitialized = false;

    public DockGroup(String title,
                     DockDTO center,
                     List<DockDTO> bottom,
                     List<DockDTO> left,
                     List<DockDTO> right) {
        this.id = "##" + UUID.randomUUID().toString().replaceAll("-", "");
        this.title = title;
        this.titleWithId = title + id;
        this.center = center;
        this.bottom.addAll(bottom);
        this.left.addAll(left);
        this.right.addAll(right);
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
        return new DockGroup("New dock group", center, bottom, left, right);
    }
}
