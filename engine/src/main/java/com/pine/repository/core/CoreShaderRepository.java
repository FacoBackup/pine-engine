package com.pine.repository.core;

import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.service.resource.ResourceService;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.ShaderCreationData;

import static com.pine.service.resource.shader.ShaderCreationData.LOCAL_SHADER;

@PBean
public class CoreShaderRepository implements CoreRepository {
    public Shader spriteShader;
    public Shader gBufferShader;
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
    public Shader lensShader;
    public Shader gaussianShader;
    public Shader upSamplingShader;
    public Shader atmosphereShader;
    public Shader terrainShader;
    public Shader brdfShader;
    public Shader debugVoxelShader;
    public Shader gBufferShading;

    @PInject
    public ResourceService resources;

    @Override
    public void initialize() {
        gBufferShading = (Shader) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "QUAD.vert", LOCAL_SHADER + "uber/G_BUFFER_SHADING.frag").staticResource());
        debugVoxelShader = (Shader) resources.addResource(new ShaderCreationData(ShaderCreationData.LOCAL_SHADER + "DEBUG_VOXEL.vert", ShaderCreationData.LOCAL_SHADER + "DEBUG_VOXEL.frag"));
        brdfShader = (Shader) resources.addResource(new ShaderCreationData(ShaderCreationData.LOCAL_SHADER + "QUAD.vert", ShaderCreationData.LOCAL_SHADER + "BRDF_GEN.frag"));
        terrainShader = (Shader) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "TERRAIN.vert", LOCAL_SHADER + "TERRAIN.frag").staticResource());
        spriteShader = (Shader) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "SPRITE.vert", LOCAL_SHADER + "SPRITE.frag").staticResource());
        gBufferShader = (Shader) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "uber/G_BUFFER.vert", LOCAL_SHADER + "uber/G_BUFFER.frag").staticResource());
        toScreenShader = (Shader) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "QUAD.vert", LOCAL_SHADER + "TO_SCREEN.frag").staticResource());
        downscaleShader = (Shader) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "QUAD.vert", LOCAL_SHADER + "BILINEAR_DOWNSCALE.glsl").staticResource());
        bilateralBlurShader = (Shader) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "QUAD.vert", LOCAL_SHADER + "BILATERAL_BLUR.glsl").staticResource());
        bokehShader = (Shader) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "QUAD.vert", LOCAL_SHADER + "BOKEH.frag").staticResource());
        irradianceShader = (Shader) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "CUBEMAP.vert", LOCAL_SHADER + "IRRADIANCE_MAP.frag").staticResource());
        prefilteredShader = (Shader) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "CUBEMAP.vert", LOCAL_SHADER + "PREFILTERED_MAP.frag").staticResource());
        ssgiShader = (Shader) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "QUAD.vert", LOCAL_SHADER + "SSGI.frag").staticResource());
        mbShader = (Shader) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "QUAD.vert", LOCAL_SHADER + "MOTION_BLUR.frag").staticResource());
        ssaoShader = (Shader) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "QUAD.vert", LOCAL_SHADER + "SSAO.frag").staticResource());
        boxBlurShader = (Shader) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "QUAD.vert", LOCAL_SHADER + "BOX-BLUR.frag").staticResource());
        directShadowsShader = (Shader) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "SHADOWS.vert", LOCAL_SHADER + "DIRECTIONAL_SHADOWS.frag").staticResource());
        omniDirectShadowsShader = (Shader) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "SHADOWS.vert", LOCAL_SHADER + "OMNIDIRECTIONAL_SHADOWS.frag").staticResource());
        frameComposition = (Shader) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "QUAD.vert", LOCAL_SHADER + "FRAME_COMPOSITION.frag").staticResource());
        bloomShader = (Shader) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "QUAD.vert", LOCAL_SHADER + "BRIGHTNESS_FILTER.frag").staticResource());
        lensShader = (Shader) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "QUAD.vert", LOCAL_SHADER + "LENS_POST_PROCESSING.frag").staticResource());
        gaussianShader = (Shader) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "QUAD.vert", LOCAL_SHADER + "GAUSSIAN.frag").staticResource());
        upSamplingShader = (Shader) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "QUAD.vert", LOCAL_SHADER + "UPSAMPLE_TENT.glsl").staticResource());
        atmosphereShader = (Shader) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "QUAD.vert", LOCAL_SHADER + "ATMOSPHERE.frag").staticResource());
    }
}
