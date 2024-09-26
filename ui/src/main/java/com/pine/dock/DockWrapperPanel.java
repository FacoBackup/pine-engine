package com.pine.dock;

import com.pine.Loggable;
import com.pine.view.AbstractView;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Vector2f;

public final class DockWrapperPanel extends AbstractView implements Loggable {
    private static final int FLAGS = ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.MenuBar;
    private static final ImVec2 DEFAULT = new ImVec2(-1, -1);
    private static final ImVec2 MAX_SIZE = new ImVec2(Float.MAX_VALUE, Float.MAX_VALUE);
    private static final ImVec2 PIVOT = new ImVec2(0.5f, 0.5f);
    public static final float FRAME_SIZE = 25;
    private static final ImVec2 MIN_SIZE = new ImVec2(300, 300);

    private final ImVec2 initialSize = DEFAULT.clone();
    public final ImVec2 padding = DEFAULT.clone();
    public final ImVec2 position = DEFAULT.clone();
    public final Vector2f size = new Vector2f();
    private final ImVec2 sizeInternal = DEFAULT.clone();
    private int stylePushCount;

    private final DockWrapperPanel mainWindow;
    private final DockDTO dock;
    private AbstractDockPanel view;

    public DockWrapperPanel(DockWrapperPanel mainWindow, DockDTO dock) {
        this.mainWindow = mainWindow;
        this.dock = dock;
        padding.set(dock.getDescription().getPaddingX(), dock.getDescription().getPaddingY());
    }

    @Override
    public void onInitialize() {
        try {
            view = appendChild(dock.getView().getConstructor().newInstance());
            view.setSize(size);
            view.setPosition(position);
        } catch (Exception e) {
            getLogger().error(e.getMessage(), e);
        }
    }

    @Override
    public void render() {
        tick();
        if (!visible) {
            return;
        }

        if (!initialSize.equals(DEFAULT)) {
            ImGui.setNextWindowSize(initialSize, ImGuiCond.FirstUseEver);
        }

        ImGui.setNextWindowSizeConstraints(MIN_SIZE, MAX_SIZE);

        if (!padding.equals(DEFAULT)) {
            ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, padding);
            stylePushCount++;
        }

        beforeWindow();
        if (ImGui.begin(dock.getInternalId(), FLAGS)) {
            ImGui.getWindowSize(sizeInternal);
            size.x = sizeInternal.x;
            size.y = sizeInternal.y;
            ImGui.getWindowPos(position);

            renderHeader();
            view.render();

            ImGui.end();
        }

        ImGui.popStyleVar(stylePushCount);
        stylePushCount = 0;
    }

    private void renderHeader() {
        if (ImGui.beginMenuBar()) {
            ImGui.text(dock.getDescription().getIcon());
            ImGui.spacing();
            ImGui.text(dock.getDescription().getTitle());
            ImGui.endMenuBar();
        }
    }

    private void beforeWindow() {
        if (mainWindow != null && mainWindow != this) {
            ImVec2 pos = mainWindow.getPosition();
            ImVec2 sze = mainWindow.getSize();
            ImVec2 center = new ImVec2(pos.x + sze.x * 0.5f, pos.y + sze.y * 0.5f);
            ImGui.setNextWindowPos(center, ImGuiCond.FirstUseEver, PIVOT);
        }
    }

    private ImVec2 getPosition() {
        return position;
    }

    private ImVec2 getSize() {
        return sizeInternal;
    }
}