package com.pine.app.core.ui.panel;

import com.pine.app.core.ui.View;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.internal.ImGuiWindow;

public abstract class AbstractWindowPanel extends AbstractPanel {
    private static final ImVec2 DEFAULT = new ImVec2(-1, -1);
    private static final ImVec2 DEFAULT_MAX = new ImVec2(Float.MAX_VALUE, Float.MAX_VALUE);
    private static final ImVec2 PIVOT = new ImVec2(0.5f, 0.5f);

    private ImGuiWindow window;
    private final ImVec2 initialSize = DEFAULT.clone();
    private final ImVec2 minSize = DEFAULT.clone();
    private final ImVec2 maxSize = DEFAULT_MAX.clone();
    private final ImVec2 padding = DEFAULT.clone();
    protected final ImVec2 position = DEFAULT.clone();
    protected final ImVec2 size = DEFAULT.clone();
    private int stylePushCount;
    private AbstractWindowPanel mainWindow;

    protected abstract String getTitle();

    @Override
    public void onInitialize() {
        super.onInitialize();
        View first = getParent().getChildren().stream().findFirst().orElse(null);
        if (first instanceof AbstractWindowPanel) {
            mainWindow = (AbstractWindowPanel) first;
        }
    }

    @Override
    protected void renderInternal() {
        if (!initialSize.equals(DEFAULT)) {
            ImGui.setNextWindowSize(initialSize, ImGuiCond.FirstUseEver);
        }

        if (!minSize.equals(DEFAULT) && !maxSize.equals(DEFAULT_MAX)) {
            ImGui.setNextWindowSizeConstraints(minSize, maxSize);
        }

        if (!padding.equals(DEFAULT)) {
            ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, padding);
            stylePushCount++;
        }

        beforeWindow();
        if (ImGui.begin(getTitle(), ImGuiWindowFlags.NoCollapse)) {
            afterWindow();
            window = imgui.internal.ImGui.getCurrentWindow();
            ImGui.getWindowSize(size);
            ImGui.getWindowPos(position);
        }

        if (window != null) {
            super.renderInternal();
        }
        ImGui.end();

        ImGui.popStyleVar(stylePushCount);
        stylePushCount = 0;
    }

    /**
     * is executed immediately after window is opened
     */
    protected void afterWindow(){}

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
        return size;
    }

    public ImGuiWindow getWindow() {
        return window;
    }
}
