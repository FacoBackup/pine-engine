package com.pine.core.repository;

import com.artemis.*;
import com.artemis.io.JsonArtemisSerializer;
import com.artemis.managers.WorldSerializationManager;
import com.artemis.utils.IntBag;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pine.core.components.component.AbstractComponent;
import com.pine.core.components.serialization.SerializableRepository;
import com.pine.core.components.serialization.SerializableResource;
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

    @Autowired
    private ApplicationContext context;

    @PostConstruct
    public void init() {
        world = new World(new WorldConfigurationBuilder()
                .with(context.getBean(PreLoopSystem.class))
                .with(context.getBean(GarbageCollectorSystem.class))
                .with(context.getBean(ScriptExecutorSystem.class))
                .with(context.getBean(DirectionalShadowSystem.class))
                .with(context.getBean(OmniShadowSystem.class))
                .with(context.getBean(VisibilityRendererSystem.class))
                .with(context.getBean(AmbientOcclusionSystem.class))
                .with(context.getBean(PreRendererSystem.class))
                .with(context.getBean(AtmosphereRendererSystem.class))
                .with(context.getBean(TerrainRendererSystem.class))
                .with(context.getBean(OpaqueRendererSystem.class))
                .with(context.getBean(DecalRendererSystem.class))
                .with(context.getBean(SpriteRenderer.class))
                .with(context.getBean(PostRendererSystem.class))
                .with(context.getBean(TransparencyRendererSystem.class))
                .with(context.getBean(GlobalIlluminationSystem.class))
                .with(context.getBean(BokehDOFSystem.class))
                .with(context.getBean(MotionBlurSystem.class))
                .with(context.getBean(BloomSystem.class))
                .with(context.getBean(PostProcessingSystem.class))
                .with(context.getBean(CompositionSystem.class))
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
            if(instance != null) {
                JsonObject serialized = instance.serialize();
                entityComponents.add(serialized);
            }
        }
        json.add(COMPONENTS_KEY, entityComponents);
        return json;
    }
}