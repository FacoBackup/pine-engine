package com.pine.engine.service.system.impl.tools;

import com.pine.common.injection.PInject;
import com.pine.editor.repository.BrushMode;
import com.pine.editor.repository.EditorMode;
import com.pine.editor.repository.EditorRepository;
import com.pine.engine.service.resource.fbo.FBO;
import com.pine.engine.service.resource.shader.Shader;
import com.pine.engine.service.resource.shader.UniformDTO;
import com.pine.engine.service.system.impl.AbstractQuadPass;
import com.pine.engine.type.ExecutionEnvironment;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL46;

public class PaintGizmoRenderingPass extends AbstractQuadPass {
    @PInject
    public EditorRepository editorRepository;

    private UniformDTO xyMouseUniform;
    private UniformDTO viewportSize;
    private UniformDTO viewportOrigin;
    private UniformDTO radiusDensityUniform;
    private final Vector3f xyDown = new Vector3f();
    private final Vector3f radiusDensityMode = new Vector3f();
    private final Vector2f viewportO = new Vector2f();
    private final Vector2f viewport = new Vector2f();

    @Override
    public void onInitialize() {
        xyMouseUniform = addUniformDeclaration("xyMouse");
        radiusDensityUniform = addUniformDeclaration("radiusDensityMode");
        viewportOrigin = addUniformDeclaration("viewportOrigin");
        viewportSize = addUniformDeclaration("viewportSize");
    }

    @Override
    protected Shader getShader() {
        return shaderRepository.paintGizmoRenderingShader;
    }

    @Override
    protected FBO getTargetFBO() {
        return bufferRepository.postProcessingBuffer;
    }

    @Override
    protected boolean isRenderable() {
        return runtimeRepository.isFocused && editorRepository.editorMode != EditorMode.TRANSFORM && editorRepository.environment == ExecutionEnvironment.DEVELOPMENT;
    }

    @Override
    protected void bindUniforms() {
        GL46.glEnable(GL11.GL_BLEND);

        radiusDensityMode.x = editorRepository.brushRadius;
        radiusDensityMode.y = editorRepository.brushDensity;
        radiusDensityMode.z = editorRepository.brushMode == BrushMode.ADD ? 1 : -1;

        xyDown.x = runtimeRepository.mouseX;
        xyDown.y = runtimeRepository.viewportH - runtimeRepository.mouseY;
        xyDown.z = runtimeRepository.mousePressed ? 1 : 0;

        viewport.x = runtimeRepository.viewportW;
        viewport.y = runtimeRepository.viewportH;

        viewportO.x = runtimeRepository.viewportX;
        viewportO.y = runtimeRepository.viewportY;

        shaderService.bindVec3(radiusDensityMode, radiusDensityUniform);
        shaderService.bindVec3(xyDown, xyMouseUniform);
        shaderService.bindVec2(viewport, viewportSize);
        shaderService.bindVec2(viewportO, viewportOrigin);

        shaderService.bindSampler2dDirect(bufferRepository.gBufferDepthIndexSampler, 0);
    }

    @Override
    public String getTitle() {
        return "Paint gizmo rendering";
    }
}