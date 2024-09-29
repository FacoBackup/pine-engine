package com.pine.panels.hierarchy;

import com.pine.PInject;
import com.pine.dock.AbstractDockPanel;
import com.pine.repository.EditorStateRepository;
import com.pine.repository.WorldRepository;
import com.pine.service.RequestProcessingService;
import com.pine.service.SelectionService;
import com.pine.service.request.HierarchyRequest;
import com.pine.theme.Icons;
import com.pine.tools.tasks.HierarchyTree;
import com.pine.tools.tasks.WorldTreeTask;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.*;
import imgui.type.ImString;

import java.util.Map;
import java.util.Objects;

import static com.pine.repository.WorldRepository.ROOT_ID;


public class HierarchyPanel extends AbstractDockPanel {
    private static final ImVec4 TRANSPARENT = new ImVec4(0, 0, 0, 0);
    private static final ImVec2 PADDING = new ImVec2(0, 0);

    @PInject
    public SelectionService selectionRepository;

    @PInject
    public WorldTreeTask worldTask;

    @PInject
    public WorldRepository world;

    @PInject
    public EditorStateRepository editorStateRepository;

    @PInject
    public RequestProcessingService requestProcessingService;

    private HierarchyHeaderPanel header;
    private HierarchyTree onDrag;
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

            Map<Integer, HierarchyTree> nodes = worldTask.getNodes();
            for (var pinned : editorStateRepository.pinnedEntities.keySet()) {
                if (nodes.containsKey(pinned)) {
                    renderNode(nodes.get(pinned), true);
                }
            }
            renderNode(worldTask.getHierarchyTree(), false);
        }
        ImGui.endTable();
    }

    private boolean renderNode(HierarchyTree node, boolean isPinned) {
        if ((isOnSearch && !node.isMatch && Objects.equals(node.matchedWith, search.get())) || (editorStateRepository.showOnlyEntitiesHierarchy && !node.isEntity)) {
            return false;
        }
        if (isOnSearch) {
            node.isMatch = node.title.contains(search.get());
            node.matchedWith = search.get();
        } else {
            node.isMatch = true;
            node.matchedWith = null;
        }

        ImGui.tableNextRow();
        ImGui.tableNextColumn();
        if (node.isEntity && !isPinned) {
            int flags = getFlags(node);

            boolean open = ImGui.treeNodeEx(node.titleWithIconId, flags);
            if(node.id != ROOT_ID) {
                handleDragDrop(node);
                renderEntityColumns(node, false);
            }
            renderEntityChildren(node, open);
        } else if (!node.isEntity) {
            ImGui.textDisabled(node.titleWithIcon);
            ImGui.tableNextColumn();
            ImGui.textDisabled("--");
            ImGui.tableNextColumn();
            ImGui.textDisabled("--");
        } else {
            if (selectionRepository.getSelected().contains(node.id)) {
                ImGui.textColored(editorStateRepository.getAccentColor(), node.titleWithIcon);
            }else{
                ImGui.text(node.titleWithIcon);
            }
            renderEntityColumns(node, true);
        }
        return node.isMatch;
    }

    private void renderEntityChildren(HierarchyTree node, boolean open) {
        if (open) {
            if (isOnSearch) {
                for (var child : node.children) {
                    node.isMatch = node.isMatch || renderNode(child, false);
                }
            } else {
                for (var child : node.children) {
                    renderNode(child, false);
                }
            }
            ImGui.treePop();
        }
    }

    private int getFlags(HierarchyTree node) {
        int flags = ImGuiTreeNodeFlags.SpanFullWidth;
        if (selectionRepository.getSelected().contains(node.id)) {
            flags |= ImGuiTreeNodeFlags.Selected;
        }
        if (isOnSearch) {
            flags |= ImGuiTreeNodeFlags.DefaultOpen;
        }
        return flags;
    }

    private void renderEntityColumns(HierarchyTree node, boolean isPinned) {
        handleClick(node);
        ImGui.tableNextColumn();

        ImGui.pushStyleColor(ImGuiCol.Button, TRANSPARENT);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, PADDING);
        ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, 0);
        Boolean isVisible = world.activeEntities.getOrDefault(node.id, true);
        if (ImGui.button((isVisible ? node.visibilityLabel : node.visibilityOffLabel) + (isPinned ? "pinned" : ""), 20, 15)) {
            world.activeEntities.put(node.id, !isVisible);
        }
        ImGui.tableNextColumn();
        Boolean isNodePinned = editorStateRepository.pinnedEntities.getOrDefault(node.id, false);
        if (ImGui.button((isNodePinned ? node.pinLabel : node.pinOffLabel) + (isPinned ? "pinned" : ""), 20, 15)) {
            editorStateRepository.pinnedEntities.put(node.id, !isNodePinned);
        }
        ImGui.popStyleColor();
        ImGui.popStyleVar(2);
    }

    private void handleClick(HierarchyTree node) {
        if (ImGui.isItemClicked()) {
            boolean isMultiSelect = ImGui.isKeyPressed(ImGuiKey.LeftCtrl);
            if (!isMultiSelect) {
                selectionRepository.clearSelection();
            }
            selectionRepository.addSelected(node.id);
        }
    }


    private void handleDragDrop(HierarchyTree target) {
        if (ImGui.beginDragDropSource()) {
            ImGui.setDragDropPayload(id, id);
            this.onDrag = target;
            ImGui.text("Dragging Node " + target.title);
            ImGui.endDragDropSource();
        }

        if (ImGui.beginDragDropTarget()) {
            if (Objects.equals(ImGui.acceptDragDropPayload(id), id)) {
                requestProcessingService.addRequest(new HierarchyRequest(target.id, onDrag.id));
            }
            ImGui.endDragDropTarget();
        }
    }
}

