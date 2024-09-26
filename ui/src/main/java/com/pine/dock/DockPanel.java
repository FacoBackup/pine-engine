package com.pine.dock;

import com.pine.PInject;
import com.pine.theme.Icon;
import com.pine.view.View;
import com.pine.view.AbstractView;
import imgui.ImGui;
import imgui.ImGuiViewport;
import imgui.ImVec2;
import imgui.flag.ImGuiDockNodeFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImInt;

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
    @PInject
    public DockService dockService;

    private View headerView;

    @Override
    public void renderInternal() {
        final ImGuiViewport viewport = ImGui.getMainViewport();

        setupPosition(viewport);
        setupStyles();
        ImGui.begin(NAME, OPEN, FLAGS);
        int windowId = ImGui.getID(NAME);
        dockMainId.set(windowId);

        ImGui.popStyleVar(3);

        if (ImGui.beginMenuBar()) {
            if (headerView != null) {
                headerView.render();
            }
            for(var dockGroup : dockService.getDockGroups()){
                if(ImGui.button(dockGroup.getTitleWithId())){
                    dockService.switchDockGroups(dockGroup, dockMainId);
                }
            }
            if(ImGui.button(Icon.PLUS.codePoint + "##addDock")){
                dockService.createDockGroup();
            }
            ImGui.endMenuBar();
        }

        if(!dockService.getCurrentDockGroup().isInitialized) {
            dockService.buildViews(dockMainId, this);
        }

        ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, 0.0f);
        ImGui.dockSpace(windowId, CENTER, ImGuiDockNodeFlags.PassthruCentralNode);
        ImGui.popStyleVar(1);

        super.renderInternal();
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



    public void setHeader(View view) {
        if (view != null) {
            headerView = appendChild(view);
            removeChild(headerView);
        }
    }
}
