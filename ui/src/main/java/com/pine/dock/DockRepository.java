package com.pine.dock;

import com.pine.Initializable;
import com.pine.PBean;

import java.util.ArrayList;
import java.util.List;

@PBean
public class DockRepository implements Initializable {
    public final List<DockGroup> dockGroups = new ArrayList<>();
    public DockGroup currentDockGroup;
    public DockGroup template;
    public transient DockDTO dockToRemove;
    public transient DockWrapperPanel dockPanelToRemove;

    @Override
    public void onInitialize() {
        if (dockGroups.isEmpty()) {
            dockGroups.add(currentDockGroup = new DockGroup("Main View"));
        }
    }

    public void setCurrentDockGroup(DockGroup currentDockGroup) {
        this.currentDockGroup = currentDockGroup;
    }

    public DockGroup getCurrentDockGroup() {
        return currentDockGroup;
    }

    public void addDock(DockDTO dto) {
        currentDockGroup.docks.add(dto);
        currentDockGroup.isInitialized = false;
    }

    public void addDockGroup(DockGroup dock) {
        dockGroups.add(dock);
    }

    public List<DockGroup> getDockGroups() {
        return dockGroups;
    }
}
