package com.pine.tools.system;

import com.pine.injection.PInject;
import com.pine.repository.EditorRepository;
import com.pine.repository.rendering.RenderingMode;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.GLSLType;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.system.AbstractPass;
import com.pine.tools.repository.ToolsResourceRepository;
import com.pine.tools.types.ExecutionEnvironment;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL46;


public class GridPass extends AbstractPass {
    @PInject
    public EditorRepository engineConfig;

    @PInject
    public ToolsResourceRepository toolsResourceRepository;


    private final Vector4f buffer = new Vector4f();
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
        GL46.glEnable(GL46.GL_BLEND);
        GL46.glDisable(GL46.GL_CULL_FACE);
        shaderService.bind(toolsResourceRepository.gridShader);
        buffer.set(
                engineConfig.gridColor,
                engineConfig.gridScale,
                engineConfig.gridThreshold,
                engineConfig.gridOpacity
        );

        shaderService.bindVec4(buffer, settingsUniform);
        shaderService.bindSampler2d(fboRepository.gBufferDepthSampler, depthUniform);

        meshService.bind(meshRepository.planeMesh);
        meshService.setRenderingMode(RenderingMode.TRIANGLES);
        meshService.setInstanceCount(0);

        meshService.draw();
        GL46.glEnable(GL46.GL_CULL_FACE);
    }

    @Override
    public String getTitle() {
        return "Grid rendering";
    }
}
