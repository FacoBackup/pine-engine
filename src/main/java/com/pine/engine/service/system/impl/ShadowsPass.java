package com.pine.engine.service.system.impl;

import com.pine.engine.service.resource.fbo.FBO;
import com.pine.engine.service.resource.fbo.FBOCreationData;
import com.pine.engine.service.resource.shader.UniformDTO;
import com.pine.engine.service.system.AbstractPass;

public class ShadowsPass extends AbstractPass {
    private long sinceLastRun;
    private UniformDTO modelMatrix;
    private UniformDTO textureSize;
    private UniformDTO tilesScaleTranslation;
    private UniformDTO heightScale;
    private UniformDTO terrainOffset;

    @Override
    public void onInitialize() {
        modelMatrix = shaderRepository.shadowsPrimitiveShader.addUniformDeclaration("modelMatrix");
        textureSize = shaderRepository.shadowsTerrainShader.addUniformDeclaration("textureSize");
        terrainOffset = shaderRepository.shadowsTerrainShader.addUniformDeclaration("terrainOffset");
        tilesScaleTranslation = shaderRepository.shadowsTerrainShader.addUniformDeclaration("tilesScaleTranslation");
        heightScale = shaderRepository.shadowsTerrainShader.addUniformDeclaration("heightScale");
    }

    @Override
    protected boolean isRenderable() {
        if (bufferRepository.shadowsBuffer == null || bufferRepository.shadowsBuffer.width != engineRepository.sunShadowsResolution) {
            if (bufferRepository.shadowsBuffer != null) {
                fboService.dispose(bufferRepository.shadowsBuffer);
            }

            bufferRepository.shadowsBuffer = fboService.create(new FBOCreationData(engineRepository.sunShadowsResolution, engineRepository.sunShadowsResolution, true).addDepthSampler("Sun Shadows"));
            bufferRepository.shadowsSampler = bufferRepository.shadowsBuffer.getDepthSampler();
        }
        return (clockRepository.totalTime - sinceLastRun) >= engineRepository.updateSunShadowsEvery && atmosphere.shadows;
    }

    @Override
    protected FBO getTargetFBO() {
        return bufferRepository.shadowsBuffer;
    }

    @Override
    protected void renderInternal() {
        sinceLastRun = clockRepository.totalTime;
        if (terrainRepository.castsShadows) {
            shaderService.bind(shaderRepository.shadowsTerrainShader);
            meshService.renderTerrain(textureSize, terrainOffset, heightScale, tilesScaleTranslation);
        }
    }

    @Override
    public String getTitle() {
        return "Shadows";
    }
}
