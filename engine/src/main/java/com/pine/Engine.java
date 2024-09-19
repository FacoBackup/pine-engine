package com.pine;

import com.pine.annotation.EngineDependency;
import com.pine.annotation.EngineInjector;
import com.pine.annotation.EngineExternalModule;
import com.pine.repository.*;
import com.pine.service.world.request.AbstractRequest;
import com.pine.service.MessageService;
import com.pine.tasks.RequestProcessingTask;
import com.pine.service.loader.ResourceLoaderService;
import com.pine.service.resource.ResourceService;
import com.pine.service.system.SystemService;
import com.pine.service.world.WorldService;

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
    public ClockRepository clock;

    @EngineDependency
    public RuntimeRepository runtimeRepository;

    @EngineDependency
    public CoreResourceRepository coreResourceRepository;

    @EngineDependency
    public SystemService systemsService;

    @EngineDependency
    public ResourceService resourcesService;

    @EngineDependency
    public ResourceLoaderService resourceLoaderService;

    @EngineDependency
    public WorldService worldService;

    @EngineDependency
    public MessageService messageService;

    @EngineDependency
    public RequestProcessingTask requestTask;

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

    public RuntimeRepository getRuntimeRepository() {
        return runtimeRepository;
    }

    public ResourceLoaderService getResourceLoaderService() {
        return resourceLoaderService;
    }

    public WorldService getWorld() {
        return worldService;
    }

    public int getFinalFrame() {
        return coreResourceRepository.finalFrameSampler;
    }

    public void addModules(List<EngineExternalModule> modules) {
        this.modules.addModules(modules);
    }

    public void addRequest(AbstractRequest request) {
        requestTask.addRequest(request);
    }
}
