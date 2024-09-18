package com.pine;

import com.pine.core.EngineDependency;
import com.pine.core.EngineInjector;
import com.pine.core.modules.EngineExternalModule;
import com.pine.core.repository.*;
import com.pine.core.service.LightService;
import com.pine.core.service.MessageService;
import com.pine.core.service.TransformationService;
import com.pine.core.service.camera.CameraService;
import com.pine.core.service.loader.ResourceLoaderService;
import com.pine.core.service.resource.ResourceService;
import com.pine.core.service.system.SystemService;
import com.pine.core.service.world.WorldService;

import java.util.List;
import java.util.function.BiConsumer;

public class Engine {
    public static final String GLSL_VERSION = "#version 410";
    public static final int MAX_LIGHTS = 310;

    public final int displayW;
    public final int displayH;

    @SuppressWarnings("unused")
    private final EngineInjector engineInjector = new EngineInjector(this);

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
    public WorldService worldService;

    @EngineDependency
    public TransformationService transformationService;

    @EngineDependency
    public LightService lightService;

    @EngineDependency
    public MessageService messageService;

    public Engine(int displayW, int displayH, BiConsumer<String, Boolean> onMessage) {
        this.displayW = displayW;
        this.displayH = displayH;
        engineInjector.onInitialize();
        this.messageService.setMessageCallback(onMessage);
        systemsService.manualInitialization();
    }

    public void render() {
        clock.tick();
        resourcesService.tick();
        systemsService.tick();
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

    public WorldService getWorld() {
        return worldService;
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
