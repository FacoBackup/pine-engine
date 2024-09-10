package com.pine.engine.core.component;

import java.util.List;

public class MeshComponent extends AbstractComponent{
    public boolean castsShadows = true;
    public boolean contributeToProbes = true;
    public String meshID;
    public String materialID;

    @Override
    public List<Class<? extends AbstractComponent>> getDependencies() {
        return List.of(TransformationComponent.class);
    }

}
