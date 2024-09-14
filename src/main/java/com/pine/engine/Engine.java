package com.pine.engine;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pine.common.Renderable;
import com.pine.engine.core.*;
import com.pine.engine.core.modules.EngineExternalModule;
import com.pine.engine.core.repository.*;
import com.pine.engine.core.service.SystemService;
import com.pine.engine.core.service.camera.CameraService;
import com.pine.engine.core.service.entity.EntityService;
import com.pine.engine.core.service.loader.ResourceLoaderService;
import com.pine.engine.core.service.resource.ResourceService;
import com.pine.engine.core.service.serialization.SerializableRepository;
import com.pine.engine.core.EngineDependency;

import java.util.List;

public class Engine extends SerializableRepository implements Renderable {
    public static final String GLSL_VERSION = "#version 410";
    public static final int MAX_LIGHTS = 310;

    public final int displayW;
    public final int displayH;

    @SuppressWarnings("unused")
    private final EngineInjector engineInjector = new EngineInjector(this);

    @EngineDependency
    public ModulesService modules;

    @EngineDependency
    public ClockRepository clock;

    @EngineDependency
    public RuntimeRepository runtimeRepository;

    @EngineDependency
    public CoreResourceRepository coreResourceRepository;

    @EngineDependency
    public ConfigurationRepository configuration;

    @EngineDependency
    public CameraService cameraService;

    @EngineDependency
    public SystemService systemsService;

    @EngineDependency
    public ResourceService resourcesService;

    @EngineDependency
    public ResourceLoaderService resourceLoaderService;

    @EngineDependency
    public EntityService entityService;

    public Engine(int displayW, int displayH) {
        this.displayW = displayW;
        this.displayH = displayH;
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

    public EntityService getEntityService() {
        return entityService;
    }

    public ConfigurationRepository getConfiguration() {
        return configuration;
    }

    public CoreResourceRepository getCoreResourceRepository() {
        return coreResourceRepository;
    }

    public int getFinalFrame() {
        return coreResourceRepository.finalFrameSampler;
    }

    public void addModules(List<EngineExternalModule> modules) {
        this.modules.addModules(modules);
    }
}
