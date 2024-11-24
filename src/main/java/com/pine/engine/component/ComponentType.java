package com.pine.engine.component;

import com.pine.common.Icons;
import com.pine.engine.component.light.PointLightComponent;
import com.pine.engine.component.light.SphereLightComponent;
import com.pine.engine.component.light.SpotLightComponent;
import com.pine.engine.type.LightType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public enum ComponentType implements Serializable {
    POINT_LIGHT(LightType.POINT.getTitle(), Icons.lightbulb, PointLightComponent.class, true),
    SPHERE_LIGHT(LightType.SPHERE.getTitle(), Icons.sunny, SphereLightComponent.class, true),
    SPOT_LIGHT(LightType.SPOT.getTitle(), Icons.highlight, SpotLightComponent.class, true),
    ENVIRONMENT_PROBE("Environment Probe", Icons.blur_on, EnvironmentProbeComponent.class, true),
    MESH("Mesh", Icons.category, MeshComponent.class, true),
    DECAL("Decal", Icons.layers, DecalComponent.class, true),
    CULLING("Culling", Icons.visibility_off, CullingComponent.class, false),
    TRANSFORMATION("Transformation", Icons.transform, TransformationComponent.class, false);

    private final boolean isSoleType;
    private final String title;
    private final String icon;
    private transient final Class<? extends AbstractComponent> clazz;

    ComponentType(String title, String icon, Class<? extends AbstractComponent> clazz, boolean isSoleType) {
        this.title = title;
        this.icon = icon;
        this.isSoleType = isSoleType;
        this.clazz = clazz;
    }

    public static List<ComponentType> getSoleTypes() {
        List<ComponentType> types = new ArrayList<>();
        for (ComponentType type : ComponentType.values()) {
            if (type.isSoleType) {
                types.add(type);
            }
        }
        return types;
    }

    public String getTitle() {
        return title;
    }

    public Class<? extends AbstractComponent> getClazz() {
        return clazz;
    }

    public String getIcon() {
        return icon;
    }

    public boolean isSoleType() {
        return isSoleType;
    }
}
