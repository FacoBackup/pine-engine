package com.pine.engine.tools.system;

import com.pine.engine.Engine;
import com.pine.engine.core.repository.CoreResourceRepository;
import com.pine.engine.core.EngineDependency;
import com.pine.engine.core.repository.EngineUtils;
import com.pine.engine.core.service.resource.MeshService;
import com.pine.engine.core.service.resource.ShaderService;
import com.pine.engine.core.service.resource.primitives.GLSLType;
import com.pine.engine.core.service.resource.primitives.mesh.MeshRenderingMode;
import com.pine.engine.core.service.resource.primitives.mesh.MeshRuntimeData;
import com.pine.engine.core.service.resource.shader.ShaderRuntimeData;
import com.pine.engine.core.service.resource.shader.UniformDTO;
import com.pine.engine.core.system.AbstractSystem;
import com.pine.engine.tools.ExecutionEnvironment;
import com.pine.engine.tools.ToolsConfigurationModule;
import com.pine.engine.tools.repository.ToolsResourceRepository;
import org.lwjgl.opengl.GL46;


public class GridSystem extends AbstractSystem {
    private static final MeshRuntimeData DRAW_COMMAND = new MeshRuntimeData(MeshRenderingMode.TRIANGLES);

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
    private final ShaderRuntimeData uniforms = new ShaderRuntimeData();

    private UniformDTO depthUniform;
    private UniformDTO settingsUniform;

    @Override
    public void onInitialize() {
        settingsUniform = toolsResourceRepository.gridShader.addUniformDeclaration("settings", GLSLType.VEC_4);
        depthUniform = toolsResourceRepository.gridShader.addUniformDeclaration("sceneDepth", GLSLType.SAMPLER_2_D);
        uniforms.getUniformData().put("settings", buffer);
    }

    @Override
    public void render() {
        if (engineConfig.showGrid && engineConfig.environment == ExecutionEnvironment.DEVELOPMENT) {
//            resolution[0] = runtimeRepository.getViewportW();
//            resolution[1] = runtimeRepository.getViewportH();

            shaderService.bind(toolsResourceRepository.gridShader);

            buffer[0] = engineConfig.gridColor;
            buffer[1] = engineConfig.gridScale;
            buffer[2] = engineConfig.gridThreshold;
            buffer[3] = engineConfig.gridOpacity;

            GL46.glUniform4fv(settingsUniform.getLocation(), buffer);
            EngineUtils.bindTexture2d(depthUniform.getLocation(), 0, coreResourceRepository.sceneDepthVelocity); // TODO - GET TEXTURE

            coreResourceRepository.finalFrame.startMapping(false);
            meshService.bind(coreResourceRepository.planeMesh, DRAW_COMMAND);
            coreResourceRepository.finalFrame.stop();
        }
    }
}
