package com.pine.tools.system;

import com.pine.EngineUtils;
import com.pine.PInject;
import com.pine.repository.EditorStateRepository;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.primitives.GLSLType;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.system.AbstractSystem;
import com.pine.tools.repository.ToolsResourceRepository;
import com.pine.tools.types.ExecutionEnvironment;
import org.lwjgl.opengl.GL46;


public class GridSystem extends AbstractSystem {
    @PInject
    public EditorStateRepository engineConfig;

    @PInject
    public ToolsResourceRepository toolsResourceRepository;

    private final float[] buffer = new float[4];

    private UniformDTO depthUniform;
    private UniformDTO settingsUniform;

    @Override
    public void onInitialize() {
        settingsUniform = toolsResourceRepository.gridShader.addUniformDeclaration("settings", GLSLType.VEC_4);
        depthUniform = toolsResourceRepository.gridShader.addUniformDeclaration("sceneDepth", GLSLType.SAMPLER_2_D);
    }

    @Override
    protected boolean isRenderable() {
        return engineConfig.showGrid && engineConfig.environment == ExecutionEnvironment.DEVELOPMENT;
    }

    @Override
    protected FrameBufferObject getTargetFBO() {
        return engine.getTargetFBO();
    }

    @Override
    protected void renderInternal() {
        shaderService.bind(toolsResourceRepository.gridShader);
        buffer[0] = engineConfig.gridColor;
        buffer[1] = engineConfig.gridScale;
        buffer[2] = engineConfig.gridThreshold;
        buffer[3] = engineConfig.gridOpacity;

        GL46.glUniform4fv(settingsUniform.getLocation(), buffer);
        EngineUtils.bindTexture2d(depthUniform.getLocation(), 0, fboRepository.sceneDepthSampler);

        primitiveService.bind(primitiveRepository.planeMesh);
    }
}
