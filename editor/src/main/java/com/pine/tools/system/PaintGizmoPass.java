package com.pine.tools.system;

import com.pine.injection.PInject;
import com.pine.repository.EditorRepository;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.ComputeRuntimeData;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.system.AbstractPass;
import com.pine.tools.repository.ToolsResourceRepository;
import com.pine.tools.types.ExecutionEnvironment;
import org.joml.Vector2f;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL46;

public class PaintGizmoPass extends AbstractPass {
    private static final ComputeRuntimeData COMPUTE_RUNTIME_DATA = new ComputeRuntimeData();
    private static final int LOCAL_SIZE_X = 4;
    private static final int LOCAL_SIZE_Y = 4;

    @PInject
    public EditorRepository engineConfig;

    @PInject
    public ToolsResourceRepository toolsResourceRepository;
    private UniformDTO xy;
    private UniformDTO viewportSize;
    private UniformDTO viewportOrigin;
    private final Vector2f coord = new Vector2f();
    private final Vector2f viewportO = new Vector2f();
    private final Vector2f viewport = new Vector2f();

    @Override
    public void onInitialize() {
        xy = addUniformDeclaration("xy");
        viewportOrigin = addUniformDeclaration("viewportOrigin");
        viewportSize = addUniformDeclaration("viewportSize");
    }

    @Override
    protected Shader getShader() {
        return toolsResourceRepository.paintGizmoCompute;
    }

    @Override
    protected boolean isRenderable() {
        return engineConfig.showGrid && engineConfig.environment == ExecutionEnvironment.DEVELOPMENT;
    }

    @Override
    protected void renderInternal() {
        GL46.glEnable(GL11.GL_BLEND);
        FrameBufferObject fbo = fboRepository.postProcessingBuffer;
        fbo.bindForCompute();

        coord.x = runtimeRepository.mouseX ;
        coord.y = runtimeRepository.viewportH - runtimeRepository.mouseY;

        viewport.x = runtimeRepository.viewportW;
        viewport.y = runtimeRepository.viewportH;

        viewportO.x = runtimeRepository.viewportX;
        viewportO.y = runtimeRepository.viewportY;

        shaderService.bindVec2(coord, xy);
        shaderService.bindVec2(viewport, viewportSize);
        shaderService.bindVec2(viewportO, viewportOrigin);
        shaderService.bindSampler2dDirect(fboRepository.gBufferDepthIndexSampler, 1);
        shaderService.bindSampler2dDirect(fboRepository.gBufferNormalSampler, 2);

        COMPUTE_RUNTIME_DATA.groupX = (fbo.width + LOCAL_SIZE_X - 1) / LOCAL_SIZE_X;
        COMPUTE_RUNTIME_DATA.groupY = (fbo.height + LOCAL_SIZE_Y - 1) / LOCAL_SIZE_Y;
        COMPUTE_RUNTIME_DATA.groupZ = 1;
        COMPUTE_RUNTIME_DATA.memoryBarrier = GL46.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT;

        shaderService.dispatch(COMPUTE_RUNTIME_DATA);
    }

    @Override
    public String getTitle() {
        return "Paint gizmo";
    }
}
