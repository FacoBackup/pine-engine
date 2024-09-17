package com.pine.engine.core.component;

import com.pine.engine.core.EngineInjectable;

import java.util.Set;

@EngineInjectable
public class MeshComponent extends AbstractComponent {
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
