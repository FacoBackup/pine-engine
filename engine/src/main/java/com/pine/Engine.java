package com.pine;

import com.pine.injection.EngineExternalModule;
import com.pine.repository.*;
import com.pine.service.MessageService;
import com.pine.service.resource.ResourceService;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.system.SystemService;
import com.pine.service.world.request.AbstractRequest;
import com.pine.tasks.RequestProcessingTask;

import java.util.List;
import java.util.function.BiConsumer;

@PBean
public class Engine {
    public static final String GLSL_VERSION = "#version 460 core";
    public static final int MAX_ENTITIES = 200000;
    public static final int MAX_LIGHTS = 310;

    private int displayW;
    private int displayH;

    private FrameBufferObject targetFBO;

    @PInject
    public ModulesService modules;
    @PInject
    public ClockRepository clock;
    @PInject
    public SystemService systemsService;
    @PInject
    public ResourceService resourcesService;
    @PInject
    public MessageService messageService;
    @PInject
    public RequestProcessingTask requestTask;
    @PInject
    public CoreShaderRepository shaderRepository;
    @PInject
    public CoreSSBORepository ssboRepository;
    @PInject
    public CoreUBORepository uboRepository;
    @PInject
    public CoreFBORepository fboRepository;
    @PInject
    public CoreComputeRepository computeRepository;
    @PInject
    public CorePrimitiveRepository primitiveRepository;

    public void prepare(int displayW, int displayH, BiConsumer<String, Boolean> onMessage) {
        this.displayW = displayW;
        this.displayH = displayH;
        this.messageService.setMessageCallback(onMessage);
        ssboRepository.initialize();
        uboRepository.initialize();
        fboRepository.initialize();
        shaderRepository.initialize();
        computeRepository.initialize();
        primitiveRepository.initialize();
        systemsService.initialize();
    }

    public void render() {
        clock.tick();
        resourcesService.tick();
        systemsService.tick();
    }

    public void shutdown() {
        resourcesService.shutdown();
    }

    public void addModules(List<EngineExternalModule> modules) {
        this.modules.addModules(modules);
    }

    public void addRequest(AbstractRequest request) {
        requestTask.addRequest(request);
    }


    public void setTargetFBO(FrameBufferObject fbo) {
        if (this.targetFBO != null) {
            this.targetFBO.clear();
        }
        this.targetFBO = fbo;
    }

    public FrameBufferObject getTargetFBO() {
        return targetFBO;
    }

    public int getDisplayH() {
        return displayH;
    }

    public int getDisplayW() {
        return displayW;
    }
}
