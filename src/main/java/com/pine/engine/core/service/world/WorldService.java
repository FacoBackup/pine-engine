package com.pine.engine.core.service.world;

import com.artemis.*;
import com.artemis.io.JsonArtemisSerializer;
import com.artemis.managers.WorldSerializationManager;
import com.artemis.utils.IntBag;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pine.common.Renderable;
import com.pine.common.Updatable;
import com.pine.engine.Engine;
import com.pine.engine.core.service.serialization.SerializableRepository;
import com.pine.engine.core.service.serialization.SerializableResource;
import com.pine.engine.core.components.component.AbstractComponent;
import com.pine.engine.core.components.system.*;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class WorldService extends SerializableRepository implements Updatable, Renderable {
    private static final String ENTITY_KEY = "entity";
    private static final String COMPONENTS_KEY = "components";
    private final Engine engine;
    private World world;
    private final List<ISystem> systems = List.of(
            new PreLoopSystem(),
            new ScriptExecutorSystem(),
            new DirectionalShadowSystem(),
            new OmniShadowSystem(),
            new VisibilityRendererSystem(),
            new PreRendererSystem(),
            new AtmosphereRendererSystem(),
            new TerrainRendererSystem(),
            new OpaqueRendererSystem(),
            new DecalRendererSystem(),
            new SpriteRendererSystem(),
            new PostRendererSystem(),
            new TransparencyRendererSystem(),
            new GlobalIlluminationSystem(),
            new PostProcessingSystem(),
            new FrameCompositionSystem(),
            new GridSystem()
    );

    public WorldService(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void onInitialize() {
        WorldSerializationManager manager = new WorldSerializationManager();
        world = new World(new WorldConfigurationBuilder()
                .with((BaseSystem) systems.get(0))
                .with((BaseSystem) systems.get(1))
                .with((BaseSystem) systems.get(2))
                .with((BaseSystem) systems.get(3))
                .with((BaseSystem) systems.get(4))
                .with((BaseSystem) systems.get(5))
                .with((BaseSystem) systems.get(6))
                .with((BaseSystem) systems.get(7))
                .with((BaseSystem) systems.get(8))
                .with((BaseSystem) systems.get(9))
                .with((BaseSystem) systems.get(10))
                .with((BaseSystem) systems.get(11))
                .with((BaseSystem) systems.get(12))
                .with((BaseSystem) systems.get(13))
                .with((BaseSystem) systems.get(14))
                .with((BaseSystem) systems.get(15))
                .with((BaseSystem) systems.get(16))
                .build()
                .setSystem(manager));
        manager.setSerializer(new JsonArtemisSerializer(world));
        for (var sys : systems) {
            sys.setEngine(engine);
        }
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void tick() {
        for (ISystem system : systems) {
            system.tick();
        }
    }

    @Override
    public void render() {
        world.process();
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
        return systems;
    }

    public void shutdown() {
        world.dispose();
    }
}