package com.pine.component;

import com.pine.PBean;

import java.util.Set;

@PBean
public class DecalComponent extends AbstractComponent<DecalComponent> {

    // TODO - MIGRATE TO MATERIAL DEFINITION
//    public String albedoID;
//    public String roughnessID;
//    public String metallicID;
//    public String normalID;
//    public String occlusionID;
//    public boolean useSSR = false;
//    public MaterialRenderingType renderingMode = MaterialRenderingType.ISOTROPIC;
//    public float anisotropicRotation = 0.0f;
//    public float anisotropy = 0.0f;
//    public float clearCoat = 0.0f;
//    public float sheen = 0.0f;
//    public float sheenTint = 0.0f;

    public DecalComponent(Integer entityId) {
        super(entityId);
    }

    public DecalComponent() {
        super();
    }

    @Override
    protected Set<Class<? extends EntityComponent>> getDependenciesInternal() {
        return Set.of(TransformationComponent.class);
    }

    @Override
    public String getComponentName() {
        return "Decal";
    }
}
