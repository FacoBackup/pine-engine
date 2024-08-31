package com.pine.core;

import com.artemis.*;
import com.artemis.io.JsonArtemisSerializer;
import com.artemis.managers.WorldSerializationManager;
import com.artemis.utils.IntBag;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pine.core.components.component.AbstractComponent;
import com.pine.common.serialization.SerializableRepository;
import com.pine.common.serialization.SerializableResource;
import com.pine.core.components.system.*;
import jakarta.annotation.PostConstruct;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Repository
public class WorldRepository extends SerializableRepository {

    private static final String ENTITY_KEY = "entity";
    private static final String COMPONENTS_KEY = "components";
    private final WorldSerializationManager manager = new WorldSerializationManager();
    private World world;

    @PostConstruct
    public void init() {
        world = new World(new WorldConfigurationBuilder()
                .with(new PreLoopSystem())
                .with(new GarbageCollectorSystem())
                .with(new ScriptExecutorSystem())
                .with(new DirectionalShadowSystem())
                .with(new OmniShadowSystem())
                .with(new VisibilityRendererSystem())
                .with(new AmbientOcclusionSystem())
                .with(new PreRendererSystem())
                .with(new AtmosphereRendererSystem())
                .with(new TerrainRendererSystem())
                .with(new OpaqueRendererSystem())
                .with(new DecalRendererSystem())
                .with(new SpriteRenderer())
                .with(new PostRendererSystem())
                .with(new TransparencyRendererSystem())
                .with(new GlobalIlluminationSystem())
                .with(new BokehDOFSystem())
                .with(new MotionBlurSystem())
                .with(new BloomSystem())
                .with(new PostProcessingSystem())
                .with(new CompositionSystem())
                .build()
                .setSystem(manager));
        manager.setSerializer(new JsonArtemisSerializer(world));
    }

    public World getWorld() {
        return world;
    }

    public void process(){
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

}