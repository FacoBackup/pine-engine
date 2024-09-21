package com.pine;

import com.pine.injection.EngineExternalModule;
import com.pine.repository.ClockRepository;
import com.pine.repository.CoreResourceRepository;
import com.pine.repository.ModulesService;
import com.pine.repository.RuntimeRepository;
import com.pine.service.MessageService;
import com.pine.service.loader.ResourceLoaderService;
import com.pine.service.resource.ResourceService;
import com.pine.service.resource.fbo.FBO;
import com.pine.service.system.SystemService;
import com.pine.service.world.WorldService;
import com.pine.service.world.request.AbstractRequest;
import com.pine.tasks.RequestProcessingTask;

import java.util.List;
import java.util.function.BiConsumer;

@PBean
public class Engine {
    public static final String GLSL_VERSION = "#version 410";
    public static final int MAX_LIGHTS = 310;

    private int displayW;
    private int displayH;

    private FBO targetFBO;

    @PInject
    public ModulesService modules;

    @PInject
    public ClockRepository clock;

    @PInject
    public RuntimeRepository runtimeRepository;

    @PInject
    public ResourceService resourceService;

    @PInject
    public SystemService systemsService;

    @PInject
    public ResourceService resourcesService;

    @PInject
    public ResourceLoaderService resourceLoaderService;

    @PInject
    public CoreResourceRepository coreResourceRepository;

    @PInject
    public WorldService worldService;

    @PInject
    public MessageService messageService;

    @PInject
    public RequestProcessingTask requestTask;

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

    public void addModules(List<EngineExternalModule> modules) {
        this.modules.addModules(modules);
    }

    public void addRequest(AbstractRequest request) {
        requestTask.addRequest(request);
    }

    public ResourceService getResourceService() {
        return resourceService;
    }

    public FBO getTargetFBO() {
        return targetFBO;
    }

    public void setTargetFBO(FBO fbo) {
        if (this.targetFBO != null) {
            this.targetFBO.clear();
        }
        this.targetFBO = fbo;
    }

    public void prepare(int displayW, int displayH, BiConsumer<String, Boolean> onMessage) {
        this.displayW = displayW;
        this.displayH = displayH;
        this.messageService.setMessageCallback(onMessage);
        coreResourceRepository.initialize();
        systemsService.initialize();
    }

    public int getDisplayH() {
        return displayH;
    }

    public int getDisplayW() {
        return displayW;
    }
}
