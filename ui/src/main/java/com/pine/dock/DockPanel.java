package com.pine.dock;

import com.pine.PInject;
import com.pine.view.View;
import com.pine.view.AbstractView;
import imgui.ImGui;
import imgui.ImGuiViewport;
import imgui.ImVec2;
import imgui.flag.ImGuiDockNodeFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.internal.ImGuiDockNode;
import imgui.type.ImBoolean;
import imgui.type.ImInt;

import java.util.Collections;
import java.util.List;

public class DockPanel extends AbstractView {
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
    private boolean isInitialized = false;
    private List<DockDTO> dockSpaces = Collections.emptyList();

    @PInject
    public DockService dockService;

    private View headerView;

    @Override
    public void renderInternal() {
        final ImGuiViewport viewport = ImGui.getMainViewport();

        setupPosition(viewport);
        setupStyles();
        ImGui.begin(NAME, OPEN, FLAGS);
        ImGui.popStyleVar(3);

        if (headerView != null) {
            if (ImGui.beginMenuBar()) {
                headerView.render();
                ImGui.endMenuBar();
            }
        }

        renderView();
        ImGui.end();
    }

    private static void setupPosition(ImGuiViewport viewport) {
        ImGui.setNextWindowPos(new ImVec2(viewport.getPos().x, viewport.getPos().y));
        ImGui.setNextWindowSize(new ImVec2(viewport.getSize().x, viewport.getSize().y));
        ImGui.setNextWindowViewport(viewport.getID());
    }

    private static void setupStyles() {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, new ImVec2(0.0f, 0.0f));
    }

    private void renderView() {
        int windowId = ImGui.getID(NAME);
        buildViews(windowId);

        ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, 0.0f);
        ImGui.dockSpace(windowId, CENTER, ImGuiDockNodeFlags.PassthruCentralNode);
        ImGui.popStyleVar(1);

        super.renderInternal();
    }

    private void buildViews(int windowId) {
        if (!isInitialized) {
            isInitialized = true;
            dockMainId.set(windowId);
            dockService.buildViews(dockSpaces, dockMainId, this);
        }
    }

    public void setHeader(View view) {
        if (view != null) {
            headerView = appendChild(view);
            removeChild(headerView);
        }
    }

    public void setDockSpaces(List<DockDTO> dockSpaces) {
        this.dockSpaces.forEach(d -> {
            if (d.getPanel() != null) {
                removeChild(d.getPanel());
            }
        });
        this.dockSpaces = dockSpaces;
    }
}
