package com.pine.component;

import com.pine.injection.PBean;
import com.pine.theme.Icons;

import java.util.LinkedList;

@PBean
public class DecalComponent extends AbstractComponent<DecalComponent> {
    public DecalComponent(Entity entity, LinkedList<?> bag) {
        super(entity, bag);
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

    public DecalComponent() {}

    @Override
    public String getTitle() {
        return "Decal";
    }

    @Override
    public String getIcon() {
        return Icons.format_paint;
    }
}
