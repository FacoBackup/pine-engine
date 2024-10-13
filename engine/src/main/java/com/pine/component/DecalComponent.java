package com.pine.component;

import com.pine.injection.PBean;
import com.pine.theme.Icons;

import java.util.LinkedList;


public class DecalComponent extends AbstractComponent {
    public DecalComponent(Entity entity) {
        super(entity);
    }

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


    @Override
    public ComponentType getType() {
        return ComponentType.DECAL;
    }
}
