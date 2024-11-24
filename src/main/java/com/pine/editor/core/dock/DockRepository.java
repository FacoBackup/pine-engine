package com.pine.editor.core.dock;

import com.pine.common.SerializableRepository;
import com.pine.common.injection.PBean;
import com.pine.common.injection.PostCreation;

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
        center = new DockDTO(DockSpace.Viewport);
        DockDTO rightUp = new DockDTO(DockSpace.Hierarchy);
        DockDTO rightDown = new DockDTO(DockSpace.Inspector);
        DockDTO downLeft = new DockDTO(DockSpace.Console);
        DockDTO downRight = new DockDTO(DockSpace.Files);

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
