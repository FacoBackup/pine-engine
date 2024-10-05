package com.pine.dock;

import com.pine.injection.PInject;
import com.pine.theme.Icons;
import com.pine.view.AbstractView;
import com.pine.view.View;
import imgui.ImGui;
import imgui.ImGuiViewport;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiDockNodeFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImInt;

import static com.pine.theme.Icons.ONLY_ICON_BUTTON_SIZE;

public abstract class DockPanel extends AbstractView {
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

    private AbstractDockHeader headerView;

    protected abstract ImVec4 getAccentColor();

    @Override
    public void renderInternal() {
        DockGroup group = dockService.getCurrentDockGroup();
        if(group == null){
            return;
        }
        final ImGuiViewport viewport = ImGui.getMainViewport();
        dockService.updateForRemoval(this);

        setupPosition(viewport);
        setupStyles();
        ImGui.begin(NAME, OPEN, FLAGS);

        int windowId = ImGui.getID(NAME);
        dockMainId.set(windowId);

        ImGui.popStyleVar(3);

        menuBar();

        if (!group.isInitialized) {
            dockService.buildViews(dockMainId, this);
        }

        ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, 0.0f);
        ImGui.dockSpace(windowId, CENTER, ImGuiDockNodeFlags.PassthruCentralNode);
        ImGui.popStyleVar(1);

        super.renderInternal();
        ImGui.end();
    }

    private void menuBar() {
        if (ImGui.beginMenuBar()) {
            if (headerView != null) {
                headerView.begin();
            }
            ImGui.dummy(25, 0);
            for (var dockGroup : dockService.getDockGroups()) {
                boolean isSelected = dockGroup == dockService.getCurrentDockGroup();
                if (isSelected) {
                    ImGui.pushStyleColor(ImGuiCol.Button, getAccentColor());
                }
                if (ImGui.button(Icons.dashboard + dockGroup.getTitleWithId()) && !isSelected) {
                    dockService.switchDockGroups(dockGroup, dockMainId);
                    children.clear();
                }
                if (isSelected) {
                    ImGui.popStyleColor();
                }
            }
            if (dockService.getDockGroups().size() > 1 && ImGui.button(Icons.remove + " Remove selected##removeDockGroup")) {
                dockService.getDockGroups().remove(dockService.getCurrentDockGroup());
                dockService.setCurrentDockGroup(dockService.getDockGroups().getLast());
            }
            if (ImGui.button(Icons.add + "##addDockGroup", ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE)) {
                dockService.createDockGroup();
            }
            if (headerView != null) {
                headerView.end();
            }
            ImGui.endMenuBar();
        }
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

    public void setHeader(AbstractDockHeader view) {
        if (view != null) {
            headerView = appendChild(view);
            removeChild(headerView);
        }
    }
}
