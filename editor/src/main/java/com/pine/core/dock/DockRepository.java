package com.pine.core.dock;

import com.pine.SerializableRepository;
import com.pine.injection.PBean;
import com.pine.injection.PostCreation;
import com.pine.window.EditorDock;

import java.util.ArrayList;
import java.util.List;

@PBean
public class DockRepository implements SerializableRepository {
    public transient boolean isInitialized;
    public DockDTO center;
    public final List<DockDTO> bottom = new ArrayList<>();
    public final List<DockDTO> left = new ArrayList<>();
    public final List<DockDTO> right = new ArrayList<>();
    public transient DockDTO dockToRemove;
    public transient DockSpacePanel dockPanelToRemove;

    @PostCreation
    public void onInitialize(){
        center = new DockDTO(EditorDock.Viewport);
        DockDTO rightUp = new DockDTO(EditorDock.Hierarchy);
        DockDTO rightDown = new DockDTO(EditorDock.Inspector);
        DockDTO downLeft = new DockDTO(EditorDock.Console);
        DockDTO downRight = new DockDTO(EditorDock.Files);

        center.setSizeRatioForNodeAtDir(0.17f);
        rightUp.setSizeRatioForNodeAtDir(0.4f);
        rightDown.setSizeRatioForNodeAtDir(0.6f);
        downLeft.setSizeRatioForNodeAtDir(0.22f);
        downRight.setSizeRatioForNodeAtDir(0.5f);

        right.add(rightUp);
        right.add(rightDown);

        bottom.add(downLeft);
        bottom.add(downRight);
    }
}
