package com.pine.repository;

import com.pine.SerializableRepository;
import com.pine.component.*;
import com.pine.component.light.PointLightComponent;
import com.pine.component.light.SphereLightComponent;
import com.pine.component.light.SpotLightComponent;
import com.pine.injection.PBean;
import com.pine.service.grid.WorldGrid;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Consumer;

@PBean
public class WorldRepository implements SerializableRepository {
    public static final String ROOT_ID = Entity.class.getCanonicalName();

    public final WorldGrid worldGrid = new WorldGrid();

    public final Map<String, PointLightComponent> bagPointLightComponent = new HashMap<>();
    public final Map<String, SphereLightComponent> bagSphereLightComponent = new HashMap<>();
    public final Map<String, SpotLightComponent> bagSpotLightComponent = new HashMap<>();
    public final Map<String, DecalComponent> bagDecalComponent = new HashMap<>();
    public final Map<String, EnvironmentProbeComponent> bagEnvironmentProbeComponent = new HashMap<>();
    public final Map<String, MeshComponent> bagMeshComponent = new HashMap<>();
    public final Map<String, CullingComponent> bagCullingComponent = new HashMap<>();
    public final Map<String, TransformationComponent> bagTransformationComponent = new HashMap<>();

    public final Map<String, Entity> entityMap = new HashMap<>() {{
        put(ROOT_ID, new Entity(ROOT_ID, "World"));
    }};
    public final Map<String, LinkedList<String>> parentChildren = new HashMap<>() {{
        put(ROOT_ID, new LinkedList<>());
    }};
    public final Map<String, String> childParent = new HashMap<>();
    public final Map<String, Boolean> hiddenEntities = new HashMap<>();
    public final Map<String, Boolean> culled = new HashMap<>();

    public void registerComponent(AbstractComponent component) {
        switch (component.getType()) {
            case POINT_LIGHT -> bagPointLightComponent.put(component.getEntityId(), (PointLightComponent) component);
            case SPHERE_LIGHT -> bagSphereLightComponent.put(component.getEntityId(), (SphereLightComponent) component);
            case SPOT_LIGHT -> bagSpotLightComponent.put(component.getEntityId(), (SpotLightComponent) component);
            case DECAL -> bagDecalComponent.put(component.getEntityId(), (DecalComponent) component);
            case CULLING -> bagCullingComponent.put(component.getEntityId(), (CullingComponent) component);
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
        bagDecalComponent.remove(entity);
        bagCullingComponent.remove(entity);
        bagEnvironmentProbeComponent.remove(entity);
        bagMeshComponent.remove(entity);
        bagTransformationComponent.remove(entity);
    }

    public void runByComponent(Consumer<AbstractComponent> consumer, String entityId) {
        AbstractComponent bag = bagPointLightComponent.get(entityId);
        if (bag != null) {
            consumer.accept(bag);
        }
        bag = bagSphereLightComponent.get(entityId);
        if (bag != null) {
            consumer.accept(bag);
        }
        bag = bagCullingComponent.get(entityId);
        if (bag != null) {
            consumer.accept(bag);
        }
        bag = bagDecalComponent.get(entityId);
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
            case CULLING -> bagCullingComponent;
            case POINT_LIGHT -> bagPointLightComponent;
            case DECAL -> bagDecalComponent;
            case SPHERE_LIGHT -> bagSphereLightComponent;
            case SPOT_LIGHT -> bagSpotLightComponent;
            case ENVIRONMENT_PROBE -> bagEnvironmentProbeComponent;
            case MESH -> bagMeshComponent;
            case TRANSFORMATION -> bagTransformationComponent;
        };
    }
}
