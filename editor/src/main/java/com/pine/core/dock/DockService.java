package com.pine.core.dock;

import com.pine.injection.PBean;
import com.pine.injection.PInject;
import imgui.ImGui;
import imgui.flag.ImGuiDir;
import imgui.internal.ImGuiDockNode;
import imgui.type.ImInt;

import java.util.List;

@PBean
public class DockService {
    private static final int NO_TAB_BAR_FLAG = 1 << 12;

    @PInject
    public DockRepository dockRepository;

    public void setDockGroupTemplate(DockGroup template) {
        dockRepository.template = template;
    }

    public List<DockGroup> getDockGroups() {
        return dockRepository.getDockGroups();
    }

    public void switchDockGroups(DockGroup targetGroup, ImInt dockMainId) {
        imgui.internal.ImGui.dockBuilderRemoveNode(dockMainId.get());
        dockRepository.currentDockGroup = targetGroup;
        targetGroup.isInitialized = false;
    }

    public void createDockGroup() {
        if (dockRepository.template != null) {
            DockGroup dockGroup = dockRepository.template.generateNew();
            dockGroup.setTitle(dockGroup.getTitle() + " " + dockRepository.getDockGroups().size());
            dockRepository.addDockGroup(dockGroup);
            if (dockRepository.currentDockGroup == null) {
                dockRepository.currentDockGroup = dockGroup;
            }
        }
    }

    public void buildViews(ImInt dockMainId, DockPanel panel) {
        panel.getChildren().clear();
        DockGroup group = dockRepository.currentDockGroup;
        imgui.internal.ImGui.dockBuilderRemoveNode(dockMainId.get());
        imgui.internal.ImGui.dockBuilderAddNode(dockMainId.get(), NO_TAB_BAR_FLAG);
        imgui.internal.ImGui.dockBuilderSetNodeSize(dockMainId.get(), ImGui.getMainViewport().getSize());

        group.center.setDirection(DockPosition.CENTER);
        group.center.setOrigin(null);
        group.center.setOutAtOppositeDir(null);
        group.center.setSplitDir(ImGuiDir.Right);

        createDockSpace(group.center, dockMainId);

        addWindow(group.center, panel);
        List<DockDTO> left = group.left;
        for (int i = 0; i < left.size(); i++) {
            DockDTO dockSpace = left.get(i);
            if (i == 0) {
                dockSpace.setOrigin(group.center);
                dockSpace.setOutAtOppositeDir(group.center);
            } else {
                DockDTO previous = left.get(i - 1);
                dockSpace.setOrigin(previous);
                dockSpace.setOutAtOppositeDir(previous);
            }
            dockSpace.setSplitDir(ImGuiDir.Down);
            dockSpace.setDirection(DockPosition.LEFT);
            createDockSpace(dockSpace, dockMainId);
            addWindow(dockSpace, panel);
        }

        List<DockDTO> right = group.right;
        for (int i = 0; i < right.size(); i++) {
            DockDTO dockSpace = right.get(i);
            if (i == 0) {
                dockSpace.setOrigin(group.center);
                dockSpace.setOutAtOppositeDir(group.center);
            } else {
                DockDTO previous = right.get(i - 1);
                dockSpace.setOrigin(previous);
                dockSpace.setOutAtOppositeDir(previous);
            }
            dockSpace.setSplitDir(ImGuiDir.Down);
            dockSpace.setDirection(DockPosition.RIGHT);
            createDockSpace(dockSpace, dockMainId);
            addWindow(dockSpace, panel);
        }

        List<DockDTO> bottom = group.bottom;
        for (int i = 0, bottomSize = bottom.size(); i < bottomSize; i++) {
            DockDTO dockSpace = bottom.get(i);
            if (i == 0) {
                dockSpace.setOrigin(null);
                dockSpace.setOutAtOppositeDir(null);
                dockSpace.setSplitDir(ImGuiDir.Down);
            } else {
                DockDTO previous = bottom.get(i - 1);
                dockSpace.setOrigin(previous);
                dockSpace.setOutAtOppositeDir(previous);
                dockSpace.setSplitDir(ImGuiDir.Right);
            }
            dockSpace.setDirection(DockPosition.BOTTOM);
            createDockSpace(dockSpace, dockMainId);
            addWindow(dockSpace, panel);
        }

        imgui.internal.ImGui.dockBuilderDockWindow(group.center.getInternalId(), dockMainId.get());
        imgui.internal.ImGui.dockBuilderFinish(dockMainId.get());
        group.isInitialized = true;
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
            panel.appendChild(new DockWrapperPanel((DockWrapperPanel) panel.getChildren().stream().findFirst().orElse(null), d));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public DockGroup getCurrentDockGroup() {
        return dockRepository.currentDockGroup;
    }

    public void prepareForRemoval(DockDTO dock, DockWrapperPanel dockWrapperPanel) {
        dockRepository.dockToRemove = dock;
        dockRepository.dockPanelToRemove = dockWrapperPanel;
    }

    public void updateForRemoval(DockPanel panel) {
        if (dockRepository.dockPanelToRemove != null) {
            switch (dockRepository.dockToRemove.getPosition()) {
                case LEFT: {
                    getCurrentDockGroup().left.remove(dockRepository.dockToRemove);
                }
                case RIGHT: {
                    getCurrentDockGroup().right.remove(dockRepository.dockToRemove);
                }
                case BOTTOM: {
                    getCurrentDockGroup().bottom.remove(dockRepository.dockToRemove);
                }
            }
            getCurrentDockGroup().isInitialized = false;
            panel.getChildren().remove(dockRepository.dockPanelToRemove);
            dockRepository.dockToRemove = null;
            dockRepository.dockPanelToRemove = null;
        }
    }

    public void setCurrentDockGroup(DockGroup last) {
        dockRepository.currentDockGroup = last;
    }
}
