package com.pine.tools.system;

import com.pine.Engine;
import com.pine.EngineUtils;
import com.pine.injection.EngineDependency;
import com.pine.repository.CoreResourceRepository;
import com.pine.service.resource.MeshService;
import com.pine.service.resource.ShaderService;
import com.pine.service.resource.fbo.FBO;
import com.pine.service.resource.primitives.GLSLType;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.system.AbstractSystem;
import com.pine.tools.ExecutionEnvironment;
import com.pine.tools.ToolsConfigurationModule;
import com.pine.tools.repository.ToolsResourceRepository;
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
        EngineUtils.bindTexture2d(depthUniform.getLocation(), 0, coreResourceRepository.sceneDepthVelocity);

        meshService.bind(coreResourceRepository.planeMesh);
        shaderService.unbind();
        meshService.unbind();
    }
}
