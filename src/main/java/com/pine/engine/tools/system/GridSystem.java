package com.pine.engine.tools.system;

import com.pine.engine.Engine;
import com.pine.engine.core.EngineDependency;
import com.pine.engine.core.EngineUtils;
import com.pine.engine.core.repository.CoreResourceRepository;
import com.pine.engine.core.service.resource.MeshService;
import com.pine.engine.core.service.resource.ShaderService;
import com.pine.engine.core.service.resource.fbo.FBO;
import com.pine.engine.core.service.resource.primitives.GLSLType;
import com.pine.engine.core.service.resource.shader.ShaderRuntimeData;
import com.pine.engine.core.service.resource.shader.UniformDTO;
import com.pine.engine.core.service.system.AbstractSystem;
import com.pine.engine.tools.ExecutionEnvironment;
import com.pine.engine.tools.ToolsConfigurationModule;
import com.pine.engine.tools.repository.ToolsResourceRepository;
import org.lwjgl.opengl.GL46;


public class GridSystem extends AbstractSystem {
    @EngineDependency
    public Engine engine;

    @EngineDependency
    public ToolsConfigurationModule engineConfig;

    @EngineDependency
    public ShaderService shaderService;

    @EngineDependency
    public MeshService meshService;

    @EngineDependency
    public CoreResourceRepository coreResourceRepository;

    @EngineDependency
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
    protected FBO getTargetFBO() {
        return coreResourceRepository.finalFrame;
    }

    @Override
    protected void renderInternal() {
        shaderService.bind(toolsResourceRepository.gridShader);
        buffer[0] = engineConfig.gridColor;
        buffer[1] = engineConfig.gridScale;
        buffer[2] = engineConfig.gridThreshold;
        buffer[3] = engineConfig.gridOpacity;

        GL46.glUniform4fv(settingsUniform.getLocation(), buffer);
        EngineUtils.bindTexture2d(depthUniform.getLocation(), 0, coreResourceRepository.sceneDepthVelocity);

        meshService.bind(coreResourceRepository.planeMesh);
        shaderService.unbind();
        meshService.unbind();
    }
}
