package com.pine.repository;

import com.pine.Engine;
import com.pine.PBean;
import com.pine.PInject;
import com.pine.service.loader.ResourceLoaderService;
import com.pine.service.loader.impl.info.MeshLoaderExtraInfo;
import com.pine.service.loader.impl.response.MeshLoaderResponse;
import com.pine.service.resource.ResourceService;
import com.pine.service.resource.compute.ComputeCreationData;
import com.pine.service.resource.compute.Compute;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.fbo.FBOCreationData;
import com.pine.service.resource.primitives.GLSLType;
import com.pine.service.resource.primitives.mesh.MeshCreationData;
import com.pine.service.resource.primitives.mesh.Primitive;
import com.pine.service.resource.shader.ShaderCreationData;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.ssbo.SSBOCreationData;
import com.pine.service.resource.ssbo.ShaderStorageBufferObject;
import com.pine.service.resource.ubo.UBOCreationData;
import com.pine.service.resource.ubo.UBOData;
import com.pine.service.resource.ubo.UniformBufferObject;
import com.pine.type.BlockPoint;
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

    public Primitive planeMesh;
    public Primitive quadMesh;
    public Primitive cubeMesh;

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
    public Shader terrainShader;
    public Shader demoShader;

    public FrameBufferObject finalFrame;
    public int finalFrameSampler;
    public FrameBufferObject visibility;
    public int sceneDepthVelocity;
    public int entityIDSampler;
    public FrameBufferObject postProcessing1;
    public int postProcessing1Sampler;
    public FrameBufferObject postProcessing2;
    public int postProcessing2Sampler;
    public FrameBufferObject ssgi;
    public int ssgiSampler;
    public FrameBufferObject ssgiFallback;
    public int ssgiFallbackSampler;
    public FrameBufferObject ssao;
    public int ssaoSampler;
    public FrameBufferObject ssaoBlurred;
    public int ssaoBlurredSampler;
    public FrameBufferObject shadows;
    public int shadowsSampler;
    public int noiseSampler; // TODO
    public final List<FrameBufferObject> upscaleBloom = new ArrayList<>();
    public final List<FrameBufferObject> downscaleBloom = new ArrayList<>();

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

    public Compute transformationCompute;

    public ShaderStorageBufferObject transformationSSBO;
    public ShaderStorageBufferObject modelSSBO;

    public void initialize() {
        var planeResponse = (MeshLoaderResponse) resourceLoader.load("plane.glb", true, new MeshLoaderExtraInfo().setSilentOperation(true));
        if (planeResponse != null) {
            planeMesh = (Primitive) resources.getById(planeResponse.getMeshes().getFirst().id());
            resources.makeStatic(planeMesh);
        }

        var cubeResponse = (MeshLoaderResponse) resourceLoader.load("cube.glb", true, new MeshLoaderExtraInfo().setSilentOperation(true));
        if (cubeResponse != null) {
            cubeMesh = (Primitive) resources.getById(cubeResponse.getMeshes().getFirst().id());
            resources.makeStatic(cubeMesh);
        }
        quadMesh = (Primitive) resources.addResource(new MeshCreationData(
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

    private void initializeCompute() {
        transformationCompute = (Compute) resources.addResource(new ComputeCreationData(LOCAL_SHADER + "compute/TRANSFORMATION.glsl").staticResource());
    }

    private void initializeSSBOs() {
        transformationSSBO = (ShaderStorageBufferObject) resources.addResource(new SSBOCreationData(
                10,
                (long) BUFFER_SIZE * GLSLType.FLOAT.getSize()
        ));
        modelSSBO = (ShaderStorageBufferObject) resources.addResource(new SSBOCreationData(
                11,
                (long) MAX_ENTITIES * GLSLType.MAT_4.getSize()
        ));
    }

    private void initializeUBOs() {

        cameraViewUBO = (UniformBufferObject) resources.addResource(new UBOCreationData(
                BlockPoint.CAMERA_VIEW.getBlockName(),
                new UBOData("viewProjection", GLSLType.MAT_4),
                new UBOData("viewMatrix", GLSLType.MAT_4),
                new UBOData("invViewMatrix", GLSLType.MAT_4),
                new UBOData("placement", GLSLType.VEC_4)).staticResource());

        cameraProjectionUBO = (UniformBufferObject) resources.addResource(new UBOCreationData(
                BlockPoint.CAMERA_PROJECTION.getBlockName(),
                new UBOData("projectionMatrix", GLSLType.MAT_4),
                new UBOData("invProjectionMatrix", GLSLType.MAT_4),
                new UBOData("bufferResolution", GLSLType.VEC_2),
                new UBOData("logDepthFC", GLSLType.FLOAT),
                new UBOData("logC", GLSLType.FLOAT)).staticResource());

        frameCompositionUBO = (UniformBufferObject) resources.addResource(new UBOCreationData(
                BlockPoint.FRAME_COMPOSITION.getBlockName(),
                new UBOData("inverseFilterTextureSize", GLSLType.VEC_2),
                new UBOData("useFXAA", GLSLType.BOOL),
                new UBOData("filmGrainEnabled", GLSLType.BOOL),
                new UBOData("FXAASpanMax", GLSLType.FLOAT),
                new UBOData("FXAAReduceMin", GLSLType.FLOAT),
                new UBOData("FXAAReduceMul", GLSLType.FLOAT),
                new UBOData("filmGrainStrength", GLSLType.FLOAT)).staticResource());

        lensPostProcessingUBO = (UniformBufferObject) resources.addResource(new UBOCreationData(
                BlockPoint.LENS_PP.getBlockName(),
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
                BlockPoint.SSAO.getBlockName(),
                new UBOData("settings", GLSLType.VEC_4),
                new UBOData("samples", GLSLType.VEC_4, 64),
                new UBOData("noiseScale", GLSLType.VEC_2)
        ).staticResource());

        uberUBO = (UniformBufferObject) resources.addResource(new UBOCreationData(
                BlockPoint.UBER.getBlockName(),
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
                BlockPoint.LIGHTS.getBlockName(),
                new UBOData("lightPrimaryBuffer", GLSLType.MAT_4, MAX_LIGHTS),
                new UBOData("lightSecondaryBuffer", GLSLType.MAT_4, MAX_LIGHTS)
        ).staticResource());
    }

    private void initializeShaders() {
        demoShader = (Shader) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "DEMO.vert", LOCAL_SHADER + "DEMO.frag").staticResource());
        terrainShader = (Shader) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "TERRAIN.vert", LOCAL_SHADER + "TERRAIN.frag").staticResource());
        spriteShader = (Shader) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "SPRITE.vert", LOCAL_SHADER + "SPRITE.frag").staticResource());
        visibilityShader = (Shader) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "V_BUFFER.vert", LOCAL_SHADER + "V_BUFFER.frag").staticResource());
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
        compositionShader = (Shader) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "QUAD.vert", LOCAL_SHADER + "FRAME_COMPOSITION.frag").staticResource());
        bloomShader = (Shader) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "QUAD.vert", LOCAL_SHADER + "BRIGHTNESS_FILTER.frag").staticResource());
        lensShader = (Shader) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "QUAD.vert", LOCAL_SHADER + "LENS_POST_PROCESSING.frag").staticResource());
        gaussianShader = (Shader) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "QUAD.vert", LOCAL_SHADER + "GAUSSIAN.frag").staticResource());
        upSamplingShader = (Shader) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "QUAD.vert", LOCAL_SHADER + "UPSAMPLE_TENT.glsl").staticResource());
        atmosphereShader = (Shader) resources.addResource(new ShaderCreationData(LOCAL_SHADER + "QUAD.vert", LOCAL_SHADER + "ATMOSPHERE.frag").staticResource());
    }

    private void initializeFBOs() {
        final int halfResW = engine.getDisplayW() / 2;
        final int halfResH = engine.getDisplayH() / 2;

        visibility = (FrameBufferObject) resources.addResource(new FBOCreationData(false, true)
                .addSampler(0, GL46.GL_RGBA32F, GL46.GL_RGBA, GL46.GL_FLOAT, false, false)
                .addSampler(1, GL46.GL_RGBA, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, false, false).staticResource());

        postProcessing1 = (FrameBufferObject) resources.addResource(new FBOCreationData(false, false).addSampler().staticResource());
        postProcessing2 = (FrameBufferObject) resources.addResource(new FBOCreationData(false, true).addSampler().staticResource());

        ssgi = (FrameBufferObject) resources.addResource(new FBOCreationData(halfResW, halfResH).addSampler(0, GL46.GL_RGBA, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, true, false).staticResource());
        ssgiFallback = (FrameBufferObject) resources.addResource(new FBOCreationData(halfResW, halfResH).addSampler(0, GL46.GL_RGBA, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, false, false).staticResource());

        ssao = (FrameBufferObject) resources.addResource(new FBOCreationData(halfResW, halfResH).addSampler(0, GL46.GL_R8, GL46.GL_RED, GL46.GL_UNSIGNED_BYTE, true, false).staticResource());
        ssaoBlurred = (FrameBufferObject) resources.addResource(new FBOCreationData(halfResW, halfResH).addSampler(0, GL46.GL_R8, GL46.GL_RED, GL46.GL_UNSIGNED_BYTE, true, false).staticResource());

        finalFrame = (FrameBufferObject) resources.addResource(new FBOCreationData(false, false).addSampler().staticResource());

        int Q = 7;
        int w = engine.getDisplayW();
        int h = engine.getDisplayH();
        for (int i = 0; i < Q; i++) {
            w /= 2;
            h /= 2;
            downscaleBloom.add((FrameBufferObject) resources.addResource(new FBOCreationData(w, h).addSampler(0, GL46.GL_RGBA, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, false, false).staticResource()));
        }
        for (int i = 0; i < (Q / 2 - 1); i++) {
            w *= 4;
            h *= 4;
            upscaleBloom.add((FrameBufferObject) resources.addResource(new FBOCreationData(w, h).addSampler(0, GL46.GL_RGBA, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, false, false).staticResource()));
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

        shadows = (FrameBufferObject) resources.addResource(new FBOCreationData(configuration.shadowMapResolution, configuration.shadowMapResolution).setDepthTexture(true).staticResource());
        shadowsSampler = shadows.getDepthSampler();
    }
}
