package com.pine.app.core.window;

import com.pine.app.core.ui.panel.AbstractPanel;
import imgui.ImGuiStyle;
import imgui.ImGuiViewport;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.*;
import imgui.type.ImBoolean;
import imgui.type.ImInt;

import java.util.Collections;
import java.util.List;

public class DockPanel extends AbstractPanel {
    private static final ImBoolean OPEN = new ImBoolean(true);
    private static final int FLAGS = ImGuiWindowFlags.MenuBar |
            ImGuiWindowFlags.NoDocking |
            ImGuiWindowFlags.NoTitleBar |
            ImGuiWindowFlags.NoCollapse |
            ImGuiWindowFlags.NoResize |
            ImGuiWindowFlags.NoMove |
            ImGuiWindowFlags.NoBringToFrontOnFocus |
            ImGuiWindowFlags.NoNavFocus;
    private static final String NAME = "##main_window";
    private static final ImVec2 CENTER = new ImVec2(0.0f, 0.0f);
    private final ImInt dockMainId = new ImInt();
    private final ImInt dockRightId = new ImInt();
    private final ImInt dockRightDownId = new ImInt();
    private final ImInt dockDownId = new ImInt();
    private final ImInt dockDownRightId = new ImInt();
    private boolean isInitialized = false;
    private List<DockDTO> dockSpaces = Collections.emptyList();

    @Override
    public void renderInternal() {
        ImGuiStyle style = ImGui.getStyle();

        final ImGuiViewport viewport = ImGui.getMainViewport();
        final float offset_y = 0;

        ImGui.setNextWindowPos(new ImVec2(viewport.getPos().x, viewport.getPos().y - offset_y));
        ImGui.setNextWindowSize(new ImVec2(viewport.getSize().x, viewport.getSize().y - offset_y));
        ImGui.setNextWindowViewport(viewport.getID());

        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, new ImVec2(0.0f, 0.0f));
        ImGui.setNextWindowBgAlpha(0.0f);

        ImGui.begin(NAME, OPEN, FLAGS);

        ImGui.popStyleVar(3);
        int windowId = ImGui.getID(NAME);
        buildViews(windowId);

        ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, 0.0f);
        ImGui.dockSpace(windowId, CENTER, ImGuiDockNodeFlags.PassthruCentralNode);
        ImGui.popStyleVar();

        super.renderInternal();

        ImGui.end();
    }

    private void buildViews(int windowId) {

        if (!isInitialized) {
            isInitialized = true;
            dockMainId.set(windowId);

            // reset current docking state
            imgui.internal.ImGui.dockBuilderRemoveNode(dockMainId.get());
            imgui.internal.ImGui.dockBuilderAddNode(dockMainId.get(), ImGuiDockNodeFlags.None);
            imgui.internal.ImGui.dockBuilderSetNodeSize(dockMainId.get(), document.getViewportDimensions());

            dockSpaces.forEach(d -> {
                int origin = dockMainId.get();
                if (d.getOrigin() != null) {
                    origin = d.getOrigin().getNodeId().get();
                }
                ImInt target = dockMainId;
                if (d.getOutAtOppositeDir() != null) {
                    target = d.getOutAtOppositeDir().getNodeId();
                }

                d.getNodeId().set(imgui.internal.ImGui.dockBuilderSplitNode(origin, d.getSplitDir(), d.getSizeRatioForNodeAtDir(), null, target));
            });

            dockSpaces.forEach(d -> {
                imgui.internal.ImGui.dockBuilderDockWindow(d.getName(), d.getNodeId().get());
            });

            imgui.internal.ImGui.dockBuilderDockWindow("Viewport", dockMainId.get());
            imgui.internal.ImGui.dockBuilderFinish(dockMainId.get());
        }

    }

    public void createDockSpaces(List<DockDTO> dockSpaces) {
        this.dockSpaces = dockSpaces;
    }
}
