package com.pine.service.system;


import com.pine.Engine;
import com.pine.MetricCollector;
import com.pine.injection.PInject;
import com.pine.repository.CameraRepository;
import com.pine.repository.ClockRepository;
import com.pine.repository.EngineSettingsRepository;
import com.pine.repository.RuntimeRepository;
import com.pine.repository.core.*;
import com.pine.repository.rendering.RenderingRepository;
import com.pine.repository.voxelization.VoxelRepository;
import com.pine.service.resource.ComputeService;
import com.pine.service.resource.SSBOService;
import com.pine.service.resource.ShaderService;
import com.pine.service.resource.UBOService;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.streaming.mesh.MeshService;

public abstract class AbstractPass extends MetricCollector {
    @PInject
    public Engine engine;
    @PInject
    public EngineSettingsRepository settingsRepository;
    @PInject
    public CameraRepository cameraRepository;
    @PInject
    public RenderingRepository renderingRepository;
    @PInject
    public ClockRepository clockRepository;
    @PInject
    public UBOService uboService;
    @PInject
    public ComputeService computeService;
    @PInject
    public ShaderService shaderService;
    @PInject
    public SSBOService ssboService;
    @PInject
    public MeshService meshService;
    @PInject
    public CoreShaderRepository shaderRepository;
    @PInject
    public RuntimeRepository runtimeRepository;
    @PInject
    public CoreSSBORepository ssboRepository;
    @PInject
    public CoreUBORepository uboRepository;
    @PInject
    public CoreFBORepository fboRepository;
    @PInject
    public CoreComputeRepository computeRepository;
    @PInject
    public CoreMeshRepository meshRepository;
    @PInject
    public VoxelRepository voxelRepository;

    final public void render() {
        if (!isRenderable()) {
            return;
        }
        start();
        FrameBufferObject fbo = getTargetFBO();
        if (fbo != null) {
            fbo.startMapping(shouldClearFBO());
            renderInternal();
            fbo.stop();
        } else {
            renderInternal();
        }
        end();
    }

    protected FrameBufferObject getTargetFBO() {
        return null;
    }

    protected void renderInternal() {
    }

    protected boolean isRenderable() {
        return true;
    }

    protected boolean shouldClearFBO() {
        return false;
    }

    public void onInitialize() {
    }
}
