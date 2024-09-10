package com.pine.engine.core;

import com.pine.common.Initializable;
import com.pine.engine.Engine;
import com.pine.engine.core.gl.FBO;
import org.lwjgl.opengl.GL46;

import java.util.ArrayList;
import java.util.List;

public class FramebufferRepository implements Initializable {
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

    private final EngineConfiguration configuration;
    private final RuntimeRepository runtime;

    public FramebufferRepository(Engine engine) {
        this.runtime = engine.getRuntimeRepository();
        this.configuration = engine.getConfiguration();
    }

    @Override
    public void onInitialize() {
        final int viewportW = runtime.windowW;
        final int viewportH = runtime.windowH;
        final int halfResW = viewportW / 2;
        final int halfResH = viewportH / 2;

        visibility = new FBO(viewportW, viewportH)
                .texture(0, GL46.GL_RGBA32F, GL46.GL_RGBA, GL46.GL_FLOAT, false, false)
                .texture(1, GL46.GL_RGBA, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, false, false)
                .depthTest();


        postProcessing1 = new FBO(viewportW, viewportH).texture();
        postProcessing2 = new FBO(viewportW, viewportH).texture().depthTest();

        ssgi = new FBO(halfResW, halfResH).texture(0, GL46.GL_RGBA, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, true, false);
        ssgiFallback = new FBO(halfResW, halfResH).texture(0, GL46.GL_RGBA, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, false, false);

        ssao = new FBO(halfResW, halfResH).texture(0, GL46.GL_R8, GL46.GL_RED, GL46.GL_UNSIGNED_BYTE, true, false);
        ssaoBlurred = new FBO(halfResW, halfResH).texture(0, GL46.GL_R8, GL46.GL_RED, GL46.GL_UNSIGNED_BYTE, true, false);
        finalFrame = new FBO(viewportW, viewportH).texture();


        int Q = 7;
        int w = viewportW;
        int h = viewportH;
        for (int i = 0; i < Q; i++) {
            w /= 2;
            h /= 2;
            downscaleBloom.add((new FBO(w, h)).texture(0, GL46.GL_RGBA, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, false, false));
        }
        for (int i = 0; i < (Q / 2 - 1); i++) {
            w *= 4;
            h *= 4;
            upscaleBloom.add((new FBO(w, h)).texture(0, GL46.GL_RGBA, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, false, false));
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

        shadows = new FBO(configuration.shadowMapResolution, configuration.shadowMapResolution).depthTexture();
        shadowsSampler = shadows.getDepthSampler();
    }
}
