package com.pine.engine.service.system.impl.gbuffer;

import com.pine.engine.repository.ShadingMode;
import com.pine.engine.repository.rendering.RenderingMode;
import com.pine.engine.service.system.AbstractPass;
import com.pine.engine.service.resource.fbo.FBO;
import com.pine.engine.service.resource.shader.UniformDTO;
import com.pine.engine.service.streaming.ref.MaterialResourceRef;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL46;

public abstract class AbstractGBufferPass extends AbstractPass {
    public static final int MAX_CUBE_MAPS = 3;
    private UniformDTO debugShadingMode;
    private UniformDTO probeFilteringLevels;
    private UniformDTO parallaxHeightScale;
    private UniformDTO parallaxLayers;
    private UniformDTO useParallax;
    private UniformDTO anisotropicRotation;
    private UniformDTO anisotropy;
    private UniformDTO clearCoat;
    private UniformDTO sheen;
    private UniformDTO sheenTint;
    private UniformDTO renderingMode;
    private UniformDTO ssrEnabled;
    private UniformDTO albedoColor;
    private UniformDTO roughnessMetallic;
    private UniformDTO useAlbedoRoughnessMetallicAO;
    private UniformDTO useNormalTexture;
    private UniformDTO fallbackMaterial;

    @Override
    public void onInitialize() {
        debugShadingMode = addUniformDeclaration("debugShadingMode");
        probeFilteringLevels = addUniformDeclaration("probeFilteringLevels");
        albedoColor = addUniformDeclaration("albedoColor");
        roughnessMetallic = addUniformDeclaration("roughnessMetallic");
        useAlbedoRoughnessMetallicAO = addUniformDeclaration("useAlbedoRoughnessMetallicAO");
        useNormalTexture = addUniformDeclaration("useNormalTexture");
        parallaxHeightScale = addUniformDeclaration("parallaxHeightScale");
        parallaxLayers = addUniformDeclaration("parallaxLayers");
        useParallax = addUniformDeclaration("useParallax");
        anisotropicRotation = addUniformDeclaration("anisotropicRotation");
        anisotropy = addUniformDeclaration("anisotropy");
        clearCoat = addUniformDeclaration("clearCoat");
        sheen = addUniformDeclaration("sheen");
        sheenTint = addUniformDeclaration("sheenTint");
        renderingMode = addUniformDeclaration("renderingMode");
        ssrEnabled = addUniformDeclaration("ssrEnabled");
        fallbackMaterial = addUniformDeclaration("fallbackMaterial");
    }

    @Override
    protected FBO getTargetFBO() {
        return bufferRepository.gBuffer;
    }

    protected void prepareCall() {
        GL46.glEnable(GL11.GL_DEPTH_TEST);
        GL46.glDisable(GL11.GL_BLEND);

        if (!engineRepository.isBakingEnvironmentMaps && engineRepository.shadingMode == ShadingMode.WIREFRAME) {
            meshService.setRenderingMode(RenderingMode.WIREFRAME);
            GL46.glDisable(GL11.GL_CULL_FACE);
        } else {
            GL46.glEnable(GL11.GL_CULL_FACE);
            meshService.setRenderingMode(RenderingMode.TRIANGLES);
        }

        if (engineRepository.isBakingEnvironmentMaps) {
            shaderService.bindInt(ShadingMode.LIT.getId(), debugShadingMode);
        } else {
            shaderService.bindInt(engineRepository.shadingMode.getId(), debugShadingMode);
            shaderService.bindFloat(engineRepository.probeFiltering, probeFilteringLevels);
            bindEnvironmentMaps();
        }
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

    protected void bindMaterial(MaterialResourceRef material) {
        if (material == null) {
            shaderService.bindBoolean(true, fallbackMaterial);
            return;
        }
        shaderService.bindBoolean(false, fallbackMaterial);

        material.anisotropicRotationUniform = anisotropicRotation;
        material.anisotropyUniform = anisotropy;
        material.clearCoatUniform = clearCoat;
        material.sheenUniform = sheen;
        material.sheenTintUniform = sheenTint;
        material.renderingModeUniform = renderingMode;
        material.ssrEnabledUniform = ssrEnabled;
        material.parallaxHeightScaleUniform = parallaxHeightScale;
        material.parallaxLayersUniform = parallaxLayers;
        material.useParallaxUniform = useParallax;

        material.albedoColorLocation = albedoColor;
        material.roughnessMetallicLocation = roughnessMetallic;
        material.useAlbedoRoughnessMetallicAO = useAlbedoRoughnessMetallicAO;
        material.useNormalTexture = useNormalTexture;

        material.albedoLocation = 3;
        material.roughnessLocation = 4;
        material.metallicLocation = 5;
        material.aoLocation = 6;
        material.normalLocation = 7;
        material.heightMapLocation = 8;

        materialService.bindMaterial(material);
    }
}
