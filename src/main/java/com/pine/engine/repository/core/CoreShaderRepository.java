package com.pine.engine.repository.core;

import com.pine.common.Initializable;
import com.pine.common.injection.PBean;
import com.pine.common.injection.PInject;
import com.pine.engine.service.resource.shader.Shader;
import com.pine.engine.service.resource.shader.ShaderCreationData;
import com.pine.engine.service.resource.shader.ShaderService;

@PBean
public class CoreShaderRepository implements Initializable {
    // TOOLS
    public Shader outlineShader;
    public Shader gridShader;
    public Shader outlineBoxGenShader;
    public Shader paintGizmoCompute;
    public Shader paintGizmoRenderingShader;
    public Shader outlineGenShader;
    public Shader iconShader;
    // TOOLS

    public Shader spriteShader;
    public Shader gBufferShader;
    public Shader gBufferTerrainShader;
    public Shader gBufferDecalShader;
    public Shader copyQuadShader;
    public Shader gBufferFoliageShader;
    public Shader foliageCullingCompute;
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
    public Shader gBufferShading;
    public Shader brdfShader;
    public Shader voxelRaymarchingCompute;
    public Shader cloudDetailCompute;
    public Shader cloudShapeCompute;
    public Shader cloudsRaymarcher;
    public Shader compositingShader;
    public Shader noiseShader;
    public Shader shadowsTerrainShader;
    public Shader shadowsPrimitiveShader;

    @PInject
    public ShaderService shaderService;

    @Override
    public void onInitialize() {
        // TOOLS
        outlineShader = shaderService.create(new ShaderCreationData("QUAD.vert", "tool/OUTLINE.frag"));
        paintGizmoRenderingShader = shaderService.create(new ShaderCreationData("QUAD.vert", "tool/PAINT_GIZMO.frag"));
        outlineGenShader = shaderService.create(new ShaderCreationData("tool/OUTLINE_GEN.vert", "tool/OUTLINE_GEN.frag"));
        outlineBoxGenShader = shaderService.create(new ShaderCreationData("tool/OUTLINE_GEN.vert", "tool/OUTLINE_GEN_BOX.frag"));
        gridShader = shaderService.create(new ShaderCreationData("QUAD.vert", "tool/GRID.frag"));
        iconShader = shaderService.create(new ShaderCreationData("tool/ICON.vert", "tool/ICON.frag"));
        paintGizmoCompute = shaderService.create(new ShaderCreationData("compute/PAINT_GIZMO_COMPUTE.glsl"));


        compositingShader = shaderService.create(new ShaderCreationData("QUAD.vert", "COMPOSITE.frag"));
        noiseShader = shaderService.create(new ShaderCreationData("QUAD.vert", "NOISE.frag"));
        gBufferShading = shaderService.create(new ShaderCreationData("QUAD.vert", "uber/G_BUFFER_SHADING.frag"));
        brdfShader = shaderService.create(new ShaderCreationData("QUAD.vert", "BRDF_GEN.frag"));
        spriteShader = shaderService.create(new ShaderCreationData("SPRITE.vert", "SPRITE.frag"));
        gBufferShader = shaderService.create(new ShaderCreationData("uber/G_BUFFER.vert", "uber/G_BUFFER.frag"));
        shadowsPrimitiveShader = shaderService.create(new ShaderCreationData("shadows/PRIMITIVE.vert", "shadows/EMPTY.frag"));
        gBufferTerrainShader = shaderService.create(new ShaderCreationData("uber/G_BUFFER_TERRAIN.vert", "uber/G_BUFFER_TERRAIN.frag"));
        shadowsTerrainShader = shaderService.create(new ShaderCreationData("shadows/TERRAIN.vert", "shadows/EMPTY.frag"));
        gBufferFoliageShader = shaderService.create(new ShaderCreationData("uber/G_BUFFER_FOLIAGE.vert", "uber/G_BUFFER.frag"));
        foliageCullingCompute = shaderService.create(new ShaderCreationData("compute/FOLIAGE_CULLING_COMPUTE.glsl"));
        toScreenShader = shaderService.create(new ShaderCreationData("QUAD.vert", "TO_SCREEN.frag"));
        downscaleShader = shaderService.create(new ShaderCreationData("QUAD.vert", "BILINEAR_DOWNSCALE.glsl"));
        bilateralBlurShader = shaderService.create(new ShaderCreationData("QUAD.vert", "BILATERAL_BLUR.glsl"));
        bokehShader = shaderService.create(new ShaderCreationData("QUAD.vert", "BOKEH.frag"));
        irradianceShader = shaderService.create(new ShaderCreationData("CUBEMAP.vert", "IRRADIANCE_MAP.frag"));
        prefilteredShader = shaderService.create(new ShaderCreationData("CUBEMAP.vert", "PREFILTERED_MAP.frag"));
        ssgiShader = shaderService.create(new ShaderCreationData("QUAD.vert", "SSGI.frag"));
        mbShader = shaderService.create(new ShaderCreationData("QUAD.vert", "MOTION_BLUR.frag"));
        ssaoShader = shaderService.create(new ShaderCreationData("QUAD.vert", "SSAO.frag"));
        boxBlurShader = shaderService.create(new ShaderCreationData("QUAD.vert", "BOX-BLUR.frag"));
        directShadowsShader = shaderService.create(new ShaderCreationData("SHADOWS.vert", "DIRECTIONAL_SHADOWS.frag"));
        omniDirectShadowsShader = shaderService.create(new ShaderCreationData("SHADOWS.vert", "OMNIDIRECTIONAL_SHADOWS.frag"));
        frameComposition = shaderService.create(new ShaderCreationData("QUAD.vert", "FRAME_COMPOSITION.frag"));
        bloomShader = shaderService.create(new ShaderCreationData("QUAD.vert", "BRIGHTNESS_FILTER.frag"));
        postProcessing = shaderService.create(new ShaderCreationData("QUAD.vert", "LENS_POST_PROCESSING.frag"));
        gaussianShader = shaderService.create(new ShaderCreationData("QUAD.vert", "GAUSSIAN.frag"));
        upSamplingShader = shaderService.create(new ShaderCreationData("QUAD.vert", "UPSAMPLE_TENT.glsl"));
        voxelRaymarchingCompute = shaderService.create(new ShaderCreationData("compute/VOXEL_RAY_MARCHING_COMPUTE.glsl"));
        cloudDetailCompute = shaderService.create(new ShaderCreationData("compute/CLOUD_DETAIL_COMPUTE.glsl"));
        cloudShapeCompute = shaderService.create(new ShaderCreationData("compute/CLOUD_SHAPE_COMPUTE.glsl"));
        cloudsRaymarcher = shaderService.create(new ShaderCreationData("QUAD.vert", "ATMOSPHERE.frag"));
        gBufferDecalShader = shaderService.create(new ShaderCreationData("uber/G_BUFFER_DECAL.vert", "uber/G_BUFFER.frag"));
        copyQuadShader = shaderService.create(new ShaderCreationData("QUAD.vert", "QUAD_COPY.frag"));
    }
}
