package com.pine.core.service.world;

import com.pine.AbstractTree;
import com.pine.core.component.EntityComponent;
import com.pine.core.component.MetadataComponent;

public class WorldHierarchyTree extends AbstractTree<MetadataComponent, EntityComponent> {
    public WorldHierarchyTree(MetadataComponent data) {
        super(data);
    }

    @Override
    public String getName() {
        return data.name;
    }
}
