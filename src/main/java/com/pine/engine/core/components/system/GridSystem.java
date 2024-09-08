package com.pine.engine.core.components.system;

import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.pine.engine.Engine;
import com.pine.engine.EngineUtils;
import com.pine.engine.ExecutionEnvironment;
import com.pine.engine.core.ConfigurationRepository;
import com.pine.engine.core.RuntimeRepository;
import com.pine.engine.core.service.resource.MeshService;
import com.pine.engine.core.service.resource.ShaderService;
import com.pine.engine.core.service.resource.primitives.GLSLType;
import com.pine.engine.core.service.resource.primitives.mesh.Mesh;
import com.pine.engine.core.service.resource.primitives.mesh.MeshRenderingMode;
import com.pine.engine.core.service.resource.primitives.mesh.MeshRuntimeData;
import com.pine.engine.core.service.resource.primitives.shader.Shader;
import com.pine.engine.core.service.resource.primitives.shader.ShaderRuntimeData;
import com.pine.engine.core.service.resource.primitives.shader.UniformDTO;
import org.lwjgl.opengl.GL46;


@All
public class GridSystem extends IteratingSystem implements ISystem {
    private Engine engine;
    private Mesh plane;
    private ConfigurationRepository engineConfig;
    private final float[] buffer = new float[4];
    private Shader gridShader;
    private ShaderService shaderService;
    private MeshService meshService;
    private ShaderRuntimeData uniforms = new ShaderRuntimeData();
    private final MeshRuntimeData meshRuntimeData = new MeshRuntimeData(MeshRenderingMode.TRIANGLES);
    private UniformDTO depthUniform;
    private UniformDTO settingsUniform;
    private UniformDTO resolutionUniform;
    private final float[] resolution = new float[2];
    private RuntimeRepository runtimeRepository;

    @Override
    public void setEngine(Engine engine) {
        this.engine = engine;
        runtimeRepository = engine.getRuntimeRepository();
        plane = (Mesh) engine.getResources().getById(runtimeRepository.planeMeshId);
        engineConfig = engine.getConfigurationRepository();

        gridShader = (Shader) engine.getResources().getById(runtimeRepository.gridShaderId);
        settingsUniform = gridShader.addUniformDeclaration("settings", GLSLType.vec4);
        depthUniform = gridShader.addUniformDeclaration("sceneDepth", GLSLType.sampler2D);
        resolutionUniform = gridShader.addUniformDeclaration("resolution", GLSLType.vec2);

        uniforms.getUniformData().put("settings", buffer);

        shaderService = engine.getResources().getShaderService();
    }

    @Override
    protected void process(int id) {
        if (engineConfig.showGrid && engine.getEnvironment() == ExecutionEnvironment.DEVELOPMENT) {

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
