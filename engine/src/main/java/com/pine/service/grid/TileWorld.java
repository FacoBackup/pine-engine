package com.pine.service.grid;

import com.pine.component.*;
import com.pine.component.light.PointLightComponent;
import com.pine.component.light.SphereLightComponent;
import com.pine.component.light.SpotLightComponent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class TileWorld {
    public static final String ROOT_ID = Entity.class.getCanonicalName();

    public final Map<String, PointLightComponent> bagPointLightComponent = new HashMap<>();
    public final Map<String, SphereLightComponent> bagSphereLightComponent = new HashMap<>();
    public final Map<String, SpotLightComponent> bagSpotLightComponent = new HashMap<>();
    public final Map<String, EnvironmentProbeComponent> bagEnvironmentProbeComponent = new HashMap<>();
    public final Map<String, MeshComponent> bagMeshComponent = new HashMap<>();
    public final Map<String, TransformationComponent> bagTransformationComponent = new HashMap<>();

    public final Entity rootEntity;
    public final Map<String, Entity> entityMap = new HashMap<>();
    public final Map<String, LinkedList<String>> parentChildren = new HashMap<>();
    public final Map<String, String> childParent = new HashMap<>();
    public final Map<String, Boolean> hiddenEntityMap = new HashMap<>();

    public TileWorld(int x, int z) {
        this.rootEntity = new Entity(ROOT_ID + UUID.randomUUID(), "Tile " + x + " " + z);
        entityMap.put(rootEntity.id(), rootEntity);
        parentChildren.put(rootEntity.id(), new LinkedList<>());
    }

    public void registerComponent(AbstractComponent component) {
        switch (component.getType()) {
            case POINT_LIGHT -> bagPointLightComponent.put(component.getEntityId(), (PointLightComponent) component);
            case SPHERE_LIGHT -> bagSphereLightComponent.put(component.getEntityId(), (SphereLightComponent) component);
            case SPOT_LIGHT -> bagSpotLightComponent.put(component.getEntityId(), (SpotLightComponent) component);
            case ENVIRONMENT_PROBE ->
                    bagEnvironmentProbeComponent.put(component.getEntityId(), (EnvironmentProbeComponent) component);
            case MESH -> bagMeshComponent.put(component.getEntityId(), (MeshComponent) component);
            case TRANSFORMATION ->
                    bagTransformationComponent.put(component.getEntityId(), (TransformationComponent) component);
        }
    }

    public void unregisterComponents(String entity) {
        bagPointLightComponent.remove(entity);
        bagSphereLightComponent.remove(entity);
        bagSpotLightComponent.remove(entity);
        bagEnvironmentProbeComponent.remove(entity);
        bagMeshComponent.remove(entity);
        bagTransformationComponent.remove(entity);
    }

    public void runByComponent(Consumer<AbstractComponent> consumer, String entityId) {
        AbstractComponent   bag = bagPointLightComponent.get(entityId);
        if (bag != null) {
            consumer.accept(bag);
        }
        bag = bagSphereLightComponent.get(entityId);
        if (bag != null) {
            consumer.accept(bag);
        }
        bag = bagSpotLightComponent.get(entityId);
        if (bag != null) {
            consumer.accept(bag);
        }
        bag = bagEnvironmentProbeComponent.get(entityId);
        if (bag != null) {
            consumer.accept(bag);
        }
        bag = bagMeshComponent.get(entityId);
        if (bag != null) {
            consumer.accept(bag);
        }
        bag = bagTransformationComponent.get(entityId);
        if (bag != null) {
            consumer.accept(bag);
        }
    }

    public Map<String, ? extends AbstractComponent> getBagByType(ComponentType type) {
        return switch (type) {
            case POINT_LIGHT -> bagPointLightComponent;
            case SPHERE_LIGHT -> bagSphereLightComponent;
            case SPOT_LIGHT -> bagSpotLightComponent;
            case ENVIRONMENT_PROBE -> bagEnvironmentProbeComponent;
            case MESH -> bagMeshComponent;
            case TRANSFORMATION -> bagTransformationComponent;
        };
    }
}
