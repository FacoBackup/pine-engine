package com.pine.engine.core.service;

import com.pine.common.EngineComponent;
import com.pine.common.Renderable;
import com.pine.engine.Engine;
import com.pine.engine.core.system.InjectEngineDependency;
import com.pine.engine.core.ClockRepository;
import com.pine.engine.core.RuntimeRepository;
import com.pine.engine.core.modules.EngineExternalModule;
import com.pine.engine.core.service.camera.CameraService;
import com.pine.engine.core.service.entity.EntityService;
import com.pine.engine.core.service.loader.ResourceLoaderService;
import com.pine.engine.core.service.resource.ResourceService;
import com.pine.engine.core.system.ISystem;
import com.pine.engine.core.system.impl.*;
import com.pine.engine.tools.system.GridSystem;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SystemService implements EngineComponent, Renderable {
    private final Engine engine;
    private final Collection<EngineExternalModule> modules;
    private List<ISystem> systems = List.of(
            new PreLoopSystem(),
            new ScriptExecutorSystem(),
            new ShadowsSystem(),
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

    public SystemService(Engine engine, Map<String, EngineExternalModule> modules) {
        this.engine = engine;
        this.modules = modules.values();
    }

    @Override
    public void onInitialize() {
        for (var externalSystem : modules) {
            systems = externalSystem.getExternalSystems(systems);
        }
        for (var sys : systems) {
            injectSystemDependencies(sys);
            sys.onInitialize();
        }
    }

    private void injectSystemDependencies(ISystem system) {
        Field[] fields = system.getClass().getFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(InjectEngineDependency.class)) {
                if (field.getType() == ClockRepository.class) {
                    inject(system, field, engine.getClock());
                } else if (field.getType() == RuntimeRepository.class) {
                    inject(system, field, engine.getRuntimeRepository());
                }else if (field.getType() == EntityService.class) {
                    inject(system, field, engine.getEntityService());
                } else if (field.getType() == CameraService.class) {
                    inject(system, field, engine.getCameraService());
                } else if (field.getType() == SystemService.class) {
                    inject(system, field, engine.getSystemsService());
                } else if (field.getType() == ResourceService.class) {
                    inject(system, field, engine.getResourcesService());
                } else if (field.getType() == ResourceLoaderService.class) {
                    inject(system, field, engine.getResourceLoaderService());
                } else if (field.getType() == Engine.class) {
                    inject(system, field, engine);
                } else {
                    getLogger().warn("Trying module injection on class {}", system.getClass().getName());
                    for (var module : modules) {
                        if (field.getType() == module.getClass()) {
                            inject(system, field, module);
                        }
                    }
                }
            }
        }
    }

    private void inject(ISystem system, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(system, value);
        } catch (Exception e) {
            getLogger().error("Failed to inject dependency", e);
        }
    }

    @Override
    public void tick() {
        for (ISystem system : systems) {
            system.tick();
        }
    }

    @Override
    public void render() {
        for (ISystem system : systems) {
            system.render();
        }
    }
}