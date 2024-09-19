package com.pine.component;

import com.pine.annotation.EngineInjectable;

import java.util.Set;

@EngineInjectable
public class LightProbeComponent extends AbstractComponent<LightProbeComponent> {
    public int mipmaps = 6;
    /**
     * Only entities within  this radius will be rendered into the probe
     */
    public int collectionRadius = 50;

    public LightProbeComponent(Integer entityId) {
        super(entityId);
    }

    public LightProbeComponent() {
        super();
    }

    @Override
    protected Set<Class<? extends EntityComponent>> getDependenciesInternal() {
        return Set.of(TransformationComponent.class);
    }

    @Override
    public String getComponentName() {
        return "Probe";
    }
}
