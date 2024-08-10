package com.jengine.jengine.app.engine.resource;

import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.jengine.jengine.app.engine.AbstractComponent;
import com.jengine.jengine.app.engine.system.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class WorldRepository extends World {
    public WorldRepository() {
        super(new WorldConfigurationBuilder()
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
                .build());
    }

    @SafeVarargs
    final public Map<String, AbstractComponent> addEntity(final Class<? extends AbstractComponent>... components) {
        int entityId = create();
        Map<String, AbstractComponent> created = new HashMap<>();
        for (var comp : components) {
            created.put(comp.getName(), edit(entityId).create(comp));
        }
        return created;
    }

    public <T extends AbstractComponent> T addComponent(int entity, Class<T> component) {
        return edit(entity).create(component);
    }

    @Bean
    protected PreLoopSystem getPreLoopSystem() {
        return getSystem(PreLoopSystem.class);
    }

    @Bean
    protected GarbageCollectorSystem getGarbageCollectorSystem() {
        return getSystem(GarbageCollectorSystem.class);
    }

    @Bean
    protected ScriptExecutorSystem getScriptExecutorSystem() {
        return getSystem(ScriptExecutorSystem.class);
    }

    @Bean
    protected DirectionalShadowSystem getDirectionalShadowSystem() {
        return getSystem(DirectionalShadowSystem.class);
    }

    @Bean
    protected OmniShadowSystem getOmniShadowSystem() {
        return getSystem(OmniShadowSystem.class);
    }

    @Bean
    protected VisibilityRendererSystem getVisibilityRendererSystem() {
        return getSystem(VisibilityRendererSystem.class);
    }

    @Bean
    protected AmbientOcclusionSystem getAmbientOcclusionSystem() {
        return getSystem(AmbientOcclusionSystem.class);
    }

    @Bean
    protected PreRendererSystem getPreRendererSystem() {
        return getSystem(PreRendererSystem.class);
    }

    @Bean
    protected AtmosphereRendererSystem getAtmosphereRendererSystem() {
        return getSystem(AtmosphereRendererSystem.class);
    }

    @Bean
    protected TerrainRendererSystem getTerrainRendererSystem() {
        return getSystem(TerrainRendererSystem.class);
    }

    @Bean
    protected OpaqueRendererSystem getOpaqueRendererSystem() {
        return getSystem(OpaqueRendererSystem.class);
    }

    @Bean
    protected DecalRendererSystem getDecalRendererSystem() {
        return getSystem(DecalRendererSystem.class);
    }

    @Bean
    protected SpriteRenderer getSpriteRenderer() {
        return getSystem(SpriteRenderer.class);
    }

    @Bean
    protected PostRendererSystem getPostRendererSystem() {
        return getSystem(PostRendererSystem.class);
    }

    @Bean
    protected TransparencyRendererSystem getTransparencyRendererSystem() {
        return getSystem(TransparencyRendererSystem.class);
    }

    @Bean
    protected GlobalIlluminationSystem getGlobalIlluminationSystem() {
        return getSystem(GlobalIlluminationSystem.class);
    }

    @Bean
    protected BokehDOFSystem getBokehDOFSystem() {
        return getSystem(BokehDOFSystem.class);
    }

    @Bean
    protected MotionBlurSystem getMotionBlurSystem() {
        return getSystem(MotionBlurSystem.class);
    }

    @Bean
    protected BloomSystem getBloomSystem() {
        return getSystem(BloomSystem.class);
    }

    @Bean
    protected PostProcessingSystem getPostProcessingSystem() {
        return getSystem(PostProcessingSystem.class);
    }

    @Bean
    protected CompositionSystem getCompositionSystem() {
        return getSystem(CompositionSystem.class);
    }
}











































