package com.pine.engine.core.service;

import com.artemis.*;
import com.artemis.io.JsonArtemisSerializer;
import com.artemis.managers.WorldSerializationManager;
import com.artemis.utils.IntBag;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pine.common.serialization.SerializableRepository;
import com.pine.common.serialization.SerializableResource;
import com.pine.engine.core.components.component.AbstractComponent;
import com.pine.engine.core.components.system.*;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class WorldService extends SerializableRepository {
    private static final String ENTITY_KEY = "entity";
    private static final String COMPONENTS_KEY = "components";
    private final World world;
    private final PreLoopSystem insPreLoopSystem = new PreLoopSystem();
    private final ScriptExecutorSystem insScriptExecutorSystem = new ScriptExecutorSystem();
    private final DirectionalShadowSystem insDirectionalShadowSystem = new DirectionalShadowSystem();
    private final OmniShadowSystem insOmniShadowSystem = new OmniShadowSystem();
    private final VisibilityRendererSystem insVisibilityRendererSystem = new VisibilityRendererSystem();
    private final PreRendererSystem insPreRendererSystem = new PreRendererSystem();
    private final AtmosphereRendererSystem insAtmosphereRendererSystem = new AtmosphereRendererSystem();
    private final TerrainRendererSystem insTerrainRendererSystem = new TerrainRendererSystem();
    private final OpaqueRendererSystem insOpaqueRendererSystem = new OpaqueRendererSystem();
    private final DecalRendererSystem insDecalRendererSystem = new DecalRendererSystem();
    private final SpriteRendererSystem insSpriteRendererSystem = new SpriteRendererSystem();
    private final PostRendererSystem insPostRendererSystem = new PostRendererSystem();
    private final TransparencyRendererSystem insTransparencyRendererSystem = new TransparencyRendererSystem();
    private final GlobalIlluminationSystem insGlobalIlluminationSystem = new GlobalIlluminationSystem();
    private final PostProcessingSystem insPostProcessingSystem = new PostProcessingSystem();
    private final FrameCompositionSystem insFrameCompositionSystem = new FrameCompositionSystem();

    public WorldService() {
        WorldSerializationManager manager = new WorldSerializationManager();
        world = new World(new WorldConfigurationBuilder()
                .with(insPreLoopSystem)
                .with(insScriptExecutorSystem)
                .with(insDirectionalShadowSystem)
                .with(insOmniShadowSystem)
                .with(insVisibilityRendererSystem)
                .with(insPreRendererSystem)
                .with(insAtmosphereRendererSystem)
                .with(insTerrainRendererSystem)
                .with(insOpaqueRendererSystem)
                .with(insDecalRendererSystem)
                .with(insSpriteRendererSystem)
                .with(insPostRendererSystem)
                .with(insTransparencyRendererSystem)
                .with(insGlobalIlluminationSystem)
                .with(insPostProcessingSystem)
                .with(insFrameCompositionSystem)
                .build()
                .setSystem(manager));
        manager.setSerializer(new JsonArtemisSerializer(world));
    }

    public World getWorld() {
        return world;
    }

    @Override
    protected void parseInternal(JsonElement data) {
        JsonArray entities = data.getAsJsonArray();
        for (JsonElement entity : entities) {
            try {
                JsonObject obj = entity.getAsJsonObject();
                int entityId = world.create();
                JsonArray componentsJson = obj.get(COMPONENTS_KEY).getAsJsonArray();
                for (JsonElement component : componentsJson) {
                    JsonObject objComponent = component.getAsJsonObject();
                    var componentEntity = (SerializableResource) world.edit(entityId).create((Class<? extends Component>) Class.forName(objComponent.get(CLASS_KEY).getAsString()));
                    componentEntity.parse(objComponent);
                }
            } catch (Exception ex) {
                getLogger().error("Error while parsing entity", ex);
            }
        }
        world.process();
    }

    @Override
    public JsonElement serializeData() {
        final List<ComponentMapper<? extends Component>> components = new ArrayList<>();
        final Reflections reflections = new Reflections(AbstractComponent.class.getPackageName());
        final Set<Class<? extends AbstractComponent>> classes = new HashSet<>(reflections.getSubTypesOf(AbstractComponent.class));
        classes.forEach(c -> {
            components.add(world.getMapper(c));
        });

        final JsonArray instances = new JsonArray();
        IntBag entities = world.getAspectSubscriptionManager()
                .get(Aspect.one(classes.stream().map(c -> (Class<? extends Component>) c).collect(Collectors.toSet())))
                .getEntities();

        int[] entityIds = entities.getData();
        for (int i = 0; i < entities.size(); i++) {
            instances.add(serializeEntity(world.getEntity(entityIds[i]), components));
        }
        return instances;
    }

    private static JsonElement serializeEntity(Entity entity, List<ComponentMapper<? extends Component>> components) {
        JsonObject json = new JsonObject();
        json.addProperty(ENTITY_KEY, entity.getId());
        JsonArray entityComponents = new JsonArray();
        for (var component : components) {
            var instance = (AbstractComponent) component.get(entity);
            if (instance != null) {
                JsonObject serialized = instance.serialize();
                entityComponents.add(serialized);
            }
        }
        json.add(COMPONENTS_KEY, entityComponents);
        return json;
    }

    public <T extends AbstractComponent> List<AbstractComponent> addComponent(int entity, final Class<T> component) {
        List<AbstractComponent> added = new ArrayList<>();
        AbstractComponent comp = world.edit(entity).create(component);
        for (var dep : comp.getDependencies()) {
            added.addAll(addComponent(entity, dep));
        }
        added.add(comp);
        return added;
    }

    public int addEntity() {
        return world.create();
    }

    public List<ISystem> getSystems() {
        return List.of(
                insPreLoopSystem,
                insScriptExecutorSystem,
                insDirectionalShadowSystem,
                insOmniShadowSystem,
                insVisibilityRendererSystem,
                insPreRendererSystem,
                insAtmosphereRendererSystem,
                insTerrainRendererSystem,
                insOpaqueRendererSystem,
                insDecalRendererSystem,
                insSpriteRendererSystem,
                insPostRendererSystem,
                insTransparencyRendererSystem,
                insGlobalIlluminationSystem,
                insPostProcessingSystem,
                insFrameCompositionSystem
        );
    }
}