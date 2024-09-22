package com.pine.component;

import com.pine.PBean;
import com.pine.inspection.MutableField;

import java.util.Collections;
import java.util.Set;

@PBean
public class MetadataComponent extends AbstractComponent<MetadataComponent> {
    @MutableField(label = "Name")
    public String name = "New Entity";
    public final long creationDate = System.currentTimeMillis();

    public MetadataComponent(Integer entityId) {
        super(entityId);
    }

    public MetadataComponent() {
        super();
    }

    @Override
    protected Set<Class<? extends EntityComponent>> getDependenciesInternal() {
        return Collections.emptySet();
    }
    
    @Override
    public String getComponentName() {
        return "Metadata";
    }
}
