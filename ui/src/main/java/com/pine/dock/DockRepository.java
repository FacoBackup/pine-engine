package com.pine.dock;

import com.pine.Initializable;
import com.pine.PBean;
import com.pine.SerializableRepository;

import java.util.ArrayList;
import java.util.List;

@PBean
public class DockRepository implements SerializableRepository {
    public transient List<DockGroup> dockGroups = new ArrayList<>();
    public transient DockGroup currentDockGroup;
    public transient DockDTO dockToRemove;
    public transient DockWrapperPanel dockPanelToRemove;
    public transient DockGroup template;

    public void setCurrentDockGroup(DockGroup currentDockGroup) {
        this.currentDockGroup = currentDockGroup;
    }

    public DockGroup getCurrentDockGroup() {
        return currentDockGroup;
    }

    public void addDockGroup(DockGroup dock) {
        dockGroups.add(dock);
    }

    public List<DockGroup> getDockGroups() {
        return dockGroups;
    }
}
