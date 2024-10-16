package com.pine.core.dock;

import com.pine.core.view.AbstractView;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.messaging.MessageRepository;
import com.pine.messaging.MessageSeverity;
import com.pine.theme.Icons;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.*;
import imgui.type.ImInt;
import org.joml.Vector2f;

import java.io.Serializable;

import static com.pine.core.dock.DockPanel.OPEN;
import static com.pine.theme.Icons.ONLY_ICON_BUTTON_SIZE;

public final class DockWrapperPanel extends AbstractView implements Loggable, Serializable {
    private static final int FLAGS = ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.MenuBar;
    private static final ImVec2 DEFAULT = new ImVec2(-1, -1);
    private static final ImVec2 MAX_SIZE = new ImVec2(Float.MAX_VALUE, Float.MAX_VALUE);
    private static final ImVec2 PIVOT = new ImVec2(0.5f, 0.5f);
    public static final float FRAME_SIZE = 25;
    private static final ImVec2 MIN_SIZE = new ImVec2(300, 300);

    public final ImVec2 padding = DEFAULT.clone();
    public final ImVec2 position = DEFAULT.clone();
    public final Vector2f size = new Vector2f();
    private final ImVec2 sizeInternal = DEFAULT.clone();
    private final boolean isDownDirection;
    private boolean sizeInitialized = false;
    private int stylePushCount;

    private final DockWrapperPanel mainWindow;
    private final DockDTO dock;
    private AbstractDockPanel view;
    private final ImVec2 headerPadding = new ImVec2(0, 3);

    @PInject
    public DockService dockService;

    @PInject
    public MessageRepository messageRepository;

    private boolean isNotCenter;

    public DockWrapperPanel(DockWrapperPanel mainWindow, DockDTO dock) {
        this.mainWindow = mainWindow;
        this.dock = dock;
        padding.set(dock.getDescription().getPaddingX(), dock.getDescription().getPaddingY());
        isDownDirection = dock.getSplitDir() == ImGuiDir.Down && dock.getOrigin() != null;
    }

    @Override
    public void onInitialize() {
        initializeView();
        isNotCenter = dock.getPosition() != DockPosition.CENTER;
    }

    private void initializeView() {
        try {
            children.clear();
            view = dock.getDescription().getView().getConstructor().newInstance();
            view.setSize(size);
            view.setPosition(position);
            setContext(dock.getContext());
            appendChild(view);
        } catch (Exception e) {
            getLogger().error(e.getMessage(), e);
        }
    }

    @Override
    public void render() {
        ImGui.setNextWindowSizeConstraints(MIN_SIZE, MAX_SIZE);
        if (!padding.equals(DEFAULT)) {
            ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, padding);
            stylePushCount++;
        }
        if (!sizeInitialized && dock.getSizeX() > 0 && dock.getSizeY() > 0) {
            ImGui.setNextWindowSize(dock.getSizeX(), dock.getSizeY());
            sizeInitialized = true;
        }
        beforeWindow();
        if (ImGui.begin(dock.getInternalId(), OPEN, FLAGS)) {
            view.isWindowFocused = ImGui.isWindowFocused(ImGuiFocusedFlags.RootAndChildWindows);
            ImGui.getWindowSize(sizeInternal);
            size.x = sizeInternal.x;
            size.y = sizeInternal.y;

            dock.setSizeX(size.x);
            dock.setSizeY(size.y);

            ImGui.getWindowPos(position);
            renderHeader();
            view.render();
            ImGui.end();
        }

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

            if (isNotCenter) {
                ImGui.dummy(ImGui.getContentRegionAvailX() - 55, 0);
                if (ImGui.button((isDownDirection ? Icons.horizontal_split : Icons.vertical_split) + "##splitView" + imguiId, ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE)) {
                    try {
                        DockDTO dto = new DockDTO(dock.getDescription().getDefault());
                        dto.setOrigin(dock);
                        dto.setSplitDir(isDownDirection ? ImGuiDir.Down : ImGuiDir.Right);
                        dto.setSizeRatioForNodeAtDir(.5f);
                        dto.setOutAtOppositeDir(dock);
                        DockGroup group = dockService.getCurrentDockGroup();
                        switch (dock.getPosition()) {
                            case LEFT -> group.left.add(group.left.indexOf(dock) + 1, dto);
                            case RIGHT -> group.right.add(group.right.indexOf(dock) + 1, dto);
                            case BOTTOM -> group.bottom.add(group.bottom.indexOf(dock) + 1, dto);
                        }
                        group.isInitialized = false;
                        messageRepository.pushMessage("Dock space created", MessageSeverity.SUCCESS);
                    } catch (Exception e) {
                        getLogger().error(e.getMessage(), e);
                        messageRepository.pushMessage("Error while creating dock space", MessageSeverity.ERROR);
                    }
                }

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
