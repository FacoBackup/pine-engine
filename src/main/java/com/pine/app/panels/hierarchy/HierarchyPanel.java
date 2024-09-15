package com.pine.app.panels.hierarchy;

import com.pine.app.EditorWindow;
import com.pine.app.core.ui.panel.AbstractWindowPanel;
import com.pine.app.core.ui.view.FragmentView;
import com.pine.app.core.ui.view.TreeView;
import com.pine.app.repository.EntitySelectionRepository;
import com.pine.common.InjectBean;
import com.pine.engine.core.component.AbstractComponent;
import com.pine.engine.core.service.world.WorldService;

public class HierarchyPanel extends AbstractWindowPanel {

    @InjectBean
    private EntitySelectionRepository selectionRepository;

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
            selectionRepository.addSelected(((AbstractComponent<?>) branch.data).getEntityId());
        });
    }

    @Override
    protected String getTitle() {
        return "Hierarchy";
    }
}
