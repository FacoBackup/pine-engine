package com.pine.repository;

import com.pine.Engine;
import com.pine.PBean;
import com.pine.PInject;
import com.pine.service.loader.ResourceLoaderService;
import com.pine.service.loader.impl.info.MeshLoaderExtraInfo;
import com.pine.service.loader.impl.response.MeshLoaderResponse;
import com.pine.service.resource.ResourceService;
import com.pine.service.resource.compute.ComputeCreationData;
import com.pine.service.resource.compute.ComputeResource;
import com.pine.service.resource.fbo.FBO;
import com.pine.service.resource.fbo.FBOCreationData;
import com.pine.service.resource.primitives.GLSLType;
import com.pine.service.resource.primitives.mesh.MeshCreationData;
import com.pine.service.resource.primitives.mesh.MeshPrimitiveResource;
import com.pine.service.resource.shader.ShaderCreationData;
import com.pine.service.resource.shader.ShaderResource;
import com.pine.service.resource.ssbo.SSBOCreationData;
import com.pine.service.resource.ssbo.ShaderStorageBufferObject;
import com.pine.service.resource.ubo.UBOCreationData;
import com.pine.service.resource.ubo.UBOData;
import com.pine.service.resource.ubo.UniformBufferObject;
import com.pine.type.CoreUBOName;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static com.pine.Engine.MAX_LIGHTS;
import static com.pine.service.resource.shader.ShaderCreationData.LOCAL_SHADER;

@PBean
public class CoreResourceRepository {
    public static final int TRANSFORMATION_PER_ENTITY = 9;
    private static final int MAX_ENTITIES = 2000;
    private static final int BUFFER_SIZE = TRANSFORMATION_PER_ENTITY * MAX_ENTITIES;

    @PInject
    public Engine engine;
    @PInject
    public ResourceService resources;
    @PInject
    public ConfigurationRepository configuration;
    @PInject
    public ResourceLoaderService resourceLoader;

    public MeshPrimitiveResource planeMesh;
    public MeshPrimitiveResource quadMesh;
    public MeshPrimitiveResource cubeMesh;

    public ShaderResource spriteShader;
    public ShaderResource visibilityShader;
    public ShaderResource toScreenShader;
    public ShaderResource downscaleShader;
    public ShaderResource bilateralBlurShader;
    public ShaderResource bokehShader;
    public ShaderResource irradianceShader;
    public ShaderResource prefilteredShader;
    public ShaderResource ssgiShader;
    public ShaderResource mbShader;
    public ShaderResource ssaoShader;
    public ShaderResource boxBlurShader;
    public ShaderResource directShadowsShader;
    public ShaderResource omniDirectShadowsShader;
    public ShaderResource compositionShader;
    public ShaderResource bloomShader;
    public ShaderResource lensShader;
    public ShaderResource gaussianShader;
    public ShaderResource upSamplingShader;
    public ShaderResource atmosphereShader;
    public ShaderResource terrainShader;
    public ShaderResource demoShader;

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

    public ShaderStorageBufferObject transformationSSBO;

    public UniformBufferObject cameraViewUBO;
    public UniformBufferObject frameCompositionUBO;
    public UniformBufferObject lensPostProcessingUBO;
    public UniformBufferObject ssaoUBO;
    public UniformBufferObject uberUBO;
    public UniformBufferObject lightsUBO;
    public UniformBufferObject cameraProjectionUBO;

    public final FloatBuffer transformationSSBOState = MemoryUtil.memAllocFloat(BUFFER_SIZE);
    public final FloatBuffer cameraViewUBOState = MemoryUtil.memAllocFloat(52);
    public final FloatBuffer cameraProjectionUBOState = MemoryUtil.memAllocFloat(35);
    public final FloatBuffer frameCompositionUBOState = MemoryUtil.memAllocFloat(1);
    public final FloatBuffer lensPostProcessingUBOState = MemoryUtil.memAllocFloat(1);
    public final FloatBuffer ssaoUBOState = MemoryUtil.memAllocFloat(1);
    public final FloatBuffer uberUBOState = MemoryUtil.memAllocFloat(1);
    public final FloatBuffer lightsUBOState = MemoryUtil.memAllocFloat(MAX_LIGHTS * 16);
    public final FloatBuffer lightsUBOState2 = MemoryUtil.memAllocFloat(MAX_LIGHTS * 16);

    public ComputeResource transformationCompute;

    public void initialize() {
        var planeResponse = (MeshLoaderResponse) resourceLoader.load("plane.glb", true, new MeshLoaderExtraInfo().setSilentOperation(true));
        if (planeResponse != null) {
            planeMesh = (MeshPrimitiveResource) resources.getById(planeResponse.getMeshes().getFirst().id());
            resources.makeStatic(planeMesh);
        }

        var cubeResponse = (MeshLoaderResponse) resourceLoader.load("cube.glb", true, new MeshLoaderExtraInfo().setSilentOperation(true));
        if (cubeResponse != null) {
            cubeMesh = (MeshPrimitiveResource) resources.getById(cubeResponse.getMeshes().getFirst().id());
            resources.makeStatic(cubeMesh);
        }
        quadMesh = (MeshPrimitiveResource) resources.addResource(new MeshCreationData(
                new float[]{-1, -1, (float) -4.371138828673793e-8, 1, -1, (float) -4.371138828673793e-8, -1, 1, 4.371138828673793e-8F, 1, 1, 4.371138828673793e-8F},
                new int[]{0, 1, 3, 0, 3, 2},
                null,
                null
        ).staticResource());

        initializeFBOs();
        initializeUBOs();
        initializeSSBOs();
        initializeShaders();
        initializeCompute();
    }

    private void initializeCompute(){
        transformationCompute = (ComputeResource) resources.addResource(new ComputeCreationData(LOCAL_SHADER + "compute/TRANSFORMATION.glsl").staticResource());
    }

    private void initializeSSBOs() {
        for (int i = 0; i < BUFFER_SIZE; i++) {
            transformationSSBOState.put(i, 0);
        }
        transformationSSBO = (ShaderStorageBufferObject) resources.addResource(new SSBOCreationData(
                CoreUBOName.CAMERA_VIEW.getBlockName(),
                10,
                (long) BUFFER_SIZE * GLSLType.FLOAT.getSize()
        ));
    }

    private void initializeUBOs() {

        cameraViewUBO = (UniformBufferObject) resources.addResource(new UBOCreationData(
                CoreUBOName.CAMERA_VIEW.getBlockName(),
                new UBOData("viewProjection", GLSLType.MAT_4),
                new UBOData("viewMatrix", GLSLType.MAT_4),
                new UBOData("invViewMatrix", GLSLType.MAT_4),
                new UBOData("placement", GLSLType.VEC_4)).staticResource());

        cameraProjectionUBO = (UniformBufferObject) resources.addResource(new UBOCreationData(
                CoreUBOName.CAMERA_PROJECTION.getBlockName(),
                new UBOData("projectionMatrix", GLSLType.MAT_4),
                new UBOData("invProjectionMatrix", GLSLType.MAT_4),
                new UBOData("bufferResolution", GLSLType.VEC_2),
                new UBOData("logDepthFC", GLSLType.FLOAT),
                new UBOData("logC", GLSLType.FLOAT)).staticResource());

        frameCompositionUBO = (UniformBufferObject) resources.addResource(new UBOCreationData(
                CoreUBOName.FRAME_COMPOSITION.getBlockName(),
                new UBOData("inverseFilterTextureSize", GLSLType.VEC_2),
                new UBOData("useFXAA", GLSLType.BOOL),
                new UBOData("filmGrainEnabled", GLSLType.BOOL),
                new UBOData("FXAASpanMax", GLSLType.FLOAT),
                new UBOData("FXAAReduceMin", GLSLType.FLOAT),
                new UBOData("FXAAReduceMul", GLSLType.FLOAT),
                new UBOData("filmGrainStrength", GLSLType.FLOAT)).staticResource());

        lensPostProcessingUBO = (UniformBufferObject) resources.addResource(new UBOCreationData(
                CoreUBOName.LENS_PP.getBlockName(),
                new UBOData("textureSizeXDOF", GLSLType.FLOAT),
                new UBOData("textureSizeYDOF", GLSLType.FLOAT),
                new UBOData("distortionIntensity", GLSLType.FLOAT),
                new UBOData("chromaticAberrationIntensity", GLSLType.FLOAT),
                new UBOData("distortionEnabled", GLSLType.BOOL),
                new UBOData("chromaticAberrationEnabled", GLSLType.BOOL),
                new UBOData("bloomEnabled", GLSLType.BOOL),
                new UBOData("focusDistanceDOF", GLSLType.FLOAT),
                new UBOData("apertureDOF", GLSLType.FLOAT),
                new UBOData("focalLengthDOF", GLSLType.FLOAT),
                new UBOData("samplesDOF", GLSLType.FLOAT),
                new UBOData("vignetteEnabled", GLSLType.BOOL),
                new UBOData("vignetteStrength", GLSLType.FLOAT),
                new UBOData("gamma", GLSLType.FLOAT),
                new UBOData("exposure", GLSLType.FLOAT)
        ).staticResource());

        ssaoUBO = (UniformBufferObject) resources.addResource(new UBOCreationData(
                CoreUBOName.SSAO.getBlockName(),
                new UBOData("settings", GLSLType.VEC_4),
                new UBOData("samples", GLSLType.VEC_4, 64),
                new UBOData("noiseScale", GLSLType.VEC_2)
        ).staticResource());

        uberUBO = (UniformBufferObject) resources.addResource(new UBOCreationData(
                CoreUBOName.UBER.getBlockName(),
                new UBOData("shadowMapsQuantity", GLSLType.FLOAT),
                new UBOData("shadowMapResolution", GLSLType.FLOAT),
                new UBOData("lightQuantity", GLSLType.INT),
                new UBOData("SSRFalloff", GLSLType.FLOAT),
                new UBOData("stepSizeSSR", GLSLType.FLOAT),
                new UBOData("maxSSSDistance", GLSLType.FLOAT),
                new UBOData("SSSDepthThickness", GLSLType.FLOAT),
                new UBOData("SSSEdgeAttenuation", GLSLType.FLOAT),
                new UBOData("skylightSamples", GLSLType.FLOAT),
                new UBOData("SSSDepthDelta", GLSLType.FLOAT),
                new UBOData("SSAOFalloff", GLSLType.FLOAT),
                new UBOData("maxStepsSSR", GLSLType.INT),
                new UBOData("maxStepsSSS", GLSLType.INT),
                new UBOData("hasSkylight", GLSLType.BOOL),
                new UBOData("hasAmbientOcclusion", GLSLType.BOOL)
        ).staticResource());

        lightsUBO = (UniformBufferObject) resources.addResource(new UBOCreationData(
                CoreUBOName.LIGHTS.getBlockName(),
                new UBOData("lightPrimaryBuffer", GLSLType.MAT_4, MAX_LIGHTS),
                new UBOData("lightSecondaryBuffer", GLSLType.MAT_4, MAX_LIGHTS)
        ).staticResource());
    }

    private void initializeShaders() {
        demoShader = (ShaderResource) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "DEMO.vert", LOCAL_SHADER + "DEMO.frag").staticResource());
        terrainShader = (ShaderResource) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "TERRAIN.vert", LOCAL_SHADER + "TERRAIN.frag").staticResource());
        spriteShader = (ShaderResource) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "SPRITE.vert", LOCAL_SHADER + "SPRITE.frag").staticResource());
        visibilityShader = (ShaderResource) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "V_BUFFER.vert", LOCAL_SHADER + "V_BUFFER.frag").staticResource());
        toScreenShader = (ShaderResource) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "QUAD.vert", LOCAL_SHADER + "TO_SCREEN.frag").staticResource());
        downscaleShader = (ShaderResource) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "QUAD.vert", LOCAL_SHADER + "BILINEAR_DOWNSCALE.glsl").staticResource());
        bilateralBlurShader = (ShaderResource) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "QUAD.vert", LOCAL_SHADER + "BILATERAL_BLUR.glsl").staticResource());
        bokehShader = (ShaderResource) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "QUAD.vert", LOCAL_SHADER + "BOKEH.frag").staticResource());
        irradianceShader = (ShaderResource) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "CUBEMAP.vert", LOCAL_SHADER + "IRRADIANCE_MAP.frag").staticResource());
        prefilteredShader = (ShaderResource) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "CUBEMAP.vert", LOCAL_SHADER + "PREFILTERED_MAP.frag").staticResource());
        ssgiShader = (ShaderResource) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "QUAD.vert", LOCAL_SHADER + "SSGI.frag").staticResource());
        mbShader = (ShaderResource) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "QUAD.vert", LOCAL_SHADER + "MOTION_BLUR.frag").staticResource());
        ssaoShader = (ShaderResource) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "QUAD.vert", LOCAL_SHADER + "SSAO.frag").staticResource());
        boxBlurShader = (ShaderResource) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "QUAD.vert", LOCAL_SHADER + "BOX-BLUR.frag").staticResource());
        directShadowsShader = (ShaderResource) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "SHADOWS.vert", LOCAL_SHADER + "DIRECTIONAL_SHADOWS.frag").staticResource());
        omniDirectShadowsShader = (ShaderResource) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "SHADOWS.vert", LOCAL_SHADER + "OMNIDIRECTIONAL_SHADOWS.frag").staticResource());
        compositionShader = (ShaderResource) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "QUAD.vert", LOCAL_SHADER + "FRAME_COMPOSITION.frag").staticResource());
        bloomShader = (ShaderResource) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "QUAD.vert", LOCAL_SHADER + "BRIGHTNESS_FILTER.frag").staticResource());
        lensShader = (ShaderResource) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "QUAD.vert", LOCAL_SHADER + "LENS_POST_PROCESSING.frag").staticResource());
        gaussianShader = (ShaderResource) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "QUAD.vert", LOCAL_SHADER + "GAUSSIAN.frag").staticResource());
        upSamplingShader = (ShaderResource) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "QUAD.vert", LOCAL_SHADER + "UPSAMPLE_TENT.glsl").staticResource());
        atmosphereShader = (ShaderResource) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "QUAD.vert", LOCAL_SHADER + "ATMOSPHERE.frag").staticResource());
    }

    private void initializeFBOs() {
        final int halfResW = engine.getDisplayW() / 2;
        final int halfResH = engine.getDisplayH() / 2;

        visibility = (FBO) resources.addResource(new FBOCreationData(false, true)
                .addSampler(0, GL46.GL_RGBA32F, GL46.GL_RGBA, GL46.GL_FLOAT, false, false)
                .addSampler(1, GL46.GL_RGBA, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, false, false).staticResource());

        postProcessing1 = (FBO) resources.addResource(new FBOCreationData(false, false).addSampler().staticResource());
        postProcessing2 = (FBO) resources.addResource(new FBOCreationData(false, true).addSampler().staticResource());

        ssgi = (FBO) resources.addResource(new FBOCreationData(halfResW, halfResH).addSampler(0, GL46.GL_RGBA, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, true, false).staticResource());
        ssgiFallback = (FBO) resources.addResource(new FBOCreationData(halfResW, halfResH).addSampler(0, GL46.GL_RGBA, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, false, false).staticResource());

        ssao = (FBO) resources.addResource(new FBOCreationData(halfResW, halfResH).addSampler(0, GL46.GL_R8, GL46.GL_RED, GL46.GL_UNSIGNED_BYTE, true, false).staticResource());
        ssaoBlurred = (FBO) resources.addResource(new FBOCreationData(halfResW, halfResH).addSampler(0, GL46.GL_R8, GL46.GL_RED, GL46.GL_UNSIGNED_BYTE, true, false).staticResource());

        finalFrame = (FBO) resources.addResource(new FBOCreationData(false, false).addSampler().staticResource());

        int Q = 7;
        int w = engine.getDisplayW();
        int h = engine.getDisplayH();
        for (int i = 0; i < Q; i++) {
            w /= 2;
            h /= 2;
            downscaleBloom.add((FBO) resources.addResource(new FBOCreationData(w, h).addSampler(0, GL46.GL_RGBA, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, false, false).staticResource()));
        }
        for (int i = 0; i < (Q / 2 - 1); i++) {
            w *= 4;
            h *= 4;
            upscaleBloom.add((FBO) resources.addResource(new FBOCreationData(w, h).addSampler(0, GL46.GL_RGBA, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, false, false).staticResource()));
        }

        ssaoBlurredSampler = ssaoBlurred.getSamplers().getFirst();
        ssaoSampler = ssao.getSamplers().getFirst();
        ssgiSampler = ssgi.getSamplers().getFirst();
        ssgiFallbackSampler = ssgiFallback.getSamplers().getFirst();
        sceneDepthVelocity = visibility.getSamplers().getFirst();
        entityIDSampler = visibility.getSamplers().get(1);
        postProcessing1Sampler = postProcessing1.getSamplers().getFirst();
        postProcessing2Sampler = postProcessing2.getSamplers().getFirst();
        finalFrameSampler = finalFrame.getSamplers().getFirst();

        shadows = (FBO) resources.addResource(new FBOCreationData(configuration.shadowMapResolution, configuration.shadowMapResolution).setDepthTexture(true).staticResource());
        shadowsSampler = shadows.getDepthSampler();
    }
}
