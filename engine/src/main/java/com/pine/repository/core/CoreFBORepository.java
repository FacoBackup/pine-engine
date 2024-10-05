package com.pine.repository.core;

import com.pine.Engine;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.EngineSettingsRepository;
import com.pine.repository.RuntimeRepository;
import com.pine.service.resource.ResourceService;
import com.pine.service.resource.ShaderService;
import com.pine.service.resource.fbo.FBOCreationData;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.streaming.mesh.MeshService;
import org.lwjgl.opengl.GL46;

import java.util.ArrayList;
import java.util.List;

@PBean
public class CoreFBORepository implements CoreRepository {
    @PInject
    public Engine engine;
    @PInject
    public CoreMeshRepository primitiveRepository;
    @PInject
    public ShaderService shaderService;
    @PInject
    public MeshService meshService;
    @PInject
    public ResourceService resources;
    @PInject
    public EngineSettingsRepository configuration;
    @PInject
    public RuntimeRepository runtimeRepository;


    public FrameBufferObject gBuffer;
    public FrameBufferObject auxBuffer;
    public FrameBufferObject ssgi;
    public FrameBufferObject ssgiFallback;
    public FrameBufferObject ssao;
    public FrameBufferObject brdfFBO;
    public FrameBufferObject shadows;
    public FrameBufferObject ssaoBlurred;

    public int gBufferAlbedoEmissive;
    public int gBufferMetallicRoughnessAO;
    public int gBufferNormal;
    public int gBufferDepth;
    public int auxSampler;
    public int ssgiSampler;
    public int ssgiFallbackSampler;
    public int ssaoSampler;
    public int ssaoBlurredSampler;
    public int shadowsSampler;
    public int brdfSampler;

    public final List<FrameBufferObject> upscaleBloom = new ArrayList<>();
    public final List<FrameBufferObject> downscaleBloom = new ArrayList<>();
    public final List<FrameBufferObject> all = new ArrayList<>();

    @Override
    public void initialize() {

        final int halfResW = runtimeRepository.getDisplayW() / 2;
        final int halfResH = runtimeRepository.getDisplayH() / 2;

        gBuffer = (FrameBufferObject) resources.addResource(new FBOCreationData(false, true)
                .addSampler(0, GL46.GL_RGBA16F, GL46.GL_RGB, GL46.GL_FLOAT, false, false)
                .addSampler(1, GL46.GL_RGBA16F, GL46.GL_RGBA, GL46.GL_FLOAT, false, false)
                .addSampler(2, GL46.GL_RGBA16F, GL46.GL_RGB, GL46.GL_FLOAT, false, false)
                .addSampler(3, GL46.GL_R16F, GL46.GL_RED, GL46.GL_FLOAT, false, false)
                .staticResource());
        gBufferMetallicRoughnessAO = gBuffer.getSamplers().get(0);
        gBufferAlbedoEmissive = gBuffer.getSamplers().get(1);
        gBufferNormal = gBuffer.getSamplers().get(2);
        gBufferDepth = gBuffer.getSamplers().get(3);

        auxBuffer = (FrameBufferObject) resources.addResource(new FBOCreationData(false, true)
                .addSampler(0, GL46.GL_RGBA16F, GL46.GL_RGBA, GL46.GL_FLOAT, true, false)
                .staticResource());

        ssgi = (FrameBufferObject) resources.addResource(new FBOCreationData(halfResW, halfResH).addSampler(0, GL46.GL_RGBA, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, true, false).staticResource());
        ssgiFallback = (FrameBufferObject) resources.addResource(new FBOCreationData(halfResW, halfResH).addSampler(0, GL46.GL_RGBA, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, false, false).staticResource());

        ssao = (FrameBufferObject) resources.addResource(new FBOCreationData(halfResW, halfResH).addSampler(0, GL46.GL_R8, GL46.GL_RED, GL46.GL_UNSIGNED_BYTE, true, false).staticResource());
        ssaoBlurred = (FrameBufferObject) resources.addResource(new FBOCreationData(halfResW, halfResH).addSampler(0, GL46.GL_R8, GL46.GL_RED, GL46.GL_UNSIGNED_BYTE, true, false).staticResource());

        int Q = 7;
        int w = runtimeRepository.getDisplayW();
        int h = runtimeRepository.getDisplayH();
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
        auxSampler = auxBuffer.getSamplers().getFirst();

        shadows = (FrameBufferObject) resources.addResource(new FBOCreationData(configuration.shadowMapResolution, configuration.shadowMapResolution).setDepthTexture(true).staticResource());
        shadowsSampler = shadows.getDepthSampler();


        brdfFBO = (FrameBufferObject) resources.addResource(new FBOCreationData(512, 512).addSampler(0, GL46.GL_RG16F, GL46.GL_RG, GL46.GL_FLOAT, false, false).staticResource());
        brdfSampler = brdfFBO.getSamplers().getFirst();

        all.add(auxBuffer);
        all.add(ssgi);
        all.add(ssgiFallback);
        all.add(ssao);
        all.add(ssaoBlurred);
        all.add(gBuffer);
        all.add(shadows);
        all.addAll(upscaleBloom);
        all.addAll(downscaleBloom);
    }
}
