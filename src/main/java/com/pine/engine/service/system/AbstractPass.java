package com.pine.engine.service.system;


import com.pine.common.MetricCollector;
import com.pine.common.injection.PInject;
import com.pine.engine.Engine;
import com.pine.engine.repository.*;
import com.pine.engine.repository.core.CoreBufferRepository;
import com.pine.engine.repository.core.CoreMeshRepository;
import com.pine.engine.repository.core.CoreShaderRepository;
import com.pine.engine.repository.rendering.RenderingRepository;
import com.pine.engine.repository.terrain.TerrainRepository;
import com.pine.engine.service.importer.ImporterService;
import com.pine.engine.service.resource.fbo.FBO;
import com.pine.engine.service.resource.fbo.FBOService;
import com.pine.engine.service.resource.shader.Shader;
import com.pine.engine.service.resource.shader.ShaderService;
import com.pine.engine.service.resource.shader.UniformDTO;
import com.pine.engine.service.resource.ssbo.SSBOService;
import com.pine.engine.service.resource.ubo.UBOService;
import com.pine.engine.service.streaming.StreamingService;
import com.pine.engine.service.streaming.impl.MaterialService;
import com.pine.engine.service.streaming.impl.MeshService;
import com.pine.engine.service.streaming.impl.TextureService;
import com.pine.engine.service.world.WorldService;

public abstract class AbstractPass extends MetricCollector {
    @PInject
    public AtmosphereRepository atmosphere;
    @PInject
    public FBOService fboService;
    @PInject
    public WorldRepository world;
    @PInject
    public Engine engine;
    @PInject
    public TextureService textureService;
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
        FBO fbo = getTargetFBO();
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

    protected FBO getTargetFBO() {
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
