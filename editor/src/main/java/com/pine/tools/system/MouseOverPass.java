package com.pine.tools.system;

import com.pine.injection.PInject;
import com.pine.repository.EditorRepository;
import com.pine.repository.rendering.RenderingMode;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.GLSLType;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.system.AbstractPass;
import com.pine.service.system.impl.AbstractQuadPassPass;
import com.pine.tools.repository.ToolsResourceRepository;
import com.pine.tools.types.ExecutionEnvironment;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL46;


public class MouseOverPass extends AbstractQuadPassPass {
    private static final float RADIUS = 5;
    private static final float RADIUS_SQ_2 = (RADIUS * RADIUS) / 2;
    @PInject
    public EditorRepository engineConfig;

    @PInject
    public ToolsResourceRepository toolsResourceRepository;

    private UniformDTO metadataSampler;
    private UniformDTO xy;
    private UniformDTO viewportSize;
    private final Vector2f coord = new Vector2f();
    private final Vector2f viewportSizeV = new Vector2f();

    @Override
    public void onInitialize() {
        metadataSampler = getShader().addUniformDeclaration("metadataSampler", GLSLType.SAMPLER_2_D);
        xy = getShader().addUniformDeclaration("xy", GLSLType.VEC_2);
        viewportSize = getShader().addUniformDeclaration("viewportSize", GLSLType.VEC_2);
    }

    @Override
    protected boolean isRenderable() {
        return engineConfig.showGrid && engineConfig.environment == ExecutionEnvironment.DEVELOPMENT;
    }

    @Override
    protected boolean shouldClearFBO() {
        return true;
    }

    @Override
    protected FrameBufferObject getTargetFBO() {
        return toolsResourceRepository.mouseOverBuffer;
    }

    @Override
    protected Shader getShader() {
        return toolsResourceRepository.mouseOver;
    }

    @Override
    protected void bindUniforms() {
        coord.x = runtimeRepository.mouseX + runtimeRepository.viewportX;
        coord.y = runtimeRepository.viewportH - runtimeRepository.mouseY + runtimeRepository.viewportY;

        viewportSizeV.x = runtimeRepository.viewportW;
        viewportSizeV.y = runtimeRepository.viewportH;

        shaderService.bindSampler2d(fboRepository.auxSampler, metadataSampler);
        shaderService.bindVec2(coord, xy);
        shaderService.bindVec2(viewportSizeV, viewportSize);
    }

    @Override
    public String getTitle() {
        return "Mouse over";
    }
}
