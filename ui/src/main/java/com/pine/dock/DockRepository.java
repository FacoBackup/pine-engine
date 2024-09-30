package com.pine.dock;

import com.pine.PBean;
import com.pine.SerializableRepository;

import java.util.ArrayList;
import java.util.List;

@PBean
public class DockRepository implements SerializableRepository {
    public List<DockGroup> dockGroups = new ArrayList<>();
    public DockGroup currentDockGroup;
    public transient DockDTO dockToRemove;
    public transient DockWrapperPanel dockPanelToRemove;
    public DockDTO currentDockDTO;
    public DockGroup template;

    public void addDockGroup(DockGroup dock) {
        dockGroups.add(dock);
    }

    public List<DockGroup> getDockGroups() {
        return dockGroups;
    }
}
