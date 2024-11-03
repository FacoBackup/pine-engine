package com.pine.tools.system;

import com.pine.injection.PInject;
import com.pine.repository.EditorRepository;
import com.pine.repository.GizmoType;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.system.AbstractPass;
import com.pine.tools.repository.ToolsResourceRepository;
import com.pine.tools.types.ExecutionEnvironment;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL46;

import static com.pine.service.resource.ShaderService.COMPUTE_RUNTIME_DATA;

public abstract class AbstractCursorPass extends AbstractPass {
    private static final int LOCAL_SIZE_X = 1;
    private static final int LOCAL_SIZE_Y = 1;

    @PInject
    public EditorRepository engineConfig;

    @PInject
    public ToolsResourceRepository toolsResourceRepository;

    private final Vector2f coord = new Vector2f();
    private final Vector2f viewportO = new Vector2f();
    private final Vector2f viewport = new Vector2f();

    protected abstract UniformDTO xyUniform();
    protected abstract UniformDTO viewportOriginUniform();
    protected abstract UniformDTO viewportSizeUniform();

    protected abstract FrameBufferObject frameBufferObject();

    @Override
    protected void renderInternal() {
        frameBufferObject().bindForCompute();

        coord.x = runtimeRepository.mouseX ;
        coord.y = runtimeRepository.viewportH - runtimeRepository.mouseY;

        viewport.x = runtimeRepository.viewportW;
        viewport.y = runtimeRepository.viewportH;

        viewportO.x = runtimeRepository.viewportX;
        viewportO.y = runtimeRepository.viewportY;

        shaderService.bindVec2(coord, xyUniform());
        shaderService.bindVec2(viewport, viewportSizeUniform());
        shaderService.bindVec2(viewportO, viewportOriginUniform());
        shaderService.bindSampler2dDirect(fboRepository.gBufferDepthIndexSampler, 1);
        shaderService.bindSampler2dDirect(fboRepository.gBufferNormalSampler, 2);

        COMPUTE_RUNTIME_DATA.groupX = (frameBufferObject().width + LOCAL_SIZE_X - 1) / LOCAL_SIZE_X;
        COMPUTE_RUNTIME_DATA.groupY = (frameBufferObject().height + LOCAL_SIZE_Y - 1) / LOCAL_SIZE_Y;
        COMPUTE_RUNTIME_DATA.groupZ = 1;
        COMPUTE_RUNTIME_DATA.memoryBarrier = GL46.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT;

        shaderService.dispatch(COMPUTE_RUNTIME_DATA);
    }

    @Override
    public String getTitle() {
        return "Paint gizmo";
    }
}
