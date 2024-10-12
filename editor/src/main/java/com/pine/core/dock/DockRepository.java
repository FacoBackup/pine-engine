package com.pine.core.dock;

import com.pine.SerializableRepository;
import com.pine.injection.PBean;

import java.util.ArrayList;
import java.util.List;

@PBean
public class DockRepository implements SerializableRepository {
    public List<DockGroup> dockGroups = new ArrayList<>();
    public DockGroup currentDockGroup;
    public transient DockDTO dockToRemove;
    public transient DockWrapperPanel dockPanelToRemove;
    public DockGroup template;

    public void addDockGroup(DockGroup dock) {
        dockGroups.add(dock);
    }

    public List<DockGroup> getDockGroups() {
        return dockGroups;
    }
}
