package com.pine.panels.hierarchy;

import com.pine.PInject;
import com.pine.component.EntityComponent;
import com.pine.component.MetadataComponent;
import com.pine.dock.AbstractDockPanel;
import com.pine.repository.EntitySelectionRepository;
import com.pine.service.RequestProcessingService;
import com.pine.service.request.HierarchyRequest;
import com.pine.tools.tasks.WorldTreeTask;
import com.pine.view.TreeView;
import imgui.ImGui;

public class HierarchyPanel extends AbstractDockPanel {

    @PInject
    public EntitySelectionRepository selectionRepository;

    @PInject
    public WorldTreeTask worldTask;

    @PInject
    public RequestProcessingService requestProcessingService;

    @Override
    public void onInitialize() {
        super.onInitialize();
        appendChild(new HierarchyHeaderPanel());
        TreeView hierarchyTree = appendChild(new TreeView());
        hierarchyTree.setTree(worldTask.getHierarchyTree());
        hierarchyTree.setOnClick((branch, multiSelect) -> {
            if (!multiSelect) {
                selectionRepository.clearSelection();
            }
            selectionRepository.addSelected(((EntityComponent) branch.data).getEntityId());
        });
        hierarchyTree.setOnDrop((target, drop) -> {
            requestProcessingService.addRequest(new HierarchyRequest(((MetadataComponent) target.data).getEntityId(), ((MetadataComponent) drop.data).getEntityId()));
        });
    }

    @Override
    public void renderInternal() {
        ImGui.beginGroup();
        super.renderInternal();
        ImGui.endGroup();
    }
}

