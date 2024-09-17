package com.pine.engine.core.service.world;

import com.pine.app.core.ui.view.AbstractTree;
import com.pine.engine.core.component.EntityComponent;
import com.pine.engine.core.component.MetadataComponent;

public class WorldHierarchyTree extends AbstractTree<MetadataComponent, EntityComponent> {
    public WorldHierarchyTree(MetadataComponent data) {
        super(data);
    }

    @Override
    public String getName() {
        return data.name;
    }
}
