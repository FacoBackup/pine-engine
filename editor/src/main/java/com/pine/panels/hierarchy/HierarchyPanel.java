package com.pine.panels.hierarchy;

import com.pine.component.ComponentType;
import com.pine.component.Entity;
import com.pine.component.MeshComponent;
import com.pine.component.TransformationComponent;
import com.pine.panels.AbstractEntityViewPanel;
import com.pine.service.request.HierarchyRequest;
import com.pine.theme.Icons;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.*;
import imgui.type.ImString;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import static com.pine.panels.console.ConsolePanel.TABLE_FLAGS;


public class HierarchyPanel extends AbstractEntityViewPanel {
    private static final byte BYTE = 1;
    private static final ImVec4 TRANSPARENT = new ImVec4(0, 0, 0, 0);
    private static final ImVec2 PADDING = new ImVec2(0, 0);

    private HierarchyHeaderPanel header;
    private Entity onDrag;
    private final ImString search = new ImString();
    private boolean isOnSearch = false;
    private HierarchyContext context;

    @Override
    public void onInitialize() {
        appendChild(header = new HierarchyHeaderPanel(search));
        context = (HierarchyContext) getContext();
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
            renderNode(world.rootEntity.id());
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
        if ((isOnSearch && context.searchMatch.containsKey(entity.id()) && Objects.equals(context.searchMatchWith.get(entity.id()), search.get()))) {
            return false;
        }

        boolean isSearchMatch = matchSearch(entity);
        ImGui.tableNextRow();
        ImGui.tableNextColumn();
        int flags = getFlags(entity);
        boolean open = ImGui.treeNodeEx(getNodeLabel(entity, true), flags);

        if (entity != world.rootEntity) {
            handleDragDrop(entity);
            renderEntityColumns(entity, false);
        }
        if (open) {
            context.opened.put(entity.id(), ImGuiTreeNodeFlags.DefaultOpen);
            renderEntityChildren(entity);
        } else {
            context.opened.put(entity.id(), ImGuiTreeNodeFlags.None);
        }

        return isSearchMatch;
    }

    private @NotNull String getNodeLabel(Entity node, boolean addId) {
        return (world.rootEntity == node ? Icons.inventory_2 : Icons.view_in_ar) + node.getTitle() + (addId ? ("##" + node.id() + imguiId) : "");
    }

    private boolean matchSearch(Entity node) {
        boolean isSearchMatch = false;
        if (isOnSearch) {
            isSearchMatch = node.getTitle().contains(search.get());
            if (isSearchMatch) {
                context.searchMatch.put(node.id(), BYTE);
            } else {
                context.searchMatch.remove(node.id());
            }
            context.searchMatchWith.put(node.id(), search.get());
        } else {
            context.searchMatch.remove(node.id());
            context.searchMatchWith.remove(node.id());
        }
        return isSearchMatch;
    }

    private void renderEntityChildren(Entity node) {
        var children = world.parentChildren.get(node.id());

        if (isOnSearch) {
            if (children != null) {
                for (var child : children) {
                    if (context.searchMatch.containsKey(node.id()) || renderNode(child)) {
                        context.searchMatch.put(node.id(), BYTE);
                    } else {
                        context.searchMatch.remove(node.id());
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
            for (var component : node.components.values()) {
                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                ImGui.textDisabled(component.getIcon() + component.getTitle());
                ImGui.tableNextColumn();
                ImGui.textDisabled("--");
                ImGui.tableNextColumn();
                ImGui.textDisabled("--");
            }
            if (node.components.containsKey(ComponentType.MESH)) {
                MeshComponent meshComponent = (MeshComponent) node.components.get(ComponentType.MESH);
                if (meshComponent.isInstancedRendering) {
                    renderInstancedComponent(meshComponent);
                }
            }
        }
    }

    private void renderInstancedComponent(MeshComponent instanced) {
        List<TransformationComponent> primitives = instanced.instances;
        for (int i = 0, primitivesSize = primitives.size(); i < primitivesSize; i++) {
            TransformationComponent p = primitives.get(i);
            ImGui.tableNextRow();
            ImGui.tableNextColumn();
            String title = Icons.content_copy + " Instance - " + i;
            if (stateRepository.primitiveSelected == p) {
                ImGui.textColored(stateRepository.accent, title);
            } else {
                ImGui.textDisabled(title);
            }
            if (ImGui.isItemClicked()) {
                stateRepository.primitiveSelected = p;
            }
            ImGui.tableNextColumn();
            ImGui.textDisabled("--");
            ImGui.tableNextColumn();
            ImGui.textDisabled("--");
        }
    }

    private int getFlags(Entity node) {
        int flags = ImGuiTreeNodeFlags.SpanFullWidth;
        if (stateRepository.selected.containsKey(node.id())) {
            flags |= ImGuiTreeNodeFlags.Selected;
        }
        if (isOnSearch) {
            flags |= ImGuiTreeNodeFlags.DefaultOpen;
        }
        return flags | context.opened.getOrDefault(node.id(), ImGuiTreeNodeFlags.None);
    }

    private void renderEntityColumns(Entity node, boolean isPinned) {
        handleClick(node);
        ImGui.tableNextColumn();

        ImGui.pushStyleColor(ImGuiCol.Button, TRANSPARENT);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, PADDING);
        ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, 0);

        boolean isVisible = !world.hiddenEntityMap.containsKey(node.id());
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
            world.hiddenEntityMap.remove(node);
        } else {
            world.hiddenEntityMap.put(node, true);
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
            selectionService.addSelected(node);
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

