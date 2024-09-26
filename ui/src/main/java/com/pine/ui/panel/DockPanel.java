package com.pine.ui.panel;

import com.pine.Icon;
import com.pine.PInject;
import com.pine.ui.theme.ThemeRepository;
import com.pine.ui.view.AbstractView;
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
    private static final int NO_TAB_BAR_FLAG = 1 << 12;
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
    public ThemeRepository themeRepository;

    @Override
    public void renderInternal() {
        final ImGuiViewport viewport = ImGui.getMainViewport();

        setupPosition(viewport);
        setupStyles();
        ImGui.begin(NAME, OPEN, FLAGS);
        ImGui.popStyleVar(3);

        // TODO configurable values
        if (ImGui.beginMenuBar()) {
            if (ImGui.beginMenu("File")) {
                if (ImGui.menuItem("New")) { /* Action for New */ }
                if (ImGui.menuItem("Open")) { /* Action for Open */ }
                if (ImGui.menuItem("Save")) { /* Action for Save */ }
                if (ImGui.menuItem("Exit")) { /* Action for Exit */ }
                ImGui.endMenu();
            }

            if (ImGui.beginMenu("Edit")) {
                if (ImGui.menuItem("Undo")) { /* Action for Undo */ }
                if (ImGui.menuItem("Redo")) { /* Action for Redo */ }
                ImGui.endMenu();
            }

            if (ImGui.button(themeRepository.isDarkMode ? Icon.MOON.codePoint : Icon.LIGHTBULB.codePoint, 27, 27)) {
                themeRepository.isDarkMode = !themeRepository.isDarkMode;
            }
            ImGui.endMenuBar();
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

            imgui.internal.ImGui.dockBuilderRemoveNode(dockMainId.get());
            imgui.internal.ImGui.dockBuilderAddNode(dockMainId.get(), NO_TAB_BAR_FLAG);
            imgui.internal.ImGui.dockBuilderSetNodeSize(dockMainId.get(), document.getViewportDimensions());

            for (DockDTO dockSpace : dockSpaces) {
                createDockSpace(dockSpace);
            }

            for (DockDTO d : dockSpaces) {
                addWindow(d);
            }

            imgui.internal.ImGui.dockBuilderDockWindow("Viewport", dockMainId.get());
            imgui.internal.ImGui.dockBuilderFinish(dockMainId.get());
        }
    }

    private void createDockSpace(DockDTO dockSpace) {
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

    private void addWindow(DockDTO d) {
        try {
            imgui.internal.ImGui.dockBuilderDockWindow(d.getName(), d.getNodeId().get());
            if (d.getBodyPanelClass() != null) {
                AbstractWindowPanel child = d.getBodyPanelClass().getConstructor().newInstance();
                appendChild(child);
                d.setPanelInstance(child);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void initializeDockSpaces(List<DockDTO> dockSpaces) {
        this.dockSpaces.forEach(d -> {
            if (d.getPanel() != null) {
                removeChild(d.getPanel());
            }
        });
        this.dockSpaces = dockSpaces;
    }
}
