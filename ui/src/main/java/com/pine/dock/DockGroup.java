package com.pine.dock;

import java.util.ArrayList;
import java.util.List;

public class DockGroup {
    public final String title;
    public final List<DockDTO> docks = new ArrayList<>();

    public DockGroup(String title, DockDTO... docks) {
        this.title = title;
        this.docks.addAll(List.of(docks));
    }
}
