package com.pine.engine;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pine.common.Renderable;
import com.pine.engine.core.ClockRepository;
import com.pine.engine.core.RuntimeRepository;
import com.pine.engine.core.modules.EngineExternalModule;
import com.pine.engine.core.service.SystemService;
import com.pine.engine.core.service.camera.CameraService;
import com.pine.engine.core.service.entity.EntityService;
import com.pine.engine.core.service.loader.ResourceLoaderService;
import com.pine.engine.core.service.resource.ResourceService;
import com.pine.engine.core.service.serialization.SerializableRepository;
import jakarta.annotation.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Engine extends SerializableRepository implements Renderable {
    transient private final Map<String, EngineExternalModule> modules = new HashMap<>();
    transient private final ClockRepository clock = new ClockRepository();
    transient private final RuntimeRepository runtimeRepository = new RuntimeRepository();
    private final CameraService cameraService = new CameraService(this);
    private final SystemService systemsService = new SystemService(this, modules);
    private final ResourceService resourcesService = new ResourceService(this);
    private final ResourceLoaderService resourceLoaderService = new ResourceLoaderService(this);
    private final EntityService entityService = new EntityService();

    public Engine(List<EngineExternalModule> modules) {
        modules.forEach(m -> {
            this.modules.put(m.getClass().getName(), m);
        });
    }

    public Engine() {
    }

    @Override
    public void onInitialize() {
        resourcesService.onInitialize();
        cameraService.onInitialize();
        resourceLoaderService.onInitialize();
        systemsService.onInitialize();
    }

    @Override
    public void tick() {
        clock.tick();
        cameraService.tick();
        resourcesService.tick();
        resourceLoaderService.tick();
        systemsService.tick();
    }

    @Override
    public void render() {
        systemsService.render();
    }

    public void shutdown() {
        resourcesService.shutdown();
    }

    @Override
    public JsonElement serializeData() {
        JsonArray arr = new JsonArray();
        arr.add(entityService.serialize().toString());
        arr.add(cameraService.serialize().toString());
        arr.add(resourceLoaderService.serialize().toString());
        return arr;
    }

    @Override
    protected void parseInternal(JsonElement data) {
        JsonArray json = data.getAsJsonArray();
        json.forEach(a -> {
            JsonObject obj = a.getAsJsonObject();
            if (entityService.isCompatible(obj)) {
                entityService.parse(obj);
            }
            if (resourceLoaderService.isCompatible(obj)) {
                resourceLoaderService.parse(obj);
            }
            if (cameraService.isCompatible(obj)) {
                cameraService.parse(obj);
            }
        });
    }

    public CameraService getCameraService() {
        return cameraService;
    }

    public RuntimeRepository getRuntimeRepository() {
        return runtimeRepository;
    }

    public ResourceService getResourcesService() {
        return resourcesService;
    }

    public SystemService getSystemsService() {
        return systemsService;
    }

    public ClockRepository getClock() {
        return clock;
    }

    public ResourceLoaderService getResourceLoaderService() {
        return resourceLoaderService;
    }

    @Nullable
    public EngineExternalModule getModule(Class<? extends EngineExternalModule> clazz) {
        return modules.get(clazz.getName());
    }

    public EntityService getEntityService() {
        return entityService;
    }
}
