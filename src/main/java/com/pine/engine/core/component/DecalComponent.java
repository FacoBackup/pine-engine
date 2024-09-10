package com.pine.engine.core.component;
import com.pine.engine.core.type.MaterialRenderingType;

import java.util.List;

public class DecalComponent extends AbstractComponent{
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

    @Override
    public List<Class<? extends AbstractComponent>> getDependencies() {
        return List.of(TransformationComponent.class, CullingComponent.class);
    }
}
