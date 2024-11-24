package com.pine.editor.tools.system;

import com.pine.common.injection.PInject;
import com.pine.editor.repository.EditorRepository;
import com.pine.engine.service.resource.fbo.FBO;
import com.pine.engine.service.resource.shader.Shader;
import com.pine.engine.service.resource.shader.UniformDTO;
import com.pine.engine.service.system.impl.AbstractQuadPass;
import com.pine.editor.tools.repository.ToolsResourceRepository;
import com.pine.editor.tools.types.ExecutionEnvironment;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL46;


public class GridPass extends AbstractQuadPass {
    @PInject
    public EditorRepository editorRepository;

    @PInject
    public ToolsResourceRepository toolsResourceRepository;

    private final Vector4f settings = new Vector4f();
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
    protected FBO getTargetFBO() {
        return bufferRepository.postProcessingBuffer;
    }

    @Override
    protected void bindUniforms() {
        GL46.glEnable(GL46.GL_BLEND);
        GL46.glDisable(GL46.GL_CULL_FACE);
        settings.set(
                editorRepository.gridOverlayObjects ? 1 : 0,
                editorRepository.gridScale,
                editorRepository.gridThreshold,
                editorRepository.gridThickness/100
        );

        shaderService.bindVec4(settings, settingsUniform);
        shaderService.bindSampler2d(bufferRepository.gBufferDepthIndexSampler, depthUniform);
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
