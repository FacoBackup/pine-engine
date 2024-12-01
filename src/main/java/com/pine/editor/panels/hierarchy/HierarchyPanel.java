package com.pine.editor.panels.hierarchy;

import com.pine.common.Icons;
import com.pine.common.injection.PInject;
import com.pine.editor.core.UIUtil;
import com.pine.editor.panels.AbstractEntityViewPanel;
import com.pine.editor.repository.EditorRepository;
import com.pine.editor.service.ThemeService;
import com.pine.engine.component.AbstractComponent;
import com.pine.engine.component.Entity;
import com.pine.engine.repository.WorldRepository;
import com.pine.engine.service.request.HierarchyRequest;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.*;
import imgui.type.ImString;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.pine.editor.panels.console.ConsolePanel.TABLE_FLAGS;


public class HierarchyPanel extends AbstractEntityViewPanel {
    private static final byte BYTE = 1;
    private static final ImVec4 TRANSPARENT = new ImVec4(0, 0, 0, 0);
    private static final ImVec2 PADDING = new ImVec2(0, 0);

    private final ImVec4 rowColor = new ImVec4(0, 0, 0, 1);
    private HierarchyHeaderPanel header;
    private Entity onDrag;
    private final ImString search = new ImString();
    private boolean isOnSearch = false;
    private final Map<String, String> searchMatchWith = new HashMap<>();
    private final Map<String, Byte> searchMatch = new HashMap<>();
    private final Map<String, Integer> opened = new HashMap<>();

    @PInject
    public ThemeService theme;

    @PInject
    public EditorRepository editorRepository;

    @Override
    public void onInitialize() {
        appendChild(header = new HierarchyHeaderPanel(search));
    }

    @Override
    public void render() {
        isOnSearch = search.isNotEmpty();
        hotKeys();

        header.render();
        if (ImGui.beginTable("##hierarchy" + imguiId, 3, TABLE_FLAGS)) {
            ImGui.tableSetupColumn("Name", ImGuiTableColumnFlags.NoHide);
            ImGui.tableSetupColumn(Icons.visibility, ImGuiTableColumnFlags.WidthFixed, 20f);
            ImGui.tableSetupColumn(Icons.lock, ImGuiTableColumnFlags.WidthFixed, 20f);
            ImGui.tableHeadersRow();

            for (String pinned : stateRepository.pinnedEntities.keySet()) {
                renderNodePinned(world.entityMap.get(pinned));
            }
            renderNode(WorldRepository.ROOT_ID);
            ImGui.endTable();
        }
    }

    private void renderNodePinned(Entity node) {
        ImGui.tableNextRow();
        ImGui.tableNextColumn();
        if (stateRepository.selected.containsKey(node.id())) {
            ImGui.textColored(stateRepository.accent, getNodeLabel(node, false));
        } else {
            ImGui.text(getNodeLabel(node, false));
        }
        renderEntityColumns(node, true);
    }

    private boolean renderNode(String entityId) {
        Entity entity = world.entityMap.get(entityId);
        if (entity == null || (isOnSearch && searchMatch.containsKey(entity.id()) && Objects.equals(searchMatchWith.get(entity.id()), search.get()))) {
            return false;
        }

        boolean isSearchMatch = matchSearch(entity);
        ImGui.tableNextRow();
        if (stateRepository.selected.containsKey(entityId)) {
            ImGui.tableSetBgColor(ImGuiTableBgTarget.RowBg0, editorRepository.accentU32);
            ImGui.tableSetBgColor(ImGuiTableBgTarget.RowBg1, editorRepository.accentU32);
        }
        ImGui.tableNextColumn();
        int flags = getFlags(entity);
        boolean open;

        if (world.culled.containsKey(entityId) || world.hiddenEntities.containsKey(entityId)) {
            ImGui.pushStyleColor(ImGuiCol.Text, theme.textDisabled);
            open = isOpen(entity, flags);
            ImGui.popStyleColor();
        } else {
            if (entity.isContainer()) {
                rowColor.x = entity.color.x;
                rowColor.y = entity.color.y;
                rowColor.z = entity.color.z;
                ImGui.pushStyleColor(ImGuiCol.Text, rowColor);
                open = isOpen(entity, flags);
                ImGui.popStyleColor();
            } else {
                open = isOpen(entity, flags);
            }
        }

        if (!Objects.equals(entity.id(), WorldRepository.ROOT_ID)) {
            handleDragDrop(entity);
            renderEntityColumns(entity, false);
        }

        if (open) {
            opened.put(entity.id(), ImGuiTreeNodeFlags.DefaultOpen);
            renderEntityChildren(entity);
        } else {
            opened.put(entity.id(), ImGuiTreeNodeFlags.None);
        }

        return isSearchMatch;
    }

    private boolean isOpen(Entity entity, int flags) {
        boolean open;
        open = ImGui.treeNodeEx(getNodeLabel(entity, true), flags);
        return open;
    }

    private @NotNull String getNodeLabel(Entity node, boolean addId) {
        return (node.isContainer() ? Icons.inventory_2 : Icons.view_in_ar) + node.name + (addId ? ("##" + node.id() + imguiId) : "");
    }

    private boolean matchSearch(Entity node) {
        boolean isSearchMatch = false;
        if (isOnSearch) {
            isSearchMatch = node.name.contains(search.get());
            if (isSearchMatch) {
                searchMatch.put(node.id(), BYTE);
            } else {
                searchMatch.remove(node.id());
            }
            searchMatchWith.put(node.id(), search.get());
        } else {
            searchMatch.remove(node.id());
            searchMatchWith.remove(node.id());
        }
        return isSearchMatch;
    }

    private void renderEntityChildren(Entity node) {
        var children = world.parentChildren.get(node.id());

        if (isOnSearch) {
            if (children != null) {
                for (var child : children) {
                    if (searchMatch.containsKey(node.id()) || renderNode(child)) {
                        searchMatch.put(node.id(), BYTE);
                    } else {
                        searchMatch.remove(node.id());
                    }
                }
            }
        } else {
            renderComponents(node);
            if (children != null) {
                for (var child : children) {
                    renderNode(child);
                }
            }
        }

        ImGui.treePop();
    }

    private void renderComponents(Entity node) {
        if (!stateRepository.showOnlyEntitiesHierarchy) {
            world.runByComponent(this::addComponent, node.id());
        }
    }

    private void addComponent(AbstractComponent component) {
        ImGui.tableNextRow();
        ImGui.tableNextColumn();
        ImGui.textDisabled(component.getIcon() + component.getTitle());
        ImGui.tableNextColumn();
        ImGui.textDisabled("--");
        ImGui.tableNextColumn();
        ImGui.textDisabled("--");
    }

    private int getFlags(Entity node) {
        int flags = ImGuiTreeNodeFlags.SpanFullWidth;
        if (isOnSearch) {
            flags |= ImGuiTreeNodeFlags.DefaultOpen;
        }
        return flags | opened.getOrDefault(node.id(), ImGuiTreeNodeFlags.None);
    }

    private void renderEntityColumns(Entity node, boolean isPinned) {
        handleClick(node);
        ImGui.tableNextColumn();

        ImGui.pushStyleColor(ImGuiCol.Button, TRANSPARENT);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, PADDING);
        ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, 0);

        boolean isVisible = !world.hiddenEntities.containsKey(node.id());
        if (ImGui.button((isVisible ? Icons.visibility : Icons.visibility_off) + (isPinned ? "##vpinned" : "##v") + node.id() + imguiId, 20, 15)) {
            changeVisibilityRecursively(node.id(), !isVisible);
        }
        ImGui.tableNextColumn();
        boolean isNodePinned = stateRepository.pinnedEntities.containsKey(node.id());
        if (ImGui.button(((isNodePinned ? Icons.lock : Icons.lock_open) + (isPinned ? "##ppinned" : "##p") + node.id()) + imguiId, 20, 15)) {
            if (isNodePinned) {
                stateRepository.pinnedEntities.remove(node.id());
            } else {
                stateRepository.pinnedEntities.put(node.id(), true);
            }
        }
        ImGui.popStyleColor();
        ImGui.popStyleVar(2);
    }

    private void changeVisibilityRecursively(String node, boolean isVisible) {
        if (isVisible) {
            world.hiddenEntities.remove(node);
        } else {
            world.hiddenEntities.put(node, true);
        }
        var children = world.parentChildren.get(node);

        if (children != null) {
            for (var child : children) {
                changeVisibilityRecursively(child, isVisible);
            }
        }
    }

    private void handleClick(Entity node) {
        if (ImGui.isItemClicked()) {
            boolean isMultiSelect = ImGui.isKeyDown(ImGuiKey.LeftCtrl);
            if (!isMultiSelect) {
                selectionService.clearSelection();
            }
            selectionService.addSelected(node.id());
        }
    }

    private void handleDragDrop(Entity target) {
        if (ImGui.beginDragDropSource()) {
            ImGui.setDragDropPayload(id, id);
            this.onDrag = target;
            ImGui.text("Dragging Node " + target.getTitle());
            ImGui.endDragDropSource();
        }

        if (ImGui.beginDragDropTarget()) {
            if (Objects.equals(ImGui.acceptDragDropPayload(id), id)) {
                requestProcessingService.addRequest(new HierarchyRequest(target, onDrag));
            }
            ImGui.endDragDropTarget();
        }
    }
}
