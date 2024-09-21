package com.pine.component;

import com.pine.PBean;

import java.util.Set;

@PBean
public class MeshComponent extends AbstractComponent<MeshComponent> {
    public boolean castsShadows = true;
    public boolean contributeToProbes = true;
    public String meshID;
    public String materialID;

    public MeshComponent(Integer entityId) {
        super(entityId);
    }

    public MeshComponent() {
        super();
    }

    @Override
    protected Set<Class<? extends EntityComponent>> getDependenciesInternal() {
        return Set.of(TransformationComponent.class);
    }


    @Override
    public String getComponentName() {
        return "Mesh";
    }
}
