package com.jengine.app.core.repository;

import com.artemis.*;
import com.artemis.io.JsonArtemisSerializer;
import com.artemis.managers.WorldSerializationManager;
import com.artemis.utils.IntBag;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jengine.app.core.components.component.AbstractComponent;
import com.jengine.app.core.components.system.*;
import com.jengine.app.core.service.serialization.SerializableRepository;
import jakarta.annotation.PostConstruct;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Repository
public class WorldRepository extends SerializableRepository {

    private static final String ENTITY_KEY = "entity";

    @Autowired
    private PreLoopSystem preLoopSystem;

    @Autowired
    private GarbageCollectorSystem garbageCollectorSystem;

    @Autowired
    private ScriptExecutorSystem scriptExecutorSystem;

    @Autowired
    private DirectionalShadowSystem directionalShadowSystem;

    @Autowired
    private OmniShadowSystem omniShadowSystem;

    @Autowired
    private VisibilityRendererSystem visibilityRendererSystem;

    @Autowired
    private AmbientOcclusionSystem ambientOcclusionSystem;

    @Autowired
    private PreRendererSystem preRendererSystem;

    @Autowired
    private AtmosphereRendererSystem atmosphereRendererSystem;

    @Autowired
    private TerrainRendererSystem terrainRendererSystem;

    @Autowired
    private OpaqueRendererSystem opaqueRendererSystem;

    @Autowired
    private DecalRendererSystem decalRendererSystem;

    @Autowired
    private SpriteRenderer spriteRenderer;

    @Autowired
    private PostRendererSystem postRendererSystem;

    @Autowired
    private TransparencyRendererSystem transparencyRendererSystem;

    @Autowired
    private GlobalIlluminationSystem globalIlluminationSystem;

    @Autowired
    private BokehDOFSystem bokehDOFSystem;

    @Autowired
    private MotionBlurSystem motionBlurSystem;

    @Autowired
    private BloomSystem bloomSystem;

    @Autowired
    private PostProcessingSystem postProcessingSystem;

    @Autowired
    private CompositionSystem compositionSystem;

    private final WorldSerializationManager manager = new WorldSerializationManager();
    private World world;

    @PostConstruct
    public void init() {
        world = new World(new WorldConfigurationBuilder()
                .with(preLoopSystem)
                .with(garbageCollectorSystem)
                .with(scriptExecutorSystem)
                .with(directionalShadowSystem)
                .with(omniShadowSystem)
                .with(visibilityRendererSystem)
                .with(ambientOcclusionSystem)
                .with(preRendererSystem)
                .with(atmosphereRendererSystem)
                .with(terrainRendererSystem)
                .with(opaqueRendererSystem)
                .with(decalRendererSystem)
                .with(spriteRenderer)
                .with(postRendererSystem)
                .with(transparencyRendererSystem)
                .with(globalIlluminationSystem)
                .with(bokehDOFSystem)
                .with(motionBlurSystem)
                .with(bloomSystem)
                .with(postProcessingSystem)
                .with(compositionSystem)
                .build()
                .setSystem(manager));
        manager.setSerializer(new JsonArtemisSerializer(world));
    }

    public World getWorld() {
        return world;
    }

    @Override
    public JsonElement serializeData() {
        final List<ComponentMapper<? extends Component>> components = new ArrayList<>();
        final Set<Class<? extends AbstractComponent>> classes = new HashSet<>();
        collectComponents(world, classes, components);

        final JsonArray instances = new JsonArray();
        IntBag entities = world.getAspectSubscriptionManager()
                .get(Aspect.all(classes.stream().map(c -> (Class<? extends Component>) c).collect(Collectors.toSet())))
                .getEntities();

        int[] entityIds = entities.getData();
        for (int i = 0; i < entities.size(); i++) {
            serializeEntity(instances, world.getEntity(entityIds[i]), components);
        }
        return instances;
    }

    private static void collectComponents(World world, Set<Class<? extends AbstractComponent>> classes, List<ComponentMapper<? extends Component>> components) {
        final Reflections reflections = new Reflections(AbstractComponent.class.getPackageName());
        classes.addAll(reflections.getSubTypesOf(AbstractComponent.class));
        classes.forEach(c -> {
            components.add(world.getMapper(c));
        });
    }

    private static void serializeEntity(JsonArray instances, Entity entity, List<ComponentMapper<? extends Component>> components) {
        for (var component : components) {
            var instance = (AbstractComponent) component.get(entity);
            JsonObject serialized = instance.serialize();
            serialized.addProperty(ENTITY_KEY, entity.getId());
            instances.add(serialized);
        }
    }

    @Override
    protected void parseInternal(JsonObject data) {
        // TODO
    }
}