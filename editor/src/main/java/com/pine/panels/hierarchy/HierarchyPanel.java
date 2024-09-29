package com.pine.panels.hierarchy;

import com.pine.PInject;
import com.pine.component.Entity;
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

import java.util.Objects;


public class HierarchyPanel extends AbstractDockPanel {
    private static final ImVec4 TRANSPARENT = new ImVec4(0, 0, 0, 0);
    private static final ImVec2 PADDING = new ImVec2(0, 0);

    @PInject
    public SelectionService selectionRepository;

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

    @Override
    public void onInitialize() {
        super.onInitialize();
        appendChild(header = new HierarchyHeaderPanel(search));
    }


    @Override
    public void renderInternal() {
        header.render();
        isOnSearch = search.isNotEmpty();

        if (ImGui.beginTable("##hierarchy", 3, ImGuiTableFlags.Resizable | ImGuiTableFlags.RowBg | ImGuiTableFlags.NoBordersInBody)) {
            ImGui.tableSetupColumn("Name", ImGuiTableColumnFlags.NoHide);
            ImGui.tableSetupColumn(Icons.visibility, ImGuiTableColumnFlags.WidthFixed, 20f);
            ImGui.tableSetupColumn(Icons.lock, ImGuiTableColumnFlags.WidthFixed, 20f);
            ImGui.tableHeadersRow();

            for (Entity pinned : stateRepository.pinnedEntities) {
                renderNode(pinned, true);
            }
            renderNode(world.rootEntity, false);
        }
        ImGui.endTable();
    }

    private boolean renderNode(Entity node, boolean isPinned) {
        if ((isOnSearch && !node.isSearchMatch && Objects.equals(node.searchMatchedWith, search.get()))) {
            return false;
        }
        if (isOnSearch) {
            node.isSearchMatch = node.getTitle().contains(search.get());
            node.searchMatchedWith = search.get();
        } else {
            node.isSearchMatch = true;
            node.searchMatchedWith = null;
        }

        ImGui.tableNextRow();
        ImGui.tableNextColumn();
        if (!isPinned) {
            int flags = getFlags(node);

            boolean open = ImGui.treeNodeEx(node.getIcon() + node.getTitle() + "##" + node.id, flags);
            if (node != world.rootEntity) {
                handleDragDrop(node);
                renderEntityColumns(node, false);
            }
            renderEntityChildren(node, open);
        } else {
            if (node.selected) {
                ImGui.textColored(stateRepository.getAccentColor(), node.getIcon() + node.getTitle());
            } else {
                ImGui.text(node.getIcon() + node.getTitle());
            }
            renderEntityColumns(node, true);
        }
        return node.isSearchMatch;
    }

    private void renderEntityChildren(Entity node, boolean open) {
        if (open) {
            if (isOnSearch) {
                for (var child : node.children) {
                    node.isSearchMatch = node.isSearchMatch || renderNode(child, false);
                }
            } else {
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
                }
                for (var child : node.children) {
                    renderNode(child, false);
                }
            }

            ImGui.treePop();
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
        return flags;
    }

    private void renderEntityColumns(Entity node, boolean isPinned) {
        handleClick(node);
        ImGui.tableNextColumn();

        ImGui.pushStyleColor(ImGuiCol.Button, TRANSPARENT);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, PADDING);
        ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, 0);
        if (ImGui.button((node.visible ? Icons.visibility : Icons.visibility_off) + (isPinned ? "pinned" : "") + "##v" + node.id, 20, 15)) {
            node.visible = !node.visible;
        }
        ImGui.tableNextColumn();
        if (ImGui.button(((node.pinned ? Icons.lock : Icons.lock_open) + (isPinned ? "pinned" : "") + "##p" + node.id), 20, 15)) {
            node.pinned = !node.pinned;
            if (node.pinned) {
                stateRepository.pinnedEntities.add(node);
            } else {
                stateRepository.pinnedEntities.remove(node);
            }
        }
        ImGui.popStyleColor();
        ImGui.popStyleVar(2);
    }

    private void handleClick(Entity node) {
        if (ImGui.isItemClicked()) {
            boolean isMultiSelect = ImGui.isKeyPressed(ImGuiKey.LeftCtrl);
            if (!isMultiSelect) {
                selectionRepository.clearSelection();
            }
            selectionRepository.addSelected(node);
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

