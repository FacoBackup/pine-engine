package com.pine;

import com.pine.injection.EngineExternalModule;
import com.pine.repository.*;
import com.pine.service.resource.ResourceService;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.system.SystemService;
import com.pine.tasks.SyncTask;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL46;

import java.util.List;

@PBean
public class Engine {
    public static final String GLSL_VERSION = "#version 460 core";
    public static final int MAX_ENTITIES = 200000;
    public static final int MAX_LIGHTS = 310;

    private int displayW;
    private int displayH;

    private int invDisplayW;
    private int invDisplayH;

    private FrameBufferObject targetFBO;

    @PInject
    public ModulesService modules;
    @PInject
    public SystemService systemsService;
    @PInject
    public ResourceService resourcesService;
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
    @PInject
    public WorldRepository worldRepository;
    @PInject
    public List<SyncTask> syncTasks;

    public void prepare(int displayW, int displayH) {
        this.displayW = displayW;
        this.displayH = displayH;
        this.invDisplayW = 1 / displayW;
        this.invDisplayH = 1 / displayH;
        GL46.glEnable(GL46.GL_BLEND);
        GL46.glBlendFunc(GL46.GL_SRC_ALPHA, GL46.GL_ONE_MINUS_SRC_ALPHA);
        GL46.glEnable(GL46.GL_CULL_FACE);
        GL46.glCullFace(GL46.GL_BACK);
        GL46.glEnable(GL46.GL_DEPTH_TEST);
        GL46.glDepthFunc(GL46.GL_LESS);
        GL46.glFrontFace(GL46.GL_CCW);
        primitiveRepository.initialize();
        ssboRepository.initialize();
        uboRepository.initialize();
        fboRepository.initialize();
        shaderRepository.initialize();
        computeRepository.initialize();
        systemsService.initialize();
        worldRepository.initialize();

        targetFBO = fboRepository.tempColorWithDepth;
    }

    public void render() {
        for (FrameBufferObject fbo : fboRepository.all) {
            fbo.clear();
        }
        if (targetFBO != null) {
            targetFBO.clear();
        }
        for (var syncTask : syncTasks) {
            syncTask.sync();
        }
    }

    public void shutdown() {
        resourcesService.shutdown();
    }

    public void addModules(List<EngineExternalModule> modules) {
        this.modules.addModules(modules);
    }

    public void setTargetFBO(@NotNull FrameBufferObject fbo) {
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

    public int getInvDisplayH() {
        return invDisplayH;
    }

    public int getInvDisplayW() {
        return invDisplayW;
    }
}
