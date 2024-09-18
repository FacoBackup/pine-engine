package com.pine.app.panels.hierarchy;

import com.pine.app.EditorWindow;
import com.pine.ui.panel.AbstractWindowPanel;
import com.pine.ui.view.FragmentView;
import com.pine.ui.view.TreeView;
import com.pine.app.repository.EntitySelectionRepository;
import com.pine.InjectBean;
import com.pine.component.AbstractComponent;
import com.pine.service.world.WorldService;

public class HierarchyPanel extends AbstractWindowPanel {

    @InjectBean
    public EntitySelectionRepository selectionRepository;

    private WorldService world;

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
        world = ((EditorWindow) document.getWindow()).getEngine().getWorld();

        var headerContainer = (FragmentView) document.getElementById("hierarchyHeader");
        headerContainer.appendChild(new HierarchyHeaderPanel());

        var hierarchyTree = (TreeView) document.getElementById("hierarchyTree");
        hierarchyTree.setTree(world.getHierarchyTree());
        hierarchyTree.setOnClick((branch, multiSelect) -> {
            if (!multiSelect) {
                selectionRepository.clearSelection();
            }
            selectionRepository.addSelected(((AbstractComponent) branch.data).getEntityId());
        });
    }

    @Override
    protected String getTitle() {
        return "Hierarchy";
    }
}