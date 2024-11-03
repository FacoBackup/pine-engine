package com.pine.repository.core;

import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.service.resource.ShaderService;
import com.pine.service.resource.shader.Shader;

@PBean
public class CoreShaderRepository implements CoreRepository {
    public Shader spriteShader;
    public Shader gBufferShader;
    public Shader gBufferTerrainShader;
    public Shader gBufferInstanceShader;
    public Shader foliageCullingShader;
    public Shader toScreenShader;
    public Shader downscaleShader;
    public Shader bilateralBlurShader;
    public Shader bokehShader;
    public Shader irradianceShader;
    public Shader prefilteredShader;
    public Shader ssgiShader;
    public Shader mbShader;
    public Shader ssaoShader;
    public Shader boxBlurShader;
    public Shader directShadowsShader;
    public Shader omniDirectShadowsShader;
    public Shader frameComposition;
    public Shader bloomShader;
    public Shader postProcessing;
    public Shader gaussianShader;
    public Shader upSamplingShader;
    public Shader atmosphereShader;
    public Shader brdfShader;
    public Shader gBufferShading;
    public Shader environmentMap;
    public Shader voxelRaymarchingCompute;
    public Shader meshletCompute;

    @PInject
    public ShaderService shaderService;

    @Override
    public void initialize() {
        gBufferShading = shaderService.create("QUAD.vert", "uber/G_BUFFER_SHADING.frag");
        brdfShader = shaderService.create("QUAD.vert", "BRDF_GEN.frag");
        spriteShader = shaderService.create("SPRITE.vert", "SPRITE.frag");
        gBufferShader = shaderService.create("uber/G_BUFFER.vert", "uber/G_BUFFER.frag");
        gBufferTerrainShader = shaderService.create("uber/G_BUFFER_TERRAIN.vert", "uber/G_BUFFER.frag");
        gBufferInstanceShader = shaderService.create("uber/G_BUFFER_FOLIAGE.vert", "uber/G_BUFFER.frag");
        foliageCullingShader = shaderService.create("compute/FOLIAGE_CULLING_COMPUTE.glsl");
        toScreenShader = shaderService.create("QUAD.vert", "TO_SCREEN.frag");
        downscaleShader = shaderService.create("QUAD.vert", "BILINEAR_DOWNSCALE.glsl");
        bilateralBlurShader = shaderService.create("QUAD.vert", "BILATERAL_BLUR.glsl");
        bokehShader = shaderService.create("QUAD.vert", "BOKEH.frag");
        irradianceShader = shaderService.create("CUBEMAP.vert", "IRRADIANCE_MAP.frag");
        prefilteredShader = shaderService.create("CUBEMAP.vert", "PREFILTERED_MAP.frag");
        ssgiShader = shaderService.create("QUAD.vert", "SSGI.frag");
        mbShader = shaderService.create("QUAD.vert", "MOTION_BLUR.frag");
        ssaoShader = shaderService.create("QUAD.vert", "SSAO.frag");
        boxBlurShader = shaderService.create("QUAD.vert", "BOX-BLUR.frag");
        directShadowsShader = shaderService.create("SHADOWS.vert", "DIRECTIONAL_SHADOWS.frag");
        omniDirectShadowsShader = shaderService.create("SHADOWS.vert", "OMNIDIRECTIONAL_SHADOWS.frag");
        frameComposition = shaderService.create("QUAD.vert", "FRAME_COMPOSITION.frag");
        bloomShader = shaderService.create("QUAD.vert", "BRIGHTNESS_FILTER.frag");
        postProcessing = shaderService.create("QUAD.vert", "LENS_POST_PROCESSING.frag");
        gaussianShader = shaderService.create("QUAD.vert", "GAUSSIAN.frag");
        upSamplingShader = shaderService.create("QUAD.vert", "UPSAMPLE_TENT.glsl");
        atmosphereShader = shaderService.create("QUAD.vert", "ATMOSPHERE.frag");
        environmentMap = shaderService.create("uber/ENVIRONMENT_MAP.vert", "uber/ENVIRONMENT_MAP.frag");
        voxelRaymarchingCompute = shaderService.create("compute/VOXEL_RAY_MARCHING_COMPUTE.glsl");
        meshletCompute = shaderService.create("compute/MESHLET_COMPUTE.glsl");
    }

    @Override
    public void dispose() {
        gBufferTerrainShader.dispose();
        foliageCullingShader.dispose();
        gBufferShading.dispose();
        brdfShader.dispose();
        spriteShader.dispose();
        gBufferShader.dispose();
        toScreenShader.dispose();
        downscaleShader.dispose();
        bilateralBlurShader.dispose();
        bokehShader.dispose();
        irradianceShader.dispose();
        prefilteredShader.dispose();
        ssgiShader.dispose();
        mbShader.dispose();
        ssaoShader.dispose();
        boxBlurShader.dispose();
        directShadowsShader.dispose();
        omniDirectShadowsShader.dispose();
        frameComposition.dispose();
        bloomShader.dispose();
        postProcessing.dispose();
        gaussianShader.dispose();
        upSamplingShader.dispose();
        atmosphereShader.dispose();
        environmentMap.dispose();
        voxelRaymarchingCompute.dispose();
        meshletCompute.dispose();
    }
}
