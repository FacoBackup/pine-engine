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
import org.lwjgl.opengl.GL46;


public class IconsPass extends AbstractPass {
    @PInject
    public EditorRepository editorRepository;

    @PInject
    public ToolsResourceRepository toolsResourceRepository;

    private UniformDTO transformationMatrix;
    private UniformDTO iconScale;
    private UniformDTO imageIndex;
    private UniformDTO isSelected;
    private UniformDTO iconColor;
    private UniformDTO renderIndex;

    @Override
    public void onInitialize() {
        transformationMatrix = addUniformDeclaration("transformationMatrix");
        iconScale = addUniformDeclaration("iconScale");
        imageIndex = addUniformDeclaration("imageIndex");
        isSelected = addUniformDeclaration("isSelected");
        iconColor = addUniformDeclaration("iconColor");
        renderIndex = addUniformDeclaration("renderIndex");
    }

    @Override
    protected boolean isRenderable() {
        return editorRepository.showIcons && editorRepository.environment == ExecutionEnvironment.DEVELOPMENT;
    }

    @Override
    protected FrameBufferObject getTargetFBO() {
        return bufferRepository.gBuffer;
    }

    @Override
    protected void renderInternal() {
        GL46.glEnable(GL46.GL_BLEND);
        GL46.glDisable(GL46.GL_CULL_FACE);
        GL46.glDisable(GL46.GL_DEPTH_TEST);

        shaderService.bindFloat(editorRepository.iconScale, iconScale);
        shaderService.bindVec3(editorRepository.iconColor, iconColor);

        shaderService.bindSampler2dDirect(toolsResourceRepository.icons, 0);
//        shaderService.bindSampler2dDirect(bufferRepository.gBufferDepthIndexSampler, 1);

        meshService.bind(meshRepository.quadMesh);
        meshService.setRenderingMode(RenderingMode.TRIANGLES);
        meshService.setInstanceCount(0);

        int index = 0;
        for (var tile : worldService.getLoadedTiles()) {
            if (tile != null) {
                for (var entityId : tile.getEntities()) {
                    if (world.bagEnvironmentProbeComponent.containsKey(entityId)) {
                        renderIcon(entityId, 3, index);
                        index++;
                    }

                    if (world.bagSphereLightComponent.containsKey(entityId)) {
                        renderIcon(entityId, 1, index);
                        index++;
                    }

                    if (world.bagPointLightComponent.containsKey(entityId)) {
                        renderIcon(entityId, 2, index);
                        index++;
                    }

                    if (world.bagSpotLightComponent.containsKey(entityId)) {
                        renderIcon(entityId, 4, index);
                        index++;
                    }
                }
            }
        }
        GL46.glEnable(GL46.GL_DEPTH_TEST);
        GL46.glEnable(GL46.GL_CULL_FACE);
    }

    private void renderIcon(String entityId, int iconIndex, int index) {
        shaderService.bindMat4(world.bagTransformationComponent.get(entityId).modelMatrix, transformationMatrix);
        shaderService.bindFloat(iconIndex, imageIndex);
        var entity = world.entityMap.get(entityId);
        entity.renderIndex = engineRepository.meshesDrawn + index;
        shaderService.bindInt(entity.renderIndex, renderIndex);
        shaderService.bindBoolean(editorRepository.selected.containsKey(entityId), isSelected);
        meshService.draw();
    }

    @Override
    protected Shader getShader() {
        return toolsResourceRepository.iconShader;
    }

    @Override
    public String getTitle() {
        return "World Icons";
    }
}
