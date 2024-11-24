package com.pine.editor.core.dock;

import com.pine.common.injection.PBean;
import com.pine.common.injection.PInject;
import com.pine.editor.core.AbstractView;
import com.pine.editor.core.AbstractWindow;
import imgui.ImGui;
import imgui.flag.ImGuiDir;
import imgui.internal.ImGuiDockNode;
import imgui.type.ImInt;

import java.util.ArrayList;
import java.util.List;

@PBean
public class DockService {
    private static final int NO_TAB_BAR_FLAG = 1 << 12;

    @PInject
    public DockRepository dockRepository;

    public void buildViews(ImInt dockMainId, AbstractWindow panel) {
        if (dockRepository.isInitialized || dockRepository.center == null) {
            return;
        }

        List<AbstractView> toKeep = new ArrayList<>();
        panel.getChildren().forEach(child -> {
            if(!(child instanceof DockSpacePanel)) {
                toKeep.add(child);
            }
        });
        panel.getChildren().clear();
        panel.getChildren().addAll(toKeep);

        imgui.internal.ImGui.dockBuilderRemoveNode(dockMainId.get());
        imgui.internal.ImGui.dockBuilderAddNode(dockMainId.get(), NO_TAB_BAR_FLAG);
        imgui.internal.ImGui.dockBuilderSetNodeSize(dockMainId.get(), ImGui.getMainViewport().getSize());

        dockRepository.center.setDirection(DockPosition.CENTER);
        dockRepository.center.setOrigin(null);
        dockRepository.center.setOutAtOppositeDir(null);
        dockRepository.center.setSplitDir(ImGuiDir.Right);

        createDockSpace(dockRepository.center, dockMainId);

        addWindow(dockRepository.center, panel);
        List<DockDTO> left = dockRepository.left;
        for (int i = 0; i < left.size(); i++) {
            DockDTO dockSpace = left.get(i);
            if (i == 0) {
                dockSpace.setOrigin(dockRepository.center);
                dockSpace.setOutAtOppositeDir(dockRepository.center);
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

        List<DockDTO> right = dockRepository.right;
        for (int i = 0; i < right.size(); i++) {
            DockDTO dockSpace = right.get(i);
            if (i == 0) {
                dockSpace.setOrigin(dockRepository.center);
                dockSpace.setOutAtOppositeDir(dockRepository.center);
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

        List<DockDTO> bottom = dockRepository.bottom;
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

        imgui.internal.ImGui.dockBuilderDockWindow(dockRepository.center.getInternalId(), dockMainId.get());
        imgui.internal.ImGui.dockBuilderFinish(dockMainId.get());
        dockRepository.isInitialized = true;
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

    private void addWindow(DockDTO d, AbstractWindow panel) {
        try {
            imgui.internal.ImGui.dockBuilderDockWindow(d.getInternalId(), d.getNodeId().get());
            panel.appendChild(new DockSpacePanel((DockSpacePanel) panel.getChildren().stream().filter(e -> e instanceof DockSpacePanel).findFirst().orElse(null), d));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void prepareForRemoval(DockDTO dock, DockSpacePanel dockSpacePanel) {
        dockRepository.dockToRemove = dock;
        dockRepository.dockPanelToRemove = dockSpacePanel;
    }

    public void updateForRemoval(AbstractWindow panel) {
        if (dockRepository.dockPanelToRemove != null) {
            switch (dockRepository.dockToRemove.getPosition()) {
                case LEFT: {
                    dockRepository.left.remove(dockRepository.dockToRemove);
                }
                case RIGHT: {
                    dockRepository.right.remove(dockRepository.dockToRemove);
                }
                case BOTTOM: {
                    dockRepository.bottom.remove(dockRepository.dockToRemove);
                }
            }
            dockRepository.isInitialized = false;
            dockRepository.dockPanelToRemove.getView().onRemove();
            panel.getChildren().remove(dockRepository.dockPanelToRemove);
            dockRepository.dockToRemove = null;
            dockRepository.dockPanelToRemove = null;
        }
    }
}
