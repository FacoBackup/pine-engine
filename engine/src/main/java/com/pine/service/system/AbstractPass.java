package com.pine.service.system;


import com.pine.Engine;
import com.pine.MetricCollector;
import com.pine.injection.PInject;
import com.pine.repository.*;
import com.pine.repository.core.CoreBufferRepository;
import com.pine.repository.core.CoreMeshRepository;
import com.pine.repository.core.CoreShaderRepository;
import com.pine.repository.rendering.RenderingRepository;
import com.pine.repository.terrain.TerrainRepository;
import com.pine.service.grid.WorldService;
import com.pine.service.importer.ImporterService;
import com.pine.service.resource.SSBOService;
import com.pine.service.resource.ShaderService;
import com.pine.service.resource.UBOService;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.streaming.StreamingService;
import com.pine.service.streaming.impl.MaterialService;
import com.pine.service.streaming.impl.MeshService;

public abstract class AbstractPass extends MetricCollector {
    @PInject
    public AtmosphereRepository atmosphere;
    @PInject
    public WorldRepository world;
    @PInject
    public Engine engine;
    @PInject
    public StreamingService streamingService;
    @PInject
    public ImporterService importerService;
    @PInject
    public EngineRepository engineRepository;
    @PInject
    public TerrainRepository terrainRepository;
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
    public WorldService worldService;
    @PInject
    public RuntimeRepository runtimeRepository;
    @PInject
    public CoreBufferRepository bufferRepository;
    @PInject
    public CoreMeshRepository meshRepository;
    @PInject
    public VoxelRepository voxelRepository;

    protected void onBeforeRender(){}

    protected void onAfterRender(){}

    final public void render() {
        startTracking();
        onBeforeRender();
        if (!isRenderable()) {
            endTracking();
            return;
        }
        FrameBufferObject fbo = getTargetFBO();
        shaderService.bind(getShader());
        if (fbo != null) {
            fbo.startMapping(shouldClearFBO());
            renderInternal();
            fbo.stop();
        } else {
            renderInternal();
        }
        onAfterRender();
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
