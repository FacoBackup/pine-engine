package com.pine.engine.tools.system;

import com.pine.engine.Engine;
import com.pine.engine.core.system.InjectEngineDependency;
import com.pine.engine.core.EngineUtils;
import com.pine.engine.core.RuntimeRepository;
import com.pine.engine.core.service.resource.MeshService;
import com.pine.engine.core.service.resource.ResourceService;
import com.pine.engine.core.service.resource.ShaderService;
import com.pine.engine.core.service.resource.primitives.GLSLType;
import com.pine.engine.core.service.resource.primitives.mesh.Mesh;
import com.pine.engine.core.service.resource.primitives.mesh.MeshRenderingMode;
import com.pine.engine.core.service.resource.primitives.mesh.MeshRuntimeData;
import com.pine.engine.core.service.resource.primitives.shader.Shader;
import com.pine.engine.core.service.resource.primitives.shader.ShaderRuntimeData;
import com.pine.engine.core.service.resource.primitives.shader.UniformDTO;
import com.pine.engine.core.system.AbstractSystem;
import com.pine.engine.tools.ExecutionEnvironment;
import com.pine.engine.tools.ToolsConfigurationModule;
import org.lwjgl.opengl.GL46;


public class GridSystem extends AbstractSystem {
    @InjectEngineDependency
    public Engine engine;

    @InjectEngineDependency
    public ToolsConfigurationModule engineConfig;

    @InjectEngineDependency
    public ShaderService shaderService;

    @InjectEngineDependency
    public MeshService meshService;

    @InjectEngineDependency
    public RuntimeRepository runtimeRepository;

    @InjectEngineDependency
    public ResourceService resourceService;

    private final float[] buffer = new float[4];
    private final float[] resolution = new float[2];
    private final ShaderRuntimeData uniforms = new ShaderRuntimeData();
    private final MeshRuntimeData meshRuntimeData = new MeshRuntimeData(MeshRenderingMode.TRIANGLES);
    private Mesh plane;
    private Shader gridShader;
    private UniformDTO depthUniform;
    private UniformDTO settingsUniform;
    private UniformDTO resolutionUniform;

    @Override
    public void onInitialize() {
        plane = (Mesh) resourceService.getById(runtimeRepository.planeMeshId);
        gridShader = (Shader) resourceService.getById(runtimeRepository.gridShaderId);
        settingsUniform = gridShader.addUniformDeclaration("settings", GLSLType.vec4);
        depthUniform = gridShader.addUniformDeclaration("sceneDepth", GLSLType.sampler2D);
        resolutionUniform = gridShader.addUniformDeclaration("resolution", GLSLType.vec2);
        uniforms.getUniformData().put("settings", buffer);
    }

    @Override
    public void render() {
        if (engineConfig.showGrid && engineConfig.environment == ExecutionEnvironment.DEVELOPMENT) {

            resolution[0] = runtimeRepository.getViewportW();
            resolution[1] = runtimeRepository.getViewportH();

            shaderService.bind(gridShader);

            buffer[0] = engineConfig.gridColor;
            buffer[1] = engineConfig.gridScale;
            buffer[2] = engineConfig.gridThreshold;
            buffer[3] = engineConfig.gridOpacity;


            GL46.glUniform4fv(settingsUniform.getLocation(), buffer);
            EngineUtils.bindTexture2d(depthUniform.getLocation(), 0, 0); // TODO - GET TEXTURE
            GL46.glUniform2fv(resolutionUniform.getLocation(), resolution);

            meshService.bind(plane, meshRuntimeData);
        }
    }
}
