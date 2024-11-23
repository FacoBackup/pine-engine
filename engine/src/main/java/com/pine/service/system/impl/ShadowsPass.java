package com.pine.service.system.impl;

import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.system.AbstractPass;
import org.lwjgl.opengl.GL46;

public class ShadowsPass extends AbstractPass {
    private long sinceLastRun;
    private UniformDTO modelMatrix;
    private UniformDTO textureSize;
    private UniformDTO tilesScaleTranslation;
    private UniformDTO heightScale;
    private UniformDTO terrainOffset;
    private UniformDTO lightSpaceMatrixTerrain;
    private UniformDTO lightSpaceMatrixPrimitive;

    @Override
    public void onInitialize() {
        modelMatrix = shaderRepository.shadowsPrimitiveShader.addUniformDeclaration("modelMatrix");
        lightSpaceMatrixPrimitive = shaderRepository.shadowsPrimitiveShader.addUniformDeclaration("lightSpaceMatrix");

        lightSpaceMatrixTerrain = shaderRepository.shadowsTerrainShader.addUniformDeclaration("lightSpaceMatrix");
        textureSize = shaderRepository.shadowsTerrainShader.addUniformDeclaration("textureSize");
        terrainOffset = shaderRepository.shadowsTerrainShader.addUniformDeclaration("terrainOffset");
        tilesScaleTranslation = shaderRepository.shadowsTerrainShader.addUniformDeclaration("tilesScaleTranslation");
        heightScale = shaderRepository.shadowsTerrainShader.addUniformDeclaration("heightScale");
    }

    @Override
    protected boolean isRenderable() {
        if(bufferRepository.shadowsBuffer == null || bufferRepository.shadowsBuffer.width != engineRepository.worldShadowsResolution){
            if(bufferRepository.shadowsBuffer != null){
                bufferRepository.shadowsBuffer.dispose();
            }

            bufferRepository.shadowsBuffer = new FrameBufferObject(engineRepository.worldShadowsResolution, engineRepository.worldShadowsResolution).depthTexture();
            bufferRepository.shadowsSampler = bufferRepository.shadowsBuffer.depthTest().getDepthSampler();
        }
        return (clockRepository.totalTime - sinceLastRun) >= engineRepository.shadowsEveryMs;
    }

    @Override
    protected FrameBufferObject getTargetFBO() {
        return bufferRepository.shadowsBuffer;
    }

    @Override
    protected void renderInternal() {
        sinceLastRun = clockRepository.totalTime;

        if(atmosphere.shadows) {
            shaderService.bind(shaderRepository.shadowsTerrainShader);
            shaderService.bindMat4(atmosphere.lightSpaceMatrix, lightSpaceMatrixTerrain);
            meshService.renderTerrain(textureSize, terrainOffset, heightScale, tilesScaleTranslation);
        }
    }

    @Override
    public String getTitle() {
        return "Shadows";
    }
}
