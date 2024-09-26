package com.pine.dock;

import com.pine.PBean;

import java.util.ArrayList;
import java.util.List;

@PBean
public class DockRepository {
    private final List<DockGroup> dockGroups = new ArrayList<>();
    public void addDockGroup(DockGroup dock) {
        dockGroups.add(dock);
    }
}
