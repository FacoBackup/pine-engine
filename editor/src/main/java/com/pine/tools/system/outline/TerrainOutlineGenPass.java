package com.pine.tools.system.outline;

import com.pine.injection.PInject;
import com.pine.repository.EditorMode;
import com.pine.repository.EditorRepository;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.system.AbstractPass;
import com.pine.tools.repository.ToolsResourceRepository;
import com.pine.tools.types.ExecutionEnvironment;


public class TerrainOutlineGenPass extends AbstractPass {
    @PInject
    public EditorRepository editorRepository;

    @PInject
    public ToolsResourceRepository toolsResourceRepository;

    private UniformDTO tilesScaleTranslation;
    private UniformDTO textureSize;
    private UniformDTO heightScale;
    private UniformDTO terrainOffset;

    @Override
    public void onInitialize() {
        terrainOffset = addUniformDeclaration("terrainOffset");
        textureSize = addUniformDeclaration("textureSize");
        tilesScaleTranslation = addUniformDeclaration("tilesScaleTranslation");
        heightScale = addUniformDeclaration("heightScale");
    }

    @Override
    protected boolean isRenderable() {
        return terrainRepository.enabled && editorRepository.editorMode != EditorMode.TRANSFORM && editorRepository.editorMode != EditorMode.MATERIAL && editorRepository.showOutline && editorRepository.environment == ExecutionEnvironment.DEVELOPMENT;
    }

    @Override
    protected Shader getShader() {
        return toolsResourceRepository.outlineTerrainGenShader;
    }

    @Override
    protected FrameBufferObject getTargetFBO() {
        return toolsResourceRepository.outlineBuffer;
    }

    @Override
    protected boolean shouldClearFBO() {
        return true;
    }

    @Override
    protected void renderInternal() {
        meshService.renderTerrain(textureSize, terrainOffset, heightScale, tilesScaleTranslation);
    }

    @Override
    public String getTitle() {
        return "Terrain Outline Generation";
    }
}
