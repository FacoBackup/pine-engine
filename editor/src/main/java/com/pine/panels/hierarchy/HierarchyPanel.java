package com.pine.panels.hierarchy;

import com.pine.PInject;
import com.pine.component.EntityComponent;
import com.pine.component.MetadataComponent;
import com.pine.repository.EntitySelectionRepository;
import com.pine.service.RequestProcessingService;
import com.pine.service.world.request.HierarchyRequest;
import com.pine.tools.repository.WorldTreeTask;
import com.pine.ui.panel.AbstractWindowPanel;
import com.pine.ui.view.FragmentView;
import com.pine.ui.view.TreeView;

public class HierarchyPanel extends AbstractWindowPanel {

    @PInject
    public EntitySelectionRepository selectionRepository;

    @PInject
    public WorldTreeTask worldTask;

    @PInject
    public RequestProcessingService requestProcessingService;

    @Override
    protected String getDefinition() {
        return """
                <group>
                    <fragment id='hierarchyHeader'/>
                    <tree id='hierarchyTree'/>
                </group>
                """;
    }

    @Override
    public void onInitialize() {
        super.onInitialize();

        var headerContainer = (FragmentView) document.getElementById("hierarchyHeader");
        headerContainer.appendChild(new HierarchyHeaderPanel());

        var hierarchyTree = (TreeView) document.getElementById("hierarchyTree");
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
    protected String getTitle() {
        return "Hierarchy";
    }
}
