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

public class InstanceMaskPaintingPass extends AbstractCursorPass {
    @PInject
    public EditorRepository engineConfig;

    @PInject
    public ToolsResourceRepository toolsResourceRepository;
    private UniformDTO xy;
    private UniformDTO viewportSize;
    private UniformDTO viewportOrigin;

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
        return false;// runtimeRepository.mousePressed && engineConfig.gizmoType == GizmoType.PAINT && engineConfig.environment == ExecutionEnvironment.DEVELOPMENT;
    }

    @Override
    protected UniformDTO xyUniform() {
        return xy;
    }

    @Override
    protected UniformDTO viewportOriginUniform() {
        return viewportOrigin;
    }

    @Override
    protected UniformDTO viewportSizeUniform() {
        return viewportSize;
    }

    @Override
    protected FrameBufferObject frameBufferObject() {
        return null;
    }

    @Override
    public String getTitle() {
        return "Painting gizmo";
    }
}
