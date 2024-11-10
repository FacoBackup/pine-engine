package com.pine.component;

import com.pine.component.light.PointLightComponent;
import com.pine.component.light.SphereLightComponent;
import com.pine.component.light.SpotLightComponent;
import com.pine.theme.Icons;
import com.pine.type.LightType;

import java.io.Serializable;

public enum ComponentType implements Serializable {
    POINT_LIGHT(LightType.POINT.getTitle(), Icons.lightbulb, PointLightComponent.class),
    SPHERE_LIGHT(LightType.SPHERE.getTitle(), Icons.circle, SphereLightComponent.class),
    SPOT_LIGHT(LightType.SPOT.getTitle(), Icons.highlight, SpotLightComponent.class),
    ENVIRONMENT_PROBE("Environment Probe", Icons.panorama_photosphere, EnvironmentProbeComponent.class),
    MESH("Mesh", Icons.category, MeshComponent.class),
    TRANSFORMATION("Transformation", Icons.transform, TransformationComponent.class);

    private final String title;
    private final String icon;
    private transient final Class<? extends AbstractComponent> clazz;

    ComponentType(String title, String icon, Class<? extends AbstractComponent> clazz) {
        this.title = title;
        this.icon = icon;
        this.clazz = clazz;
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
}
