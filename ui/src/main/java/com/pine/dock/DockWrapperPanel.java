package com.pine.dock;

import com.pine.Loggable;
import com.pine.PInject;
import com.pine.theme.Icons;
import com.pine.view.AbstractView;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiDir;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import org.joml.Vector2f;

import java.io.Serializable;

import static com.pine.theme.Icons.ONLY_ICON_BUTTON_SIZE;

public final class DockWrapperPanel extends AbstractView implements Loggable, Serializable {
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
    private final boolean isDownDirection;
    private int stylePushCount;

    private final DockWrapperPanel mainWindow;
    private final DockDTO dock;
    private AbstractDockPanel view;
    private final ImVec2 headerPadding = new ImVec2(0, 3);

    @PInject
    public DockService dockService;
    private boolean isNotFirstDockSpace;

    public DockWrapperPanel(DockWrapperPanel mainWindow, DockDTO dock) {
        this.mainWindow = mainWindow;
        this.dock = dock;
        this.initialSize.set(dock.getSizeX(), dock.getSizeY());
        padding.set(dock.getDescription().getPaddingX(), dock.getDescription().getPaddingY());
        isDownDirection = dock.getSplitDir() == ImGuiDir.Down && dock.getOrigin() != null;
    }

    @Override
    public void onInitialize() {
        initializeView();
        isNotFirstDockSpace = dockService.getCurrentDockGroup().docks.getFirst() != dock;
    }

    private void initializeView() {
        try {
            children.clear();
            view = dock.getDescription().getView().getConstructor().newInstance();
            view.setSize(size);
            view.setPosition(position);
            appendChild(view);

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

            dock.setSizeX(size.x);
            dock.setSizeY(size.y);

            ImGui.getWindowPos(position);

            renderHeader();
            view.render();
        }
        ImGui.end();

        ImGui.popStyleVar(stylePushCount);
        stylePushCount = 0;
    }

    private void renderHeader() {
        headerPadding.x = ImGui.getStyle().getFramePaddingX();

        if (ImGui.beginMenuBar()) {
            String[] options = dock.getDescription().getOptions();
            ImInt selected = dock.selectedOption();
            ImGui.setNextItemWidth(ImGui.calcTextSizeX(options[selected.get()]) + 30);
            ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, headerPadding);
            if (ImGui.combo(imguiId, selected, options)) {
                dock.setDescription(dock.getDescription().getSelectedOption(selected.get()));
                initializeView();
            }
            ImGui.popStyleVar();

            ImGui.dummy(ImGui.getContentRegionAvailX() - (isNotFirstDockSpace ? 55 : 35), 0);
            if (ImGui.button((isDownDirection ? Icons.horizontal_split : Icons.vertical_split) + "##splitView" + imguiId, ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE)) {
                DockDTO dto = new DockDTO(dock.getDescription().getDefault());
                dto.setOrigin(dock);
                dto.setSplitDir(isDownDirection ? ImGuiDir.Down : ImGuiDir.Right);
                dto.setSizeRatioForNodeAtDir(.5f);
                dto.setOutAtOppositeDir(dock);

                dockService.getCurrentDockGroup().docks.add(dto);
                dockService.getCurrentDockGroup().isInitialized = false;
            }

            if (isNotFirstDockSpace) {
                if (ImGui.button(Icons.close + "##removeView" + imguiId, ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE)) {
                    dockService.prepareForRemoval(dock, this);
                }
            }

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
