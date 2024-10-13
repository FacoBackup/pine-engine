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
    DECAL("Decal Component", Icons.format_shapes, DecalComponent.class),
    LIGHT_PROBE("Light Probe Component", Icons.panorama, LightProbeComponent.class),
    MESH("Mesh Component", Icons.category, MeshComponent.class),
    PHYSICS_COLLIDER("Physics Collider Component", Icons.widgets, PhysicsColliderComponent.class),
    RIGID_BODY("Rigid Body Component", Icons.widgets, RigidBodyComponent.class),
    SPRITE("Sprite Component", Icons.image, SpriteComponent.class);

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
