package com.pine.engine.core.component;

import com.pine.engine.core.EngineInjectable;
import com.pine.engine.core.type.MaterialRenderingType;

import java.util.Set;

@EngineInjectable
public class DecalComponent extends AbstractComponent<DecalComponent> {
    public String albedoID;
    public String roughnessID;
    public String metallicID;
    public String normalID;
    public String occlusionID;
    public boolean useSSR = false;
    public MaterialRenderingType renderingMode = MaterialRenderingType.ISOTROPIC;
    public float anisotropicRotation = 0.0f;
    public float anisotropy = 0.0f;
    public float clearCoat = 0.0f;
    public float sheen = 0.0f;
    public float sheenTint = 0.0f;

    public DecalComponent(Integer entityId) {
        super(entityId);
    }

    public DecalComponent() {
        super();
    }

    @Override
    protected Set<Class<? extends AbstractComponent>> getDependenciesInternal() {
        return Set.of(TransformationComponent.class);
    }
}
