package com.pine.dock;

import java.io.Serializable;
import java.util.*;

public class DockGroup implements Serializable {
    private final String id;
    private String title;
    private String titleWithId;
    public final List<DockDTO> docks = new ArrayList<>();
    transient public boolean isInitialized = false;

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
        Map<DockDTO, DockDTO> oldToNew = new LinkedHashMap<>();
        for (var d : docks) {
            DockDTO dto = new DockDTO(d.getDescription());
            oldToNew.put(d, dto);
            dto.setOrigin(d.getOrigin());
            dto.setSplitDir(d.getSplitDir());
            dto.setSizeRatioForNodeAtDir(d.getSizeRatioForNodeAtDir());
            dto.setOutAtOppositeDir(d.getOutAtOppositeDir());
        }
        for (var newDock : oldToNew.values()) {
            if (newDock.getOrigin() != null) {
                newDock.setOrigin(oldToNew.get(newDock.getOrigin()));
            }
            if (newDock.getOutAtOppositeDir() != null) {
                newDock.setOutAtOppositeDir(oldToNew.get(newDock.getOutAtOppositeDir()));
            }
        }
        newDockGroup.docks.addAll(oldToNew.values());
        return newDockGroup;
    }
}
