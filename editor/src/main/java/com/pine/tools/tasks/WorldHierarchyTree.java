package com.pine.tools.tasks;

import com.pine.AbstractTree;
import com.pine.component.EntityComponent;
import com.pine.component.MetadataComponent;

public class WorldHierarchyTree extends AbstractTree<MetadataComponent, EntityComponent> {
    public WorldHierarchyTree(MetadataComponent data) {
        super(data);
    }

    @Override
    public String getName() {
        return data.name;
    }
}
