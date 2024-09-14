package com.pine.engine.core.repository;

import com.pine.engine.Engine;
import com.pine.engine.core.EngineDependency;
import com.pine.engine.core.EngineInjectable;
import com.pine.engine.core.LateInitializable;
import com.pine.engine.core.service.loader.ResourceLoaderService;
import com.pine.engine.core.service.loader.impl.info.MeshLoaderExtraInfo;
import com.pine.engine.core.service.loader.impl.response.MeshLoaderResponse;
import com.pine.engine.core.service.resource.ResourceService;
import com.pine.engine.core.service.resource.fbo.FBO;
import com.pine.engine.core.service.resource.fbo.FBOCreationData;
import com.pine.engine.core.service.resource.primitives.GLSLType;
import com.pine.engine.core.service.resource.primitives.mesh.Mesh;
import com.pine.engine.core.service.resource.primitives.mesh.MeshCreationData;
import com.pine.engine.core.service.resource.shader.Shader;
import com.pine.engine.core.service.resource.ubo.UBO;
import com.pine.engine.core.service.resource.ubo.UBOCreationData;
import com.pine.engine.core.service.resource.ubo.UBOData;
import com.pine.engine.core.type.CoreUBOName;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static com.pine.engine.Engine.MAX_LIGHTS;

@EngineInjectable
public class CoreResourceRepository implements LateInitializable {
    @EngineDependency
    public Engine engine;
    @EngineDependency
    public ResourceService resources;
    @EngineDependency
    public ConfigurationRepository configuration;
    @EngineDependency
    public ResourceLoaderService resourceLoader;

    public Mesh planeMesh;
    public Mesh quadMesh;

    public Shader spriteShader;
    public Shader visibilityShader;
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
    public Shader compositionShader;
    public Shader bloomShader;
    public Shader lensShader;
    public Shader gaussianShader;
    public Shader upSamplingShader;
    public Shader atmosphereShader;

    public FBO finalFrame;
    public int finalFrameSampler;
    public FBO visibility;
    public int sceneDepthVelocity;
    public int entityIDSampler;
    public FBO postProcessing1;
    public int postProcessing1Sampler;
    public FBO postProcessing2;
    public int postProcessing2Sampler;
    public FBO ssgi;
    public int ssgiSampler;
    public FBO ssgiFallback;
    public int ssgiFallbackSampler;
    public FBO ssao;
    public int ssaoSampler;
    public FBO ssaoBlurred;
    public int ssaoBlurredSampler;
    public FBO shadows;
    public int shadowsSampler;
    public int noiseSampler; // TODO
    public final List<FBO> upscaleBloom = new ArrayList<>();
    public final List<FBO> downscaleBloom = new ArrayList<>();

    public UBO cameraViewUBO;
    public UBO frameCompositionUBO;
    public UBO lensPostProcessingUBO;
    public UBO ssaoUBO;
    public UBO uberUBO;
    public UBO lightsUBO;
    public UBO cameraProjectionUBO;

    public final FloatBuffer cameraViewUBOState = MemoryUtil.memAllocFloat(52);
    public final FloatBuffer frameCompositionUBOState = MemoryUtil.memAllocFloat(1);
    public final FloatBuffer lensPostProcessingUBOState = MemoryUtil.memAllocFloat(1);
    public final FloatBuffer ssaoUBOState = MemoryUtil.memAllocFloat(1);
    public final FloatBuffer uberUBOState = MemoryUtil.memAllocFloat(1);
    public final FloatBuffer lightsUBOState = MemoryUtil.memAllocFloat(MAX_LIGHTS * 16);
    public final FloatBuffer lightsUBOState2 = MemoryUtil.memAllocFloat(MAX_LIGHTS * 16);
    public final FloatBuffer cameraProjectionUBOState = MemoryUtil.memAllocFloat(35);

    @Override
    public void lateInitialize() {
        var planeResponse = (MeshLoaderResponse) resourceLoader.load("plane.glb", true, new MeshLoaderExtraInfo().setSilentOperation(true));
        planeMesh = (Mesh) resources.getById(planeResponse.getMeshes().getFirst().id());
        quadMesh = (Mesh) resources.addResource(new MeshCreationData(
                new float[]{-1, -1, (float) -4.371138828673793e-8, 1, -1, (float) -4.371138828673793e-8, -1, 1, 4.371138828673793e-8F, 1, 1, 4.371138828673793e-8F},
                new int[]{0, 1, 3, 0, 3, 2},
                null,
                null
        ));

        initializeShaders();
        initializeFBOs();
        initializeUBOs();
    }

    private void initializeUBOs() {

        cameraViewUBO = (UBO) resources.addResource(new UBOCreationData(
                CoreUBOName.CAMERA_VIEW.getBlockName(),
                UBOData.of("viewProjection", GLSLType.MAT_4),
                UBOData.of("viewMatrix", GLSLType.MAT_4),
                UBOData.of("invViewMatrix", GLSLType.MAT_4),
                UBOData.of("placement", GLSLType.VEC_4)));

        cameraProjectionUBO = (UBO) resources.addResource(new UBOCreationData(
                CoreUBOName.CAMERA_PROJECTION.getBlockName(),
                UBOData.of("projectionMatrix", GLSLType.MAT_4),
                UBOData.of("invProjectionMatrix", GLSLType.MAT_4),
                UBOData.of("bufferResolution", GLSLType.VEC_2),
                UBOData.of("logDepthFC", GLSLType.FLOAT),
                UBOData.of("logC", GLSLType.FLOAT)));

        frameCompositionUBO = (UBO) resources.addResource(new UBOCreationData(
                CoreUBOName.FRAME_COMPOSITION.getBlockName(),
                UBOData.of("inverseFilterTextureSize", GLSLType.VEC_2),
                UBOData.of("useFXAA", GLSLType.BOOL),
                UBOData.of("filmGrainEnabled", GLSLType.BOOL),
                UBOData.of("FXAASpanMax", GLSLType.FLOAT),
                UBOData.of("FXAAReduceMin", GLSLType.FLOAT),
                UBOData.of("FXAAReduceMul", GLSLType.FLOAT),
                UBOData.of("filmGrainStrength", GLSLType.FLOAT)));

        lensPostProcessingUBO = (UBO) resources.addResource(new UBOCreationData(
                CoreUBOName.LENS_PP.getBlockName(),
                UBOData.of("textureSizeXDOF", GLSLType.FLOAT),
                UBOData.of("textureSizeYDOF", GLSLType.FLOAT),
                UBOData.of("distortionIntensity", GLSLType.FLOAT),
                UBOData.of("chromaticAberrationIntensity", GLSLType.FLOAT),
                UBOData.of("distortionEnabled", GLSLType.BOOL),
                UBOData.of("chromaticAberrationEnabled", GLSLType.BOOL),
                UBOData.of("bloomEnabled", GLSLType.BOOL),
                UBOData.of("focusDistanceDOF", GLSLType.FLOAT),
                UBOData.of("apertureDOF", GLSLType.FLOAT),
                UBOData.of("focalLengthDOF", GLSLType.FLOAT),
                UBOData.of("samplesDOF", GLSLType.FLOAT),
                UBOData.of("vignetteEnabled", GLSLType.BOOL),
                UBOData.of("vignetteStrength", GLSLType.FLOAT),
                UBOData.of("gamma", GLSLType.FLOAT),
                UBOData.of("exposure", GLSLType.FLOAT)
        ));

        ssaoUBO = (UBO) resources.addResource(new UBOCreationData(
                CoreUBOName.SSAO.getBlockName(),
                UBOData.of("settings", GLSLType.VEC_4),
                UBOData.of("samples", GLSLType.VEC_4, 64),
                UBOData.of("noiseScale", GLSLType.VEC_2)
        ));

        uberUBO = (UBO) resources.addResource(new UBOCreationData(
                CoreUBOName.UBER.getBlockName(),
                UBOData.of("shadowMapsQuantity", GLSLType.FLOAT),
                UBOData.of("shadowMapResolution", GLSLType.FLOAT),
                UBOData.of("lightQuantity", GLSLType.INT),
                UBOData.of("SSRFalloff", GLSLType.FLOAT),
                UBOData.of("stepSizeSSR", GLSLType.FLOAT),
                UBOData.of("maxSSSDistance", GLSLType.FLOAT),
                UBOData.of("SSSDepthThickness", GLSLType.FLOAT),
                UBOData.of("SSSEdgeAttenuation", GLSLType.FLOAT),
                UBOData.of("skylightSamples", GLSLType.FLOAT),
                UBOData.of("SSSDepthDelta", GLSLType.FLOAT),
                UBOData.of("SSAOFalloff", GLSLType.FLOAT),
                UBOData.of("maxStepsSSR", GLSLType.INT),
                UBOData.of("maxStepsSSS", GLSLType.INT),
                UBOData.of("hasSkylight", GLSLType.BOOL),
                UBOData.of("hasAmbientOcclusion", GLSLType.BOOL)
        ));

        lightsUBO = (UBO) resources.addResource(new UBOCreationData(
                CoreUBOName.LIGHTS.getBlockName(),
                UBOData.of("lightPrimaryBuffer", GLSLType.MAT_4, MAX_LIGHTS),
                UBOData.of("lightSecondaryBuffer", GLSLType.MAT_4, MAX_LIGHTS)
        ));
    }

    private void initializeShaders() {
//        spriteShader = (Shader) resources.addResource(new ShaderCreationData("shaders/SPRITE.vert", "shaders/SPRITE.frag", "sprite"));
//        visibilityShader = (Shader) resources.addResource(new ShaderCreationData("shaders/V_BUFFER.vert", "shaders/V_BUFFER.frag", "visibility"));
//        toScreenShader = (Shader) resources.addResource(new ShaderCreationData("shaders/QUAD.vert", "shaders/TO_SCREEN.frag", "toScreen"));
//        downscaleShader = (Shader) resources.addResource(new ShaderCreationData("shaders/QUAD.vert", "shaders/BILINEAR_DOWNSCALE.glsl", "downscale"));
//        bilateralBlurShader = (Shader) resources.addResource(new ShaderCreationData("shaders/QUAD.vert", "shaders/BILATERAL_BLUR.glsl", "bilateralBlur"));
//        bokehShader = (Shader) resources.addResource(new ShaderCreationData("shaders/QUAD.vert", "shaders/BOKEH.frag", "bokeh"));
//        irradianceShader = (Shader) resources.addResource(new ShaderCreationData("shaders/CUBEMAP.vert", "shaders/IRRADIANCE_MAP.frag", "irradiance"));
//        prefilteredShader = (Shader) resources.addResource(new ShaderCreationData("shaders/CUBEMAP.vert", "shaders/PREFILTERED_MAP.frag", "prefiltered"));
//        ssgiShader = (Shader) resources.addResource(new ShaderCreationData("shaders/QUAD.vert", "shaders/SSGI.frag", "ssgi"));
//        mbShader = (Shader) resources.addResource(new ShaderCreationData("shaders/QUAD.vert", "shaders/MOTION_BLUR.frag", "mb"));
//        ssaoShader = (Shader) resources.addResource(new ShaderCreationData("shaders/QUAD.vert", "shaders/SSAO.frag", "ssao"));
//        boxBlurShader = (Shader) resources.addResource(new ShaderCreationData("shaders/QUAD.vert", "shaders/BOX-BLUR.frag", "boxBlur"));
//        directShadowsShader = (Shader) resources.addResource(new ShaderCreationData("shaders/SHADOWS.vert", "shaders/DIRECTIONAL_SHADOWS.frag", "directShadows"));
//        omniDirectShadowsShader = (Shader) resources.addResource(new ShaderCreationData("shaders/SHADOWS.vert", "shaders/OMNIDIRECTIONAL_SHADOWS.frag", "omniDirectShadows"));
//        compositionShader = (Shader) resources.addResource(new ShaderCreationData("shaders/QUAD.vert", "shaders/FRAME_COMPOSITION.frag", "composition"));
//        bloomShader = (Shader) resources.addResource(new ShaderCreationData("shaders/QUAD.vert", "shaders/BRIGHTNESS_FILTER.frag", "bloom"));
//        lensShader = (Shader) resources.addResource(new ShaderCreationData("shaders/QUAD.vert", "shaders/LENS_POST_PROCESSING.frag", "lens"));
//        gaussianShader = (Shader) resources.addResource(new ShaderCreationData("shaders/QUAD.vert", "shaders/GAUSSIAN.frag", "gaussian"));
//        upSamplingShader = (Shader) resources.addResource(new ShaderCreationData("shaders/QUAD.vert", "shaders/UPSAMPLE_TENT.glsl", "upSampling"));
//        atmosphereShader = (Shader) resources.addResource(new ShaderCreationData("shaders/QUAD.vert", "shaders/ATMOSPHERE.frag", "atmosphere"));
    }

    private void initializeFBOs() {
        final int halfResW = engine.displayW / 2;
        final int halfResH = engine.displayH / 2;

        visibility = (FBO) resources.addResource(new FBOCreationData(false, true)
                .addColor(0, GL46.GL_RGBA32F, GL46.GL_RGBA, GL46.GL_FLOAT, false, false)
                .addColor(1, GL46.GL_RGBA, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, false, false));

        postProcessing1 = (FBO) resources.addResource(new FBOCreationData(false, false).addColor());
        postProcessing2 = (FBO) resources.addResource(new FBOCreationData(false, true).addColor());

        ssgi = (FBO) resources.addResource(new FBOCreationData(halfResW, halfResH).addColor(0, GL46.GL_RGBA, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, true, false));
        ssgiFallback = (FBO) resources.addResource(new FBOCreationData(halfResW, halfResH).addColor(0, GL46.GL_RGBA, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, false, false));

        ssao = (FBO) resources.addResource(new FBOCreationData(halfResW, halfResH).addColor(0, GL46.GL_R8, GL46.GL_RED, GL46.GL_UNSIGNED_BYTE, true, false));
        ssaoBlurred = (FBO) resources.addResource(new FBOCreationData(halfResW, halfResH).addColor(0, GL46.GL_R8, GL46.GL_RED, GL46.GL_UNSIGNED_BYTE, true, false));

        finalFrame = (FBO) resources.addResource(new FBOCreationData(false, false).addColor());

        int Q = 7;
        int w = engine.displayW;
        int h = engine.displayH;
        for (int i = 0; i < Q; i++) {
            w /= 2;
            h /= 2;
            downscaleBloom.add((FBO) resources.addResource(new FBOCreationData(w, h).addColor(0, GL46.GL_RGBA, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, false, false)));
        }
        for (int i = 0; i < (Q / 2 - 1); i++) {
            w *= 4;
            h *= 4;
            upscaleBloom.add((FBO) resources.addResource(new FBOCreationData(w, h).addColor(0, GL46.GL_RGBA, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, false, false)));
        }

        ssaoBlurredSampler = ssaoBlurred.getColors().getFirst();
        ssaoSampler = ssao.getColors().getFirst();
        ssgiSampler = ssgi.getColors().getFirst();
        ssgiFallbackSampler = ssgiFallback.getColors().getFirst();
        sceneDepthVelocity = visibility.getColors().getFirst();
        entityIDSampler = visibility.getColors().get(1);
        postProcessing1Sampler = postProcessing1.getColors().getFirst();
        postProcessing2Sampler = postProcessing2.getColors().getFirst();
        finalFrameSampler = finalFrame.getColors().getFirst();

        shadows = (FBO) resources.addResource(new FBOCreationData(configuration.shadowMapResolution, configuration.shadowMapResolution).setDepthTexture(true));
        shadowsSampler = shadows.getDepthSampler();
    }
}
