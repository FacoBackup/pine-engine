package com.pine.tools.system;

import com.pine.injection.PInject;
import com.pine.repository.EditorRepository;
import com.pine.repository.rendering.RenderingMode;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.system.AbstractPass;
import com.pine.tools.repository.ToolsResourceRepository;
import com.pine.tools.types.ExecutionEnvironment;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL46;


public class GridPass extends AbstractPass {
    @PInject
    public EditorRepository editorRepository;

    @PInject
    public ToolsResourceRepository toolsResourceRepository;


    private final Vector4f buffer = new Vector4f();
    private UniformDTO depthUniform;
    private UniformDTO settingsUniform;

    @Override
    public void onInitialize() {
        settingsUniform = toolsResourceRepository.gridShader.addUniformDeclaration("settings");
        depthUniform = toolsResourceRepository.gridShader.addUniformDeclaration("sceneDepth");
    }

    @Override
    protected boolean isRenderable() {
        return editorRepository.showGrid && editorRepository.environment == ExecutionEnvironment.DEVELOPMENT;
    }

    @Override
    protected FrameBufferObject getTargetFBO() {
        return bufferRepository.postProcessingBuffer;
    }

    @Override
    protected void renderInternal() {
        GL46.glEnable(GL46.GL_BLEND);
        GL46.glDisable(GL46.GL_CULL_FACE);
        buffer.set(
                editorRepository.gridColor,
                editorRepository.gridScale,
                editorRepository.gridThreshold,
                editorRepository.gridOpacity
        );

        shaderService.bindVec4(buffer, settingsUniform);
        shaderService.bindSampler2d(bufferRepository.gBufferDepthIndexSampler, depthUniform);

        meshService.bind(meshRepository.planeMesh);
        meshService.setRenderingMode(RenderingMode.TRIANGLES);
        meshService.setInstanceCount(0);

        meshService.draw();
        GL46.glEnable(GL46.GL_CULL_FACE);
    }

    @Override
    protected Shader getShader() {
        return toolsResourceRepository.gridShader;
    }

    @Override
    public String getTitle() {
        return "Grid rendering";
    }
}
