package com.pine.panels.hierarchy;

import com.pine.PInject;
import com.pine.component.Entity;
import com.pine.component.EntityComponent;
import com.pine.component.InstancedPrimitiveComponent;
import com.pine.component.Transformation;
import com.pine.dock.AbstractDockPanel;
import com.pine.repository.EditorStateRepository;
import com.pine.repository.WorldRepository;
import com.pine.service.RequestProcessingService;
import com.pine.service.SelectionService;
import com.pine.service.request.HierarchyRequest;
import com.pine.theme.Icons;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.*;
import imgui.type.ImString;

import java.util.List;
import java.util.Objects;


public class HierarchyPanel extends AbstractDockPanel {
    private static final byte BYTE = 1;
    private static final String INSTANCED_COMPONENT = InstancedPrimitiveComponent.class.getSimpleName();
    private static final ImVec4 TRANSPARENT = new ImVec4(0, 0, 0, 0);
    private static final ImVec2 PADDING = new ImVec2(0, 0);
    private static final int FLAGS = ImGuiTableFlags.ScrollY | ImGuiTableFlags.Resizable | ImGuiTableFlags.RowBg | ImGuiTableFlags.NoBordersInBody;

    @PInject
    public SelectionService selectionService;

    @PInject
    public WorldRepository world;

    @PInject
    public EditorStateRepository stateRepository;

    @PInject
    public RequestProcessingService requestProcessingService;

    private HierarchyHeaderPanel header;
    private Entity onDrag;
    private final ImString search = new ImString();
    private boolean isOnSearch = false;
    private HierarchyContext context;

    @Override
    public void onInitialize() {
        super.onInitialize();
        appendChild(header = new HierarchyHeaderPanel(search));
        context = (HierarchyContext) getContext();
    }


    @Override
    public void renderInternal() {
        header.render();
        isOnSearch = search.isNotEmpty();

        if (ImGui.beginTable("##hierarchy" + imguiId, 3, FLAGS)) {
            ImGui.tableSetupColumn("Name", ImGuiTableColumnFlags.NoHide);
            ImGui.tableSetupColumn(Icons.visibility, ImGuiTableColumnFlags.WidthFixed, 20f);
            ImGui.tableSetupColumn(Icons.lock, ImGuiTableColumnFlags.WidthFixed, 20f);
            ImGui.tableHeadersRow();

            for (Entity pinned : stateRepository.pinnedEntities.values()) {
                renderNode(pinned, true);
            }
            renderNode(world.rootEntity, false);
        }
        ImGui.endTable();
    }

    private boolean renderNode(Entity node, boolean isPinned) {
        if ((isOnSearch && context.searchMatch.containsKey(node.id) && Objects.equals(context.searchMatchWith.get(node.id), search.get()))) {
            return false;
        }

        boolean isSearchMatch = matchSearch(node);

        ImGui.tableNextRow();
        ImGui.tableNextColumn();
        if (!isPinned) {
            int flags = getFlags(node);

            boolean open = ImGui.treeNodeEx((world.rootEntity == node ? Icons.inventory_2 : Icons.view_in_ar) + node.getTitle() + "##" + node.id + imguiId, flags);
            if (node != world.rootEntity) {
                handleDragDrop(node);
                renderEntityColumns(node, false);
            }
            if (open) {
                context.opened.put(node.id, ImGuiTreeNodeFlags.DefaultOpen);
                renderEntityChildren(node);
            }else{
                context.opened.put(node.id, ImGuiTreeNodeFlags.None);
            }
        } else {
            if (node.selected) {
                ImGui.textColored(stateRepository.getAccentColor(), node.getIcon() + node.getTitle());
            } else {
                ImGui.text(node.getIcon() + node.getTitle());
            }
            renderEntityColumns(node, true);
        }
        return isSearchMatch;
    }

    private boolean matchSearch(Entity node) {
        boolean isSearchMatch = false;
        if (isOnSearch) {
            isSearchMatch = node.getTitle().contains(search.get());
            if (isSearchMatch) {
                context.searchMatch.put(node.id, BYTE);
            } else {
                context.searchMatch.remove(node.id);
            }
            context.searchMatchWith.put(node.id, search.get());
        } else {
            context.searchMatch.remove(node.id);
            context.searchMatchWith.remove(node.id);
        }
        return isSearchMatch;
    }

    private void renderEntityChildren(Entity node) {
        if (isOnSearch) {
            for (var child : node.transformation.children) {
                if (context.searchMatch.containsKey(node.id) || renderNode(child.entity, false)) {
                    context.searchMatch.put(node.id, BYTE);
                } else {
                    context.searchMatch.remove(node.id);
                }
            }
        } else {
            renderComponents(node);
            for (var child : node.transformation.children) {
                renderNode(child.entity, false);
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
            EntityComponent instanced = node.components.get(INSTANCED_COMPONENT);
            if (instanced != null) {
                renderInstancedComponent(node, (InstancedPrimitiveComponent) instanced);
            }
        }
    }

    private void renderInstancedComponent(Entity node, InstancedPrimitiveComponent instanced) {
        List<Transformation> primitives = instanced.primitives;
        for (int i = 0, primitivesSize = primitives.size(); i < primitivesSize; i++) {
            Transformation p = primitives.get(i);
            ImGui.tableNextRow();
            ImGui.tableNextColumn();
            String title = Icons.content_copy + " Instance - " + i;
            if (stateRepository.primitiveSelected == p) {
                ImGui.textColored(stateRepository.getAccentColor(), title);
            } else {
                ImGui.textDisabled(title);
            }
            if (ImGui.isItemClicked()) {
                stateRepository.primitiveSelected = p;
                node.selected = true;
            }
            ImGui.tableNextColumn();
            ImGui.textDisabled("--");
            ImGui.tableNextColumn();
            ImGui.textDisabled("--");
        }
    }

    private int getFlags(Entity node) {
        int flags = ImGuiTreeNodeFlags.SpanFullWidth;
        if (node.selected) {
            flags |= ImGuiTreeNodeFlags.Selected;
        }
        if (isOnSearch) {
            flags |= ImGuiTreeNodeFlags.DefaultOpen;
        }
        return flags | context.opened.getOrDefault(node.id, ImGuiTreeNodeFlags.None);
    }

    private void renderEntityColumns(Entity node, boolean isPinned) {
        handleClick(node);
        ImGui.tableNextColumn();

        ImGui.pushStyleColor(ImGuiCol.Button, TRANSPARENT);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, PADDING);
        ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, 0);
        if (ImGui.button((node.visible ? Icons.visibility : Icons.visibility_off) + (isPinned ? "##vpinned" : "##v") + node.id + imguiId, 20, 15)) {
            node.visible = !node.visible;
        }
        ImGui.tableNextColumn();
        boolean isNodePinned = stateRepository.pinnedEntities.containsKey(node.id);
        if (ImGui.button(((isNodePinned ? Icons.lock : Icons.lock_open) + (isPinned ? "##ppinned" : "##p") + node.id) + imguiId, 20, 15)) {
            if (isNodePinned) {
                stateRepository.pinnedEntities.remove(node.id);
            } else {
                stateRepository.pinnedEntities.put(node.id, node);
            }
        }
        ImGui.popStyleColor();
        ImGui.popStyleVar(2);
    }

    private void handleClick(Entity node) {
        if (ImGui.isItemClicked()) {
            boolean isMultiSelect = ImGui.isKeyPressed(ImGuiKey.LeftCtrl);
            if (!isMultiSelect) {
                selectionService.clearSelection();
            }
            selectionService.addSelected(node);
            node.selected = true;
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

