package com.pine.engine;

import com.pine.common.MetricCollector;
import com.pine.common.injection.Disposable;
import com.pine.common.injection.PBean;
import com.pine.common.injection.PInject;
import com.pine.common.messaging.Loggable;
import com.pine.engine.repository.RuntimeRepository;
import com.pine.engine.repository.core.CoreBufferRepository;
import com.pine.engine.repository.core.CoreMeshRepository;
import com.pine.engine.repository.core.CoreShaderRepository;
import com.pine.engine.service.resource.fbo.FBO;
import com.pine.engine.service.system.SystemService;
import com.pine.engine.tasks.AbstractTask;
import com.pine.engine.tasks.SyncTask;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL46;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@PBean
public class Engine extends MetricCollector implements Disposable, Loggable {
    public static final float PI_OVER_2 = (float) (Math.PI / 2);
    public static final int MAX_LIGHTS = 310;
    private FBO targetFBO;

    @PInject
    public SystemService systemsService;
    @PInject
    public CoreShaderRepository shaderRepository;
    @PInject
    public CoreBufferRepository bufferRepository;
    @PInject
    public CoreMeshRepository primitiveRepository;
    @PInject
    public RuntimeRepository runtimeRepository;
    @PInject
    public List<Disposable> disposables;
    @PInject
    public List<SyncTask> syncTasks;
    @PInject
    public List<AbstractTask> tasks;
    private boolean ready = false;
    private String targetDirectory;

    public void start(int displayW, int displayH, String targetDirectory) {
        runtimeRepository.setDisplayW(displayW);
        runtimeRepository.setDisplayH(displayH);
        runtimeRepository.setInvDisplayW(1f / displayW);
        runtimeRepository.setInvDisplayH(1f / displayH);

        setupGL();

        primitiveRepository.onInitialize();
        bufferRepository.onInitialize();
        shaderRepository.onInitialize();
        systemsService.onInitialize();

        targetFBO = bufferRepository.gBufferTarget;

        tasks.forEach(t -> {
            t.onInitialize();
            t.start();
        });

        this.targetDirectory = targetDirectory;
        createDirectories();

        ready = true;
    }

    private void createDirectories() {
        try {
            Path path = Paths.get(getResourceDirectory());
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            path = Paths.get(getMetadataDirectory());
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (Exception ex) {
            getLogger().error(ex.getMessage(), ex);
        }
    }

    private static void setupGL() {
        GL46.glEnable(GL46.GL_BLEND);
        GL46.glBlendFunc(GL46.GL_SRC_ALPHA, GL46.GL_ONE_MINUS_SRC_ALPHA);
        GL46.glEnable(GL46.GL_CULL_FACE);
        GL46.glCullFace(GL46.GL_BACK);
        GL46.glEnable(GL46.GL_DEPTH_TEST);
        GL46.glDepthFunc(GL46.GL_LESS);
        GL46.glFrontFace(GL46.GL_CCW);
    }

    public void render() {
        if (!ready) {
            return;
        }
        startTracking();
        for (FBO fbo : bufferRepository.all) {
            fbo.clear();
        }

        if (targetFBO != null) {
            targetFBO.clear();
        }

        for (var syncTask : syncTasks) {
            syncTask.sync();
        }
        endTracking();
        MetricCollector.shouldCollect = false;
    }

    public void setTargetFBO(@NotNull FBO fbo) {
        this.targetFBO = fbo;
    }

    public FBO getTargetFBO() {
        return targetFBO;
    }

    public String getMetadataDirectory() {
        return getResourceDirectory() + "metadata" + File.separator;
    }

    public String getResourceDirectory() {
        return targetDirectory + File.separator + "resources" + File.separator;
    }

    @Override
    public String getTitle() {
        return "Engine total";
    }

    @Override
    public void dispose() {
        disposables.forEach(Disposable::dispose);
    }
}