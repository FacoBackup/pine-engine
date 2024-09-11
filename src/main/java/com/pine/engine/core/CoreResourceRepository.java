package com.pine.engine.core;

import com.pine.common.Initializable;
import com.pine.engine.Engine;
import com.pine.engine.core.service.EngineInjectable;
import com.pine.engine.core.service.loader.ResourceLoaderService;
import com.pine.engine.core.service.loader.impl.info.MeshLoaderExtraInfo;
import com.pine.engine.core.service.loader.impl.response.MeshLoaderResponse;
import com.pine.engine.core.service.resource.ResourceService;
import com.pine.engine.core.service.resource.fbo.FBO;
import com.pine.engine.core.service.resource.fbo.FBOCreationData;
import com.pine.engine.core.service.resource.primitives.mesh.Mesh;
import com.pine.engine.core.service.resource.shader.Shader;
import com.pine.engine.core.service.resource.shader.ShaderCreationData;
import org.lwjgl.opengl.GL46;

import java.util.ArrayList;
import java.util.List;

public class CoreResourceRepository implements EngineInjectable, Initializable {
    private final Engine engine;

    public Mesh planeMesh;
    public Shader gridShader;
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

    public CoreResourceRepository(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void onInitialize() {
        RuntimeRepository runtime = engine.getRuntimeRepository();
        EngineConfiguration configuration = engine.getConfiguration();
        ResourceService resources = engine.getResourcesService();
        ResourceLoaderService resourceLoader = engine.getResourceLoaderService();

        var planeResponse = (MeshLoaderResponse) resourceLoader.load("plane.glb", true, new MeshLoaderExtraInfo().setSilentOperation(true));
        planeMesh = (Mesh) resources.getById(planeResponse.getMeshes().getFirst().id());

        initializeShaders(resources);
        initializeFBOs(runtime, resources, configuration);
    }

    private void initializeShaders(ResourceService resources) {
        spriteShader = (Shader) resources.addResource(new ShaderCreationData("shaders/SPRITE.vert", "shaders/SPRITE.frag", "sprite"));
        visibilityShader = (Shader) resources.addResource(new ShaderCreationData("shaders/V_BUFFER.vert", "shaders/V_BUFFER.frag", "visibility"));
        toScreenShader = (Shader) resources.addResource(new ShaderCreationData("shaders/QUAD.vert", "shaders/TO_SCREEN.frag", "toScreen"));
        downscaleShader = (Shader) resources.addResource(new ShaderCreationData("shaders/QUAD.vert", "shaders/BILINEAR_DOWNSCALE.glsl", "downscale"));
        bilateralBlurShader = (Shader) resources.addResource(new ShaderCreationData("shaders/QUAD.vert", "shaders/BILATERAL_BLUR.glsl", "bilateralBlur"));
        bokehShader = (Shader) resources.addResource(new ShaderCreationData("shaders/QUAD.vert", "shaders/BOKEH.frag", "bokeh"));
        irradianceShader = (Shader) resources.addResource(new ShaderCreationData("shaders/CUBEMAP.vert", "shaders/IRRADIANCE_MAP.frag", "irradiance"));
        prefilteredShader = (Shader) resources.addResource(new ShaderCreationData("shaders/CUBEMAP.vert", "shaders/PREFILTERED_MAP.frag", "prefiltered"));
        ssgiShader = (Shader) resources.addResource(new ShaderCreationData("shaders/QUAD.vert", "shaders/SSGI.frag", "ssgi"));
        mbShader = (Shader) resources.addResource(new ShaderCreationData("shaders/QUAD.vert", "shaders/MOTION_BLUR.frag", "mb"));
        ssaoShader = (Shader) resources.addResource(new ShaderCreationData("shaders/QUAD.vert", "shaders/SSAO.frag", "ssao"));
        boxBlurShader = (Shader) resources.addResource(new ShaderCreationData("shaders/QUAD.vert", "shaders/BOX-BLUR.frag", "boxBlur"));
        directShadowsShader = (Shader) resources.addResource(new ShaderCreationData("shaders/SHADOWS.vert", "shaders/DIRECTIONAL_SHADOWS.frag", "directShadows"));
        omniDirectShadowsShader = (Shader) resources.addResource(new ShaderCreationData("shaders/SHADOWS.vert", "shaders/OMNIDIRECTIONAL_SHADOWS.frag", "omniDirectShadows"));
        compositionShader = (Shader) resources.addResource(new ShaderCreationData("shaders/QUAD.vert", "shaders/FRAME_COMPOSITION.frag", "composition"));
        bloomShader = (Shader) resources.addResource(new ShaderCreationData("shaders/QUAD.vert", "shaders/BRIGHTNESS_FILTER.frag", "bloom"));
        lensShader = (Shader) resources.addResource(new ShaderCreationData("shaders/QUAD.vert", "shaders/LENS_POST_PROCESSING.frag", "lens"));
        gaussianShader = (Shader) resources.addResource(new ShaderCreationData("shaders/QUAD.vert", "shaders/GAUSSIAN.frag", "gaussian"));
        upSamplingShader = (Shader) resources.addResource(new ShaderCreationData("shaders/QUAD.vert", "shaders/UPSAMPLE_TENT.glsl", "upSampling"));
        atmosphereShader = (Shader) resources.addResource(new ShaderCreationData("shaders/QUAD.vert", "shaders/ATMOSPHERE.frag", "atmosphere"));
        gridShader = (Shader) resources.addResource(new ShaderCreationData("shaders/GRID.vert", "shaders/GRID.frag", "grid"));
    }

    private void initializeFBOs(RuntimeRepository runtime, ResourceService resources, EngineConfiguration configuration) {
        final int halfResW = runtime.displayW / 2;
        final int halfResH = runtime.displayH / 2;

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
        int w = runtime.displayW;
        int h = runtime.displayH;
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
