package com.pine.service.system;


import com.pine.Engine;
import com.pine.MetricCollector;
import com.pine.injection.PInject;
import com.pine.repository.*;
import com.pine.repository.core.*;
import com.pine.repository.rendering.RenderingRepository;
import com.pine.service.resource.SSBOService;
import com.pine.service.resource.ShaderService;
import com.pine.service.resource.UBOService;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.streaming.impl.MaterialService;
import com.pine.service.streaming.impl.MeshService;

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
    public ShaderService shaderService;
    @PInject
    public MaterialService materialService;
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
    public CoreMeshRepository meshRepository;
    @PInject
    public VoxelRepository voxelRepository;

    final public void render() {
        if (!isRenderable()) {
            return;
        }
        startTracking();
        FrameBufferObject fbo = getTargetFBO();
        shaderService.bind(getShader());
        if (fbo != null) {
            fbo.startMapping(shouldClearFBO());
            renderInternal();
            fbo.stop();
        } else {
            renderInternal();
        }
        endTracking();
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

    final protected UniformDTO addUniformDeclaration(String name) {
        if (getShader() != null) {
            return getShader().addUniformDeclaration(name);
        }
        return null;
    }

    protected Shader getShader() {
        return null;
    }
}
