package com.pine.dock;

import com.pine.PBean;
import imgui.ImGui;
import imgui.internal.ImGuiDockNode;
import imgui.type.ImInt;

import java.util.List;

@PBean
public class DockService {
    private static final int NO_TAB_BAR_FLAG = 1 << 12;

    public void buildViews(List<DockDTO> docks, ImInt dockMainId, DockPanel panel) {
        imgui.internal.ImGui.dockBuilderRemoveNode(dockMainId.get());
        imgui.internal.ImGui.dockBuilderAddNode(dockMainId.get(), NO_TAB_BAR_FLAG);
        imgui.internal.ImGui.dockBuilderSetNodeSize(dockMainId.get(), ImGui.getMainViewport().getSize());

        for (DockDTO dockSpace : docks) {
            createDockSpace(dockSpace, dockMainId);
        }

        for (DockDTO d : docks) {
            addWindow(d, panel);
        }

        imgui.internal.ImGui.dockBuilderDockWindow(docks.getFirst().getInternalId(), dockMainId.get());
        imgui.internal.ImGui.dockBuilderFinish(dockMainId.get());
    }

    private void createDockSpace(DockDTO dockSpace, ImInt dockMainId) {
        int origin = dockMainId.get();
        if (dockSpace.getOrigin() != null) {
            origin = dockSpace.getOrigin().getNodeId().get();
        }
        ImInt target = dockMainId;
        if (dockSpace.getOutAtOppositeDir() != null) {
            target = dockSpace.getOutAtOppositeDir().getNodeId();
        }

        dockSpace.getNodeId().set(imgui.internal.ImGui.dockBuilderSplitNode(origin, dockSpace.getSplitDir(), dockSpace.getSizeRatioForNodeAtDir(), null, target));
        ImGuiDockNode imGuiDockNode = imgui.internal.ImGui.dockBuilderGetNode(dockSpace.getNodeId().get());
        imGuiDockNode.addLocalFlags(NO_TAB_BAR_FLAG);
    }

    private void addWindow(DockDTO d, DockPanel panel) {
        try {
            imgui.internal.ImGui.dockBuilderDockWindow(d.getInternalId(), d.getNodeId().get());
            if (d.getView() != null) {
                d.setPanelInstance(panel.appendChild(new DockWrapperPanel((DockWrapperPanel) panel.getChildren().stream().findFirst().orElse(null), d)));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
