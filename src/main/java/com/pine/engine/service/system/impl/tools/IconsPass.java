package com.pine.engine.service.system.impl.tools;

import com.pine.common.injection.PInject;
import com.pine.editor.repository.EditorRepository;
import com.pine.engine.repository.rendering.RenderingMode;
import com.pine.engine.service.resource.fbo.FBO;
import com.pine.engine.service.resource.shader.Shader;
import com.pine.engine.service.resource.shader.UniformDTO;
import com.pine.engine.service.system.AbstractPass;
import com.pine.engine.type.ExecutionEnvironment;
import org.lwjgl.opengl.GL46;


public class IconsPass extends AbstractPass {
    @PInject
    public EditorRepository editorRepository;

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
    protected FBO getTargetFBO() {
        return bufferRepository.gBuffer;
    }

    @Override
    protected void renderInternal() {
        GL46.glEnable(GL46.GL_BLEND);
        GL46.glDisable(GL46.GL_CULL_FACE);
        GL46.glDisable(GL46.GL_DEPTH_TEST);

        shaderService.bindFloat(editorRepository.iconScale, iconScale);
        shaderService.bindVec3(editorRepository.iconColor, iconColor);

        shaderService.bindSampler2dDirect(bufferRepository.icons, 0);
        shaderService.bindSampler2dDirect(bufferRepository.sceneDepthCopySampler, 1);

        meshService.bind(meshRepository.quadMesh);
        meshService.setRenderingMode(RenderingMode.TRIANGLES);
        meshService.setInstanceCount(0);

        int index = 0;
        for (var tile : worldService.getLoadedTiles()) {
            if (tile != null) {
                for (var entityId : tile.getEntities()) {
                    if (world.bagDecalComponent.containsKey(entityId)) {
                        renderIcon(entityId, 6, index);
                        index++;
                    }

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
        return shaderRepository.iconShader;
    }

    @Override
    public String getTitle() {
        return "World Icons";
    }
}