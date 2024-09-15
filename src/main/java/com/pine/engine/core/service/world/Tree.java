package com.pine.engine.core.service.world;

import com.pine.app.core.ui.view.AbstractTree;
import com.pine.engine.core.component.MetadataComponent;

public class Tree extends AbstractTree<MetadataComponent> {
    public Tree(MetadataComponent data) {
        super(data);
    }

    @Override
    public String getName() {
        return data.name;
    }
}
