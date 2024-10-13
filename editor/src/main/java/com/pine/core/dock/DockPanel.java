package com.pine.core.dock;

import com.pine.core.view.AbstractView;
import com.pine.injection.PInject;
import imgui.ImGui;
import imgui.ImGuiViewport;
import imgui.ImVec2;
import imgui.flag.ImGuiDockNodeFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImInt;


public class DockPanel extends AbstractView {
    public static final ImBoolean OPEN = new ImBoolean(true);
    public static final int FLAGS = ImGuiWindowFlags.NoDocking |
            ImGuiWindowFlags.NoTitleBar |
            ImGuiWindowFlags.NoCollapse |
            ImGuiWindowFlags.NoResize |
            ImGuiWindowFlags.NoMove |
            ImGuiWindowFlags.NoBringToFrontOnFocus |
            ImGuiWindowFlags.NoNavFocus;
    private static final String NAME = "##main_window";
    private static final ImVec2 CENTER = new ImVec2(0.0f, 0.0f);
    private final ImInt dockMainId = new ImInt();
    private static final float HEADER_HEIGHT = 62;

    @PInject
    public DockService dockService;

    private AbstractView headerView;

    @Override
    public void render() {
        ImGuiViewport viewport = ImGui.getMainViewport();
        DockGroup group = dockService.getCurrentDockGroup();
        if (group == null) {
            return;
        }

        renderHeader(viewport);

        dockService.updateForRemoval(this);
        beginMainWindowSetup(viewport);
        ImGui.begin(NAME, OPEN, FLAGS);

        int windowId = ImGui.getID(NAME);
        dockMainId.set(windowId);

        endMainWindowSetup();

        if (!group.isInitialized) {
            dockService.buildViews(dockMainId, this);
        }

        ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, 0.0f);
        ImGui.dockSpace(windowId, CENTER, ImGuiDockNodeFlags.PassthruCentralNode);
        ImGui.popStyleVar(1);

        super.render();
        ImGui.end();
    }

    private void renderHeader(ImGuiViewport viewport) {
        ImGui.setNextWindowPos(viewport.getPosX(), 0);
        ImGui.setNextWindowSize(viewport.getSizeX(), HEADER_HEIGHT);
        windowStyle();
        ImGui.begin(NAME + "2", OPEN, FLAGS | ImGuiWindowFlags.NoScrollbar);
        endMainWindowSetup();

        headerView.render();
        ImGui.end();
    }

    private void beginMainWindowSetup(ImGuiViewport viewport) {
        ImGui.setNextWindowPos(viewport.getPos().x, viewport.getPos().y + HEADER_HEIGHT);
        ImGui.setNextWindowSize(viewport.getSize().x, viewport.getSize().y - HEADER_HEIGHT);
        ImGui.setNextWindowViewport(viewport.getID());

        windowStyle();
    }

    private static void windowStyle() {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, new ImVec2(0.0f, 0.0f));
    }

    public static void endMainWindowSetup() {
        ImGui.popStyleVar(3);
    }

    public void setHeader(AbstractView view) {
        if (view != null) {
            headerView = appendChild(view);
            removeChild(headerView);
        }
    }
}
