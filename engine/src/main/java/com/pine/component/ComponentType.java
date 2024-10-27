package com.pine.component;

import com.pine.component.light.DirectionalLightComponent;
import com.pine.component.light.PointLightComponent;
import com.pine.component.light.SphereLightComponent;
import com.pine.component.light.SpotLightComponent;
import com.pine.theme.Icons;
import com.pine.type.LightType;

import java.io.Serializable;

public enum ComponentType implements Serializable {
    DIRECTIONAL_LIGHT(LightType.DIRECTIONAL.getTitle(), Icons.wb_sunny, DirectionalLightComponent.class),
    POINT_LIGHT(LightType.POINT.getTitle(), Icons.lightbulb, PointLightComponent.class),
    SPHERE_LIGHT(LightType.SPHERE.getTitle(), Icons.circle, SphereLightComponent.class),
    SPOT_LIGHT(LightType.SPOT.getTitle(), Icons.highlight, SpotLightComponent.class),
    DECAL("Decal", Icons.format_shapes, DecalComponent.class),
    ENVIRONMENT_PROBE("Environment Probe", Icons.panorama_photosphere, EnvironmentProbeComponent.class),
    MESH("Mesh", Icons.category, MeshComponent.class),
    SPRITE("Sprite", Icons.image, SpriteComponent.class);

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
