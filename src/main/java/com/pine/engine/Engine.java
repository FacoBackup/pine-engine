package com.pine.engine;

import com.pine.engine.core.EngineDependency;
import com.pine.engine.core.EngineInjector;
import com.pine.engine.core.modules.EngineExternalModule;
import com.pine.engine.core.repository.*;
import com.pine.engine.core.service.LightService;
import com.pine.engine.core.service.TransformationService;
import com.pine.engine.core.service.camera.CameraService;
import com.pine.engine.core.service.entity.EntityService;
import com.pine.engine.core.service.loader.ResourceLoaderService;
import com.pine.engine.core.service.resource.ResourceService;
import com.pine.engine.core.service.system.SystemService;

import java.util.List;

public class Engine {
    public static final String GLSL_VERSION = "#version 410";
    public static final int MAX_LIGHTS = 310;

    public final int displayW;
    public final int displayH;

    @SuppressWarnings("unused")
    private final EngineInjector engineInjector;

    @EngineDependency
    public ModulesService modules;

    @EngineDependency
    public EngineRepository engineRepository;

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

    @EngineDependency
    public TransformationService transformationService;

    @EngineDependency
    public LightService lightService;

    public Engine(int displayW, int displayH) {
        this.displayW = displayW;
        this.displayH = displayH;
        engineInjector = new EngineInjector(this);
    }

    public void render() {
        clock.tick();
        systemsService.render();
    }

    public void shutdown() {
        resourcesService.shutdown();
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

    public EngineRepository getEngineRepository() {
        return engineRepository;
    }

    public TransformationService getTransformationService() {
        return transformationService;
    }

    public LightService getLightService() {
        return lightService;
    }
}
