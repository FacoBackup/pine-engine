package com.jengine.app.core.components.component;
import com.jengine.app.core.components.MaterialRenderingType;

import java.util.List;

public class DecalComponent extends AbstractComponent{
    private String albedoID;
    private String roughnessID;
    private String metallicID;
    private String normalID;
    private String occlusionID;
    private boolean useSSR = false;
    private MaterialRenderingType renderingMode = MaterialRenderingType.ISOTROPIC;
    private float anisotropicRotation = 0.0f;
    private float anisotropy = 0.0f;
    private float clearCoat = 0.0f;
    private float sheen = 0.0f;
    private float sheenTint = 0.0f;

    @Override
    public List<Class<? extends AbstractComponent>> getDependencies() {
        return List.of(TransformationComponent.class, CullingComponent.class);
    }
}
