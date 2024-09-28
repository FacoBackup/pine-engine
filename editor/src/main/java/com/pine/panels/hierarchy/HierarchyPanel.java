package com.pine.panels.hierarchy;

import com.pine.PInject;
import com.pine.dock.AbstractDockPanel;
import com.pine.repository.EditorSettingsRepository;
import com.pine.repository.EntitySelectionRepository;
import com.pine.service.RequestProcessingService;
import com.pine.service.request.HierarchyRequest;
import com.pine.theme.Icons;
import com.pine.tools.tasks.HierarchyTree;
import com.pine.tools.tasks.WorldTreeTask;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.*;
import imgui.type.ImString;

import javax.swing.*;
import java.util.Objects;


public class HierarchyPanel extends AbstractDockPanel {
    private static final ImVec4 TRANSPARENT = new ImVec4(0, 0, 0, 0);
    private static final ImVec2 PADDING = new ImVec2(0, 0);

    @PInject
    public EntitySelectionRepository selectionRepository;

    @PInject
    public WorldTreeTask worldTask;

    @PInject
    public EditorSettingsRepository editorSettingsRepository;

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

        if (ImGui.beginTable("##hierarchy", 2, ImGuiTableFlags.Resizable | ImGuiTableFlags.RowBg | ImGuiTableFlags.NoBordersInBody)) {
            ImGui.tableSetupColumn("Name", ImGuiTableColumnFlags.NoHide);
            ImGui.tableSetupColumn("Hide", ImGuiTableColumnFlags.WidthFixed, 35f);
            ImGui.tableHeadersRow();
            renderNode(worldTask.getHierarchyTree());
        }
        ImGui.endTable();
    }

    private boolean renderNode(HierarchyTree node) {
        if ((isOnSearch && !node.isMatch && Objects.equals(node.matchedWith, search.get())) || (editorSettingsRepository.showOnlyEntitiesHierarchy && !node.isEntity)) {
            return false;
        }
        if (isOnSearch) {
            node.isMatch = node.title.contains(search.get());
            node.matchedWith = search.get();
        }else{
            node.isMatch = true;
            node.matchedWith = null;
        }

        ImGui.tableNextRow();
        ImGui.tableNextColumn();
        if (node.isEntity) {
            int flags = ImGuiTreeNodeFlags.SpanFullWidth;
            if (selectionRepository.getSelected().contains(node.id)) {
                flags |= ImGuiTreeNodeFlags.Selected;
            }
            if (isOnSearch) {
                flags |= ImGuiTreeNodeFlags.DefaultOpen;
            }

            boolean open = ImGui.treeNodeEx(node.titleWithIconId, flags);
            handleClick(node);
            handleDragDrop(node);
            ImGui.tableNextColumn();

            ImGui.pushStyleColor(ImGuiCol.Button, TRANSPARENT);
            ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, PADDING);
            ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, 0);
            if (ImGui.button(Icons.visibility + "##visibility" + node.id, 20, 15)) {

            }
            ImGui.popStyleColor();
            ImGui.popStyleVar(2);
            if (open) {
                if (isOnSearch) {
                    for (var child : node.children) {
                        node.isMatch = node.isMatch || renderNode(child);
                    }
                } else {
                    for (var child : node.children) {
                        renderNode(child);
                    }
                }
                ImGui.treePop();
            }
        } else {
            ImGui.textDisabled(node.titleWithIcon);
            ImGui.tableNextColumn();
            ImGui.textDisabled("--");
        }
        return node.isMatch;
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

