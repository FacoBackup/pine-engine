package com.pine.service.system.impl;

import com.pine.repository.DebugShadingModel;
import com.pine.repository.rendering.RenderingMode;
import com.pine.repository.rendering.RenderingRequest;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.system.AbstractPass;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL46;

import java.util.List;

public abstract class AbstractGBufferPass extends AbstractPass {
    private static final int MAX_CUBE_MAPS = 3;
    private UniformDTO debugShadingMode;
    private UniformDTO applyGrid;
    private UniformDTO probeFilteringLevels;

    @Override
    public void onInitialize() {
        debugShadingMode = addUniformDeclaration("debugShadingMode");
        applyGrid = addUniformDeclaration("applyGrid");
        probeFilteringLevels = addUniformDeclaration("probeFilteringLevels");
    }

    @Override
    protected FrameBufferObject getTargetFBO() {
        return fboRepository.gBuffer;
    }

    protected void prepareCall() {
        GL46.glEnable(GL11.GL_DEPTH_TEST);
        GL46.glDisable(GL11.GL_BLEND);
        if (settingsRepository.debugShadingModel == DebugShadingModel.WIREFRAME) {
            meshService.setRenderingMode(RenderingMode.WIREFRAME);
            GL46.glDisable(GL11.GL_CULL_FACE);
        } else {
            GL46.glEnable(GL11.GL_CULL_FACE);
            meshService.setRenderingMode(RenderingMode.TRIANGLES);
        }

        shaderService.bindInt(settingsRepository.debugShadingModel.getId(), debugShadingMode);
        shaderService.bindFloat(settingsRepository.probeFiltering, probeFilteringLevels);
        shaderService.bindBoolean(settingsRepository.gridOverlay, applyGrid);

        bindEnvironmentMaps();
    }

    private void bindEnvironmentMaps() {
        boolean isFirst = true;
        int samplerOffset = 0;
        for (int i = 0; i < renderingRepository.environmentMaps.length; i++) {
            var current = renderingRepository.environmentMaps[i];
            if (current != null && samplerOffset < MAX_CUBE_MAPS) {
                if (isFirst) {
                    shaderService.bindSamplerCubeDirectTriLinear(current.texture, samplerOffset);
                    samplerOffset++;
                    shaderService.bindSamplerCubeDirect(current.irradiance, samplerOffset);
                    samplerOffset++;
                } else {
                    shaderService.bindSamplerCubeDirectTriLinear(current.hasIrradianceGenerated ? current.irradiance : current.texture, samplerOffset);
                    samplerOffset++;
                }
                current.lastUse = clockRepository.totalTime;
                isFirst = false;
            }
        }
    }
}
